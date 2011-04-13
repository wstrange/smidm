package com.my2do.idm.mongo

import com.mongodb.casbah.MongoConnection
import org.identityconnectors.framework.common.objects.ObjectClass
import com.my2do.idm.connector.ConnectorConfig
import com.mongodb.casbah.commons.MongoDBObject

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

  def collectionForConnector(config:ConnectorConfig) = {
    val c = db("Account_" + config.instanceName)
    // account names should be unique...
    if( c.isEmpty )
      c.ensureIndex( MongoDBObject(MongoUtil.NAME_ATTRIBUTE_STRING -> "1"), "nameIndex", true)
    c
  }

  /**
   * The __NAME__ is an artifact of the ICF connector framework. It is a special attribute name used to
   * identity the account name. The account name is the unique (primary key) of the account. For example,
   * uid=test,ou=People,dc=foo,dc=com
   */
  val NAME_ATTRIBUTE_STRING = "__NAME__"


  /**
   * Create a name attribute expression that we can use to search for an resource account by name
   *
   */
  def makeNameAttribute(name:String) = MongoDBObject( NAME_ATTRIBUTE_STRING -> name)

  def createIndexes = {
    // employeeId is indexed- but not unique - this is due to Mongos null handling
    userCollection.ensureIndex(MongoDBObject("employeeId" -> "1"), "employeeId", false)
    // accountName is indexed and unique
    userCollection.ensureIndex(MongoDBObject("accountName" -> "1"), "account", true)

    // compound index to lookup accounts by resource name and accountName
    accountIndexCollection.ensureIndex(MongoDBObject("resourceName" -> "1", "accountName" -> "1"), "accountIndex", true)
  }

  def dropAndCreateDB = {
      MongoUtil.db.dropDatabase
      MongoUtil.createIndexes
  }

}