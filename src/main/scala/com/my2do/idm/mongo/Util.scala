package com.my2do.idm.mongo

import com.mongodb.casbah.commons.MongoDBObject
import org.identityconnectors.framework.common.objects.ConnectorObject
import com.my2do.idm.connector.util.ICAttributes

/**
 * 
 * User: warren
 * Date: 4/2/11
 * Time: 1:22 PM
 * 
 */

class Util  {

  def toMongoObject(a:ICAttributes) = {
    val  builder = MongoDBObject.newBuilder
    a.attributeMap.foreach{ case(attrname,attrValue) => builder += (attrname -> attrValue) }
    builder.result
  }
}