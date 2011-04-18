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

package com.my2do.idm.dao

import com.my2do.idm.mongo.MongoUtil
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import dao.SalatDAO
import com.my2do.idm.objects._
import collection.mutable.ListBuffer
import net.liftweb.common.Logger
import com.my2do.idm.resource.Resource
import net.liftweb.common.Logger

/**
 *
 * User: warren
 * Date: 4/2/11
 * Time: 4:20 PM
 *
 */

object AccountIndexDAO extends SalatDAO[AccountIndex, ObjectId] {
  val _grater = grater[AccountIndex]
  val collection = MongoUtil.accountIndexCollection

  def findByUserId(obj: ObjectId) = this.find(MongoDBObject("userId" -> obj))

  def findByAccountName(collectionName: String, accountName: String) =
    this.find(MongoDBObject("resourceKey" -> collectionName, "accountName" -> accountName))

  def findByResourceNeedSynced(resource:Resource) =
    this.find(MongoDBObject("resourceKey" -> resource.resourceKey, "needsSync" ->true))


  /**
   * Return the list of AccountIndex linked to the user
   * @return a Scala List of the AccountIndex linked to the user
   */
  def getAccountIndexList(user: User): List[AccountIndex] = {
    val accountIter = AccountIndexDAO.findByUserId(user.id)
    val list = new ListBuffer[AccountIndex]()
    while (accountIter.hasNext) list += accountIter.next
    list toList
  }

   def addResourceObject(user:User, resource: Resource, dbo: DBObject):AccountIndex = {
    // insert the resource acccount
    val collection = MongoUtil.collectionForResource(resource)
    val nameAttr = ResourceDAO.insertAccountObject(collection, dbo)
    // update the account index
    val ai: AccountIndex = AccountIndex(Some(user.id), resource.instanceName, nameAttr.get, needsSync = true)
    val id = AccountIndexDAO.insert(ai)
    if (id.isEmpty)
      error("Could not insert account index record=" + ai)
    // update the current account map with the newly inserted account
    ai
  }

}





