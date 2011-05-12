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
import com.my2do.idm.resource.Resource
import net.liftweb.common.Logger
/**
 *
 * User: warren
 * Date: 4/2/11
 * Time: 4:20 PM
 *
 */

object SyncIndexDAO extends SalatDAO[SyncIndex, ObjectId] (collection = MongoUtil.syncIndexCollection) with Logger {

  def findByOwnerId(obj: ObjectId, objectClass:ObjectClass = ObjectClass.account) =
    this.find(MongoDBObject("ownerId" -> obj, "objectClass.name" -> objectClass.name))

  def findByAccountName(collectionName: String, accountName: String) =
    this.find(MongoDBObject("resourceKey" -> collectionName, "accountName" -> accountName))

  def findByResourceNeedsSynced(resource:Resource, objectClass:ObjectClass) =  {
    val key =  MongoDBObject("resourceKey" -> resource.resourceKey, "needsSync" ->true,
            "objectClass.name" -> objectClass.name)
    debug("Find by key=" + key)
    this.find(key)
  }


  /**
   * Return the list of SyncIndex linked to the user
   * @return a Scala List of the SyncIndex linked to the user
   */
  def getSyncIndexList(user: User): List[SyncIndex] = {
    val accountIter = SyncIndexDAO.findByOwnerId(user.id)
    val list = new ListBuffer[SyncIndex]()
    while (accountIter.hasNext) list += accountIter.next
    list toList
  }

   def addResourceObject(user:User, resource: Resource, ro:ResourceObject):SyncIndex = {
    // insert the resource acccount
    resource.dao.save(ro)
    // update the account index
    val ai: SyncIndex = SyncIndex(Some(user.id), resource.resourceKey, ro.accountName, needsSync = true)
    val id = SyncIndexDAO.insert(ai)
    if (id.isEmpty)
      error("Could not insert account index record=" + ai)
    // update the current account map with the newly inserted account
    ai
  }

}





