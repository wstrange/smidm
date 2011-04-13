package com.my2do.idm.objects

import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import collection.mutable.HashMap
import com.my2do.idm.dao.{ResourceDAO, AccountIndexDAO}
import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.mongo.MongoUtil

/**
 * 
 * User: warren
 * Date: 4/2/11
 * Time: 3:07 PM
 * 
 */

case class User(accountName:String,
                var firstName:String, var lastName:String,
                var employeeId:String = null, var department:String = "",
                var email:String = "",
                @Key("_id")id:ObjectId = new ObjectId()) {


  def accountIndexIterator =  AccountIndexDAO.findByUserId(id)


  // transient map of account objects - keyed by (resource,account?)
  val accountMap = new HashMap[AccountIndex,DBObject]

  def fetchLinkedAccounts = {
    accountMap.clear
    val accountIndexList = AccountIndexDAO.getAccountIndexList(this)

    accountIndexList.foreach{ ai =>
      ResourceDAO.getAccountObject(ai) match {
        case Some(d:DBObject) =>  accountMap.put(ai,d)
        case None => error("Account Index points to non existant resource object. AI=" + ai)
      }
    }
    accountMap
  }

  def hasResourceAccount(config:ConnectorConfig) = {
   accountIndexForResource(config).size > 0
  }

  def accountIndexForResource(config:ConnectorConfig) = {
     val rname = MongoUtil.collectionForConnector(config).getName
     accountMap.keys.filter( ai => ai.resourceName.equals(rname))
  }

  def printAccounts() = {
    accountMap.foreach{ case (ai,dbo) => println("Account " + ai + " vals=" + dbo)}
  }
}


case class AccountIndex(var userId:Option[ObjectId] = None,
                        resourceName:String,
                        accountName:String = null,
                        needsSync:Boolean = false,
                        lastSync:Long = System.currentTimeMillis,
                        @Key("_id") var id: ObjectId = new ObjectId)