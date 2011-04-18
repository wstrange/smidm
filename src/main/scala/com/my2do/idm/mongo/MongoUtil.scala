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

package com.my2do.idm.mongo

import com.mongodb.casbah.MongoConnection
import org.identityconnectors.framework.common.objects.ObjectClass
import com.my2do.idm.connector.ConnectorConfig
import com.mongodb.casbah.commons.MongoDBObject
import com.my2do.idm.resource.Resource
import com.mongodb.DBObject

/**
 * 
 * User: warren
 * Date: 4/1/11
 * Time: 10:12 PM
 * 
 */

object  MongoUtil  {

  val mongo = MongoConnection()

  /**
   * @return the default database
   */
  val db =   mongo("test")

  val userCollection = db("users")
  val accountIndexCollection = db("accounts")
  val roleCollection = db("roles")


  def collectionForResourceKey(name:String) = {

     val c = db("Resource" + name)
    // account names should be unique...
    if( c.isEmpty )
      c.ensureIndex( MongoDBObject(MongoUtil.NAME_ATTRIBUTE_STRING -> "1"), "nameIndex", true)
    c
  }

  def collectionForResource(resource:Resource) = collectionForResourceKey(resource.config.instanceKey)

  /**
   * The __NAME__ is an artifact of the ICF connector framework. It is a special attribute name used to
   * identity the account name. The account name is the unique (primary key) of the account. For example,
   * uid=test,ou=People,dc=foo,dc=com
   */
  val NAME_ATTRIBUTE_STRING = "__NAME__"
  val UID_ATTRIBUTE_STRING = "__UID__"


  /**
   * Create a name attribute expression that we can use to search for an resource account by name
   *
   */
  def makeNameAttribute(name:String) = MongoDBObject( NAME_ATTRIBUTE_STRING -> name)
  def makeUidAttribute(uid:String) = MongoDBObject(UID_ATTRIBUTE_STRING -> uid)

  /**
   * @return the accountName attribute from given object
   */
  def accountName(dbo:DBObject):String = dbo.get(NAME_ATTRIBUTE_STRING).asInstanceOf[String]

  def uid(dbo:DBObject):String = dbo.get(UID_ATTRIBUTE_STRING).asInstanceOf[String]

  def createIndexes = {
    // employeeId is indexed- but not unique - this is due to Mongos null handling
    userCollection.ensureIndex(MongoDBObject("employeeId" -> "1"), "employeeId", false)
    // accountName is indexed and unique
    userCollection.ensureIndex(MongoDBObject("accountName" -> "1"), "account", true)

    // compound index to lookup accounts by resource name and accountName
    accountIndexCollection.ensureIndex(MongoDBObject("resourceKey" -> "1", "accountName" -> "1"), "accountIndex", true)
  }

  def dropAndCreateDB = {
      MongoUtil.db.dropDatabase
      MongoUtil.createIndexes
  }

}