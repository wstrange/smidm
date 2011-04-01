package com.my2do.idmsvc.test

import org.junit.Test

import com.mongodb.casbah.Imports._
import scala.collection.JavaConversions._
import config.LDAP_Prod
import org.identityconnectors.framework.common.objects.{Attribute, ObjectClass, ConnectorObject, ResultsHandler, AttributeInfo}
import collection.mutable.{HashMap, HashSet}
import com.my2do.idm.connector.ConnectorConfig

/**
 * 
 * User: warren
 * Date: 3/31/11
 * Time: 5:03 PM
 * 
 */

class MongoTest extends TestBase {

   val aset = new HashMap[String,AttributeInfo]()


  @Test
  def testMongoDB = {

    val mongo = MongoConnection()

    var proddb = mongo("prod")

    val facade = LDAP_Prod.getFacade


    LDAP_Prod.objectClasses.foreach { objClass =>

        val collection =  proddb("ldapProd" + objClass.getObjectClassValue)
        facade.search(objClass,null, new ResultsHandler() {
          def handle(obj:ConnectorObject) = {
            val u= obj.getUid
            val uid = MongoDBObject( u.getName -> u.getValue.head )
            collection.findOne(uid) match {
              case Some(x) => println("Found obj with uid=" + uid + " vals " +x)
              case None =>  addObject(collection,obj,LDAP_Prod)
            }
            true
          }
        }, null )
    }
  }

  def addObject(coll:MongoCollection, obj:ConnectorObject,config:ConnectorConfig) = {
     val o = MongoDBObject()
      obj.getAttributes.foreach { a =>
          o +=  (attrValue(a, config.attributeInfo(obj.getObjectClass, a.getName)))
      }
    coll += o
    println("Added o=" + o)
  }

  def attrValue(a:Attribute,info:Option[AttributeInfo]):Pair[String,AnyRef] = {
    val n = a.getName
    info match {
      case Some(i) => if( i.isMultiValued) return ( n -> a.getValue )
      case None =>
    }
    ( n -> a.getValue.head)
  }

}