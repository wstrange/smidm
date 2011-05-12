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
import com.my2do.idm.dao.{ResourceDAO, UserDAO, SyncIndexDAO}
import com.my2do.idm.objects.ResourceRef

/**
 *
 * Companion UserView - factory methods
 */

object UserView {
  def apply(accountName:String) = {
    val u = UserDAO.findByAccountName(accountName)
    new UserView(u.get)
  }
}

/**
  * This represents a dynamic runtime construction of the user and their accounts
  *@param user - User repo object
 * @param createMissingAccounts - if true any view expressions that refer to missing accounts will trigger
 *    the creation and assignment of the account. For example, view(ldapResource,"sn") = "test"
 *    would cause ldapResource to be created and assigned to the user (if it was missing).
 */
class UserView(var user:User,
               createMissingAccounts:Boolean = true) extends Logger with UserRoleView {

  // transient map of resource objects - keyed by the account index
  private var accountMap   = new HashMap[SyncIndex,ResourceObject]

  refreshView // on construction we populate the list of resource accounts

  /**
   * @param instance?? - A user could have more than one account on a resource - so
   * this return the nth instance. todo: Need to find a better solution..
   * @return The attribute value for this resoruce/attribute pair
   *
   */
  def get(resourceKey:String,attribute:String):Option[AnyRef] = {
    resourceObjectsForResourceKey(resourceKey).foreach { case (ai,ro) =>
      val a = ro.attributes.get(attribute)
      if( a.isDefined ) return a
    }
    None
  }

  def get(r:Resource,attribute:String):Option[AnyRef] = get(r.resourceKey,attribute)

  /**
   * Get the attribute assoicated with the resource
   */
  def apply(resource:Resource,attribute:String) = get(resource.resourceKey, attribute).get

  /**
   * Get the user extended attribute.
   */
  def apply(attrName:String) = user.attributes.get(attrName)

 /*
  def update(attribute:String,v:AnyRef):Unit = {
    accountMap.foreach{ case(ai,ro) =>
      if( ro.attributes.keySet.contains(attribute)) {
        ro.attributes.put(attribute,v)
        ai.isDirty = true
      }
    }
  }
  */

  def update(expression:String,v:AnyRef):Unit = {
    // use reflection to update values
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

  def resourceObjectsForResourceKey(resourceKey:String) = accountMap.filter{ case(ai,ro) => ai.resourceKey.equals(resourceKey)}


  def getResourceObjects(resource:Resource) = {
      val ro = resourceObjectsForResourceKey(resource.resourceKey)
      ro.values.toIndexedSeq
  }

  def accountIndexIterator =  SyncIndexDAO.findByOwnerId(user.id)

  /**
   * Read the User View state from the Mongo Repo
   *
   */
  def refreshView = {

    accountMap.clear
    refreshRoleView

    val accountIndexList = SyncIndexDAO.getSyncIndexList(user)

    accountIndexList.foreach{ ai =>

     ai.getResourceObject match {
        case Some(ro:ResourceObject) =>  accountMap.put(ai,ro)
        case None => error("Sync Index points to non existant resource object. AI=" + ai)
      }
    }

    debug("Refresh User View")
    printDebug()

    accountMap
  }

  /**
   * @return true if the user has the resource linked to their account
   */
  def hasResourceAccount(resource:Resource) = accountIndexForResource(resource).size > 0

  /**
   * @return the list of account indexes associated with this resource. Could be more than one
   *   if the user has multiple accounts on the same resource
   */
  def accountIndexForResource(resource:Resource) = {
     accountMap.keys.filter( ai => ai.resourceKey.equals(resource.resourceKey ))
  }

  def printDebug() = {
    debug("User=" + user)
    accountMap.foreach{ case (ai,ro) => debug("\t" + ai); debug("\t" + ro)}
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
          val ro = resource.rule.newResourceObject(user)
          //val ai = SyncIndexDAO.addResourceObject(user,resource,dbo)
          val ai = SyncIndex(Some(user.id),resource.config.instanceKey,ro.accountName)
          accountMap.put(ai,ro)
          ai.isDirty = true
          if( directAssignment)
            user = user.copy( directlyAssignedResources = (resource.resourceKey :: user.directlyAssignedResources))
     }
  }

  /**
   * Remove a directly assigned resource - will trigger deletion of all resource accounts for this user!
   */
  def removeResource(resource:Resource):Unit = {
    if( ! user.isResourceDirectlyAssigned(resource)) {
      error("Can't unassign resource as it is not directly assigned. R=" + resource)
      return
    }
    if( user.isResourceRoleAssigned(resource)) {
      info("Resource will be unassigned - but account cant be deleted as it assigned through a role")
      unassignResource(resource)
      return
    }

    accountIndexForResource(resource).foreach{ ai =>
      info("Unassigning ai=" + ai)
      ai.delete = true
      ai.needsSync = true
      ai.isDirty = true
    }
    unassignResource(resource)
  }

  private def unassignResource(r:Resource) = {
    user = user.copy( directlyAssignedResources = user.directlyAssignedResources.filterNot( x => x.equals(r.resourceKey)))
  }

  /**
   *  flush the view out to MongoDB
   *  Only the dirty account index elements and resource objects are flushed
   *
   */

  def flush(flushUserObject:Boolean = true) = {
    if( flushUserObject) {
      this.flushRole()
      UserDAO.save(user)


    }

    accountMap.foreach{case (ai,ro) =>
      // if the linked resource object is dirty flag it for recon

      // todo: Test
      val rx = ResourceRef(ai.resourceKey,ro.accountName,ro.objectClass,null)

      ResourceRefDAO.save(rx)

      val l = ResourceRefDAO.childObjects.findByParentId(rx.id).toList
      debug("Child list=" + l)

      if( ro.isDirty)  {
        ai.needsSync = true
        ai.isDirty = true
      }
      if( ai.isDirty )  {
        SyncIndexDAO.save(ai)
        ResourceDAO(ai.resourceKey).save(ro)
      }
    }
  }
}
