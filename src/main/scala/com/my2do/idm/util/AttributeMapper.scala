package com.my2do.idm.util

import com.my2do.idm.objects.User
import com.my2do.idm.connector.util.{ConnectorObjectWrapper, ICAttributes}

/**
 *
 * Map input variable to multiple output variables in the user objects
 *
 * User: warren
 * Date: 3/26/11
 * Time: 8:40 PM
 * 
 */


class AttributeMapper(var funcList:List[(User, ICAttributes) => Unit]  = Nil) {
  def doMap(u:User, s: ICAttributes) = {
    funcList.foreach( action => action(u,s) )
  }

  def addMapping(func:((User,  ICAttributes) => Unit) ) = {
    funcList +:= func
  }
}

