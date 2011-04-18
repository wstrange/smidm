/*
 * Copyright (c) 2011 - Warren Strange
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.my2do.idm.objects

import collection.mutable.HashMap

import com.mongodb.casbah.Imports._
import com.my2do.idm.resource.Resource
import com.my2do.idm.mongo.MongoUtil
import net.liftweb.common.Logger
import com.my2do.idm.dao.{UserDAO, ResourceDAO, AccountIndexDAO}

/**
 *
 * This represents a dynamic runtime construction of the user and their accounts
 *
 *
 * User: warren
 * Date: 4/13/11
 * Time: 12:16 PM
 *
 * @param user - User repo object
 * @param createMissingAccounts - if true any view expressions that refer to missing accounts will trigger
 *    the creation and assignment of the account. For example, view(ldapResource,"sn") = "test"
 *    would cause ldapResource to be created and assigned to the user (if it was missing).
 * 
 */

object UserView {
  def apply(id:ObjectId) = {
    val u = UserDAO.findOneByID(id).get
    new UserView(u)
  }
}

class UserView(val user:User,
               createMissingAccounts:Boolean = true) extends Logger with UserRoleView {

  // transient map of account objects - keyed by (resource,account?)
  var accountMap   = new HashMap[AccountIndex,MongoDBObject]

  refreshView // on construction we populate the list of resource accounts

  /**
   * @param instance?? - A user could have more than one account on a resource - so
   * this return the nth instance. todo: Need to find a better solution..
   * @return The attribute value for this resoruce/attribute pair
   *
   */
  def get(resourceKey:String,attribute:String):Option[AnyRef] = {
    resourceObjectsForResourceKey(resourceKey:String).foreach {
      case (ai,dbo) =>  val a = dbo.get(attribute)
            if( a.isDefined ) return a
    }
    None
  }

  def apply(resource:Resource,attribute:String) = get(resource.resourceKey, attribute).get

  //def apply(resource:Resource,attribute:String):Option[AnyRef] = apply(resource.resourceKey,attribute)

  def resourceObjectsForResourceKey(resourceKey:String) = accountMap.filter{ case(ai,dbo) => ai.resourceKey.equals(resourceKey)}

  def update(attribute:String,v:AnyRef):Unit = {
    accountMap.foreach{ case(ai,dbo) =>
      if( dbo.containsField(attribute)) {
        dbo.put(attribute,v)
        ai.isDirty = true
      }
    }
  }

  def update(resourceString:String, attrName:String, v:AnyRef):Unit = {
    val accounts = accountMap.keys.filter( ai => ai.resourceKey.equals(resourceString))
    accounts.foreach{ ai =>
      accountMap(ai).put(attrName, v)
      ai.isDirty = true
    }
  }

  def update(resource:Resource,attrName:String,v:AnyRef):Unit = {
    if( createMissingAccounts )
      ensureHasResource(resource)
    update(resource.instanceName,attrName,v)
  }

  // todo: Remove
  private def normalizeAttribute(a:AnyRef):AnyRef = {
    a match {
      case l:List[AnyRef] =>  MongoDBList(l)
      case _  => a
    }
  }

  def accountIndexIterator =  AccountIndexDAO.findByUserId(user.id)

  def refreshView = {
    accountMap.clear
    refreshRoleView

    val accountIndexList = AccountIndexDAO.getAccountIndexList(user)

    accountIndexList.foreach{ ai =>
      ResourceDAO.getResourceObject(ai) match {
        case Some(d:DBObject) =>  accountMap.put(ai,d)
        case None => error("Account Index points to non existant resource object. AI=" + ai)
      }
    }
    accountMap
  }

  def hasResourceAccount(resource:Resource) = {
   accountIndexForResource(resource).size > 0
  }

  def accountIndexForResource(resource:Resource) = {
     accountMap.keys.filter( ai => ai.resourceKey.equals(resource.resourceKey ))
  }

  def printAccounts() = {
    accountMap.foreach{ case (ai,dbo) => println("Account " + ai + " vals=" + dbo)}
  }

  /**
   * Check to make sure the user has a resource account. Add it if required
   *
   * @param resource - resource to add to this user profile
   * @param directAssignment if true - this assignment is direct (not indirect via a role)
   */
  def ensureHasResource(resource:Resource,directAssignment:Boolean = true) = {
     if (! hasResourceAccount(resource)) {
          // add an ldap account
          val dbo = resource.rule.newResourceObject(user)
          //val ai = AccountIndexDAO.addResourceObject(user,resource,dbo)
          val ai = AccountIndex(Some(user.id),resource.config.instanceKey,MongoUtil.accountName(dbo))
          accountMap.put(ai,dbo)
          ai.isDirty = true
          if( directAssignment)
            user.directlyAssignedResources = resource.resourceKey :: user.directlyAssignedResources

     }
  }

  /**
   * Remove a directly assigned resource - will trigger deletion of all resoruce accounts for this user!
   */
  def removeResource(resource:Resource):Unit = {

    if( ! user.isResourceDirectlyAssigned(resource)) {
      error("Can't unassign resource as it is not directly assigned. R=" + resource)
      return
    }
    if( user.isResourceRoleAssigned(resource)) {
      info("Resource will be unassigned - but account cant be deleted as it assigned through a role")
      user.unassignResource(resource)
      return
    }

    accountIndexForResource(resource).foreach{ ai =>
      info("Unassigning ai=" + ai)
      ai.delete = true
      ai.needsSync = true
      ai.isDirty = true
    }
    user.unassignResource(resource)
  }

  /**
   *  flush the view out to MongoDB
   *  Only the dirty elements are flushed?
   *
   *  Note that roles themselves are not managed by this View
   */

  def flush(flushUserObject:Boolean = true) = {
    if( flushUserObject)
      UserDAO.save(user)

    accountMap.foreach{case (ai,dbo) =>
      if( ai.isDirty)  {
        AccountIndexDAO.save(ai)
        ResourceDAO.saveResourceObject(ai.resourceKey,dbo)
      }
    }
  }
}
