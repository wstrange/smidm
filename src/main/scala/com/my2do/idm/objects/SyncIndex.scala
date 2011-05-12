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

import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import collection.mutable.HashMap
import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.mongo.MongoUtil
import com.my2do.idm.resource.Resource
import com.my2do.idm.dao.{ResourceDAO, SyncIndexDAO}

/**
  *
  * SyncIndex records the association of resource objects to a user, and the
  * state of the required synchronization  (i.e. does the repo  need to be synced to resource)
  *
  * This can be used for Accounts and Group objects. Group objects may not be owned.
  *
  * @param ownerId - the owner of this resource object
  * @param resourceKey - the repo collection the object belongs to
  * @param accountName - the unique account name of the linked resource object (e.g. uid=fred,dc=example,dc=com)
  * @param needsSync - true if the resoruce object needs to be flushed to the underlying resource
  * @param delete - true if the resource object is scheduled for deletion from the underlying resource
  * @param objectClass - the ICF String value of the object class (e.g. ACCOUNT or GROUP). The type of object this index refers to
  * @param lastSync - last sync time of the object
  * @param id - the identity of this entry. May not be required?
  *
  *
  */

case class SyncIndex(var ownerId: Option[ObjectId] = None,
                     resourceKey: String,
                     accountName: String = null,
                     var needsSync: Boolean = true,
                     var delete: Boolean = false,
                     var objectClass: ObjectClass = ObjectClass.account,
                     var lastSync: Long = System.currentTimeMillis,
                     @Key("_id") var id: ObjectId = new ObjectId) {

  /**
    *  Flag to indicate the index and the resource object need to be flushed
    *  By default, newly created instances are dirty
    *
    */

  var isDirty: Boolean = true


  def getResourceObject():Option[ResourceObject] = {
    val dao = ResourceDAO(resourceKey)
    dao.findByName(accountName,objectClass)
  }
}


