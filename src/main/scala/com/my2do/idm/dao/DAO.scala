package com.my2do.idm.dao

import com.my2do.idm.mongo.MongoUtil
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import dao.SalatDAO
import com.my2do.idm.objects.{AccountIndex, User}
import collection.mutable.ListBuffer
import com.my2do.idm.connector.ConnectorConfig
import config.LDAP_Prod
import net.liftweb.common.Logger
import org.junit.Assert._

/**
 * 
 * User: warren
 * Date: 4/2/11
 * Time: 4:20 PM
 * 
 */

object UserDAO extends SalatDAO[User,ObjectId] with Logger {
  val _grater = grater[User]
  val collection = MongoUtil.userCollection

  def findByEmployeeId(id:String): Option[User] = this.findOne(MongoDBObject("employeeId" -> id))
  def findByAccountName(name:String):Option[User] = this.findOne( MongoUtil.makeNameAttribute(name))

  def addAccount(u:User,connector:ConnectorConfig,dbo:DBObject) = {
    // insert the resource acccount
    val collection = MongoUtil.collectionForConnector(connector)
    val nameAttr = ResourceDAO.insertAccountObject(collection,dbo)
    // update the account index
    val ai:AccountIndex = AccountIndex(Some(u.id), collection.getName, nameAttr.get, needsSync = true)
    val id = AccountIndexDAO.insert(ai)
    if( id.isEmpty )
      error("Could not insert account index record=" + ai)
    // update the current account map with the newly inserted account
    u.accountMap.put(ai,dbo)
  }

}

object AccountIndexDAO extends SalatDAO[AccountIndex,ObjectId] {
  val _grater = grater[AccountIndex]
  val collection = MongoUtil.accountIndexCollection

  def findByUserId(obj:ObjectId) = this.find( MongoDBObject("userId" ->  obj))

  def findByAccountName(collectionName:String,accountName:String) =
    this.find( MongoDBObject("resourceName" -> collectionName, "accountName" -> accountName))


  /**
   * Return the list of AccountIndex linked to the user
   * @return a Scala List of the AccountIndex linked to the user
   */
  def getAccountIndexList(user:User):List[AccountIndex] = {
    val accountIter = AccountIndexDAO.findByUserId(user.id)
    val  list = new ListBuffer[AccountIndex]()
    while(accountIter.hasNext) list += accountIter.next
    list toList
  }
}

object ResourceDAO extends Logger {

  def getAccountObject(ai:AccountIndex):Option[DBObject]= {
      val collection = MongoUtil.db(ai.resourceName)
      collection.findOne(MongoUtil.makeNameAttribute(ai.accountName))
  }

  /**
   * Insert an account object into a resource collection.
   *
   * todo: Should we check for duplciates?
   *
   */
  def insertAccountObject(collection:MongoCollection, dbo:DBObject):Option[String] = {
    val nameAttr= dbo.getAs[String](MongoUtil.NAME_ATTRIBUTE_STRING)

    if( nameAttr.isEmpty ) {
      error("Missing manadatory account name attribute for object " + dbo)
    }
    val result = collection.insert(dbo)
    if( result.getError != null )  {
      throw new RuntimeException("Could not insert into collection: error=" + result.getError)
    }
    nameAttr
  }
}
