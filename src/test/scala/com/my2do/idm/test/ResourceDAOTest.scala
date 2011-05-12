package com.my2do.idm.test

import com.my2do.idm.resource.Resource
import com.my2do.idm.dao.ResourceDAO
import com.my2do.idm.mongo.MongoUtil
import com.my2do.idm.objects.ResourceObject
import com.mongodb.casbah.Imports._

/**
 * 
 * User: warren
 * Date: 4/19/11
 * Time: 6:28 PM
 * 
 */

class ResourceDAOTest   extends FunTest {
  test("Resource DAO") {

    val resource = Resource.ldapTest

    MongoUtil.dropAndCreateDB

    val rdao =  resource.dao

    val obj = ResourceObject("uid=foo", "1345",
      Map( "sn" -> List("fred", "fredious"),
      "employeeNumber" -> "1234"))

    val id = rdao.save(obj)
    val obj2 = rdao.findByName( obj.accountName, obj.objectClass).get

    debug("got back " + obj2)
    val sn = obj2("sn").asInstanceOf[Seq[AnyRef]]

    debug("SN=" + sn + " obj=" + sn.getClass)
    sn.foreach( x => println(x))
  }
}