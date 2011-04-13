package com.my2do.idmsvc.test

import org.junit.Test

import com.mongodb.casbah.Imports._
import scala.collection.JavaConversions._
import config.LDAP_Prod
import org.identityconnectors.framework.common.objects.{Attribute, ObjectClass, ConnectorObject, ResultsHandler, AttributeInfo}
import collection.mutable.{HashMap, HashSet}
import com.my2do.idm.connector.{ConnectorManager, ConnectorConfig}
import com.my2do.idm.mongo.{MongoUtil, ICFacade}
import com.novus.salat._
import com.novus.salat.global._
import com.my2do.idm.objects._
import org.junit.Assert._
import com.my2do.idm.dao.{AccountIndexDAO, UserDAO}


/**
 * 
 * User: warren
 * Date: 3/31/11
 * Time: 5:03 PM
 * 
 */

class MongoTest extends TestBase {


  val db = MongoUtil.db



  @Test
  def testSalatDAO():Unit = {

    MongoUtil.dropAndCreateDB

    val u = User("test1","test","tester")
    val userWithSameId = User("test1","test","tester with new name")

    val a1 = AccountIndex(Some(u.id),"ldap1")
    val a2 = AccountIndex(Some(u.id),"ldap2")
    val a3 = AccountIndex(None,"ldap2")

    var r = UserDAO.insert(u)
    assertTrue(r.isDefined)
    r = UserDAO.insert(userWithSameId)
    assertTrue("Should not be able to insert a duplicate user with same accountName", r.isEmpty)

    AccountIndexDAO.insert(a1, a2, a3)

    val iter = u.accountIndexIterator
    assertEquals("User should have two associated accounts",iter.count,2 )
    while( iter.hasNext){
      println("a=" + iter.next)
    }

  }
}