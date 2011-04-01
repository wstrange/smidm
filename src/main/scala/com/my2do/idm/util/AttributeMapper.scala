package com.my2do.idm.util

import com.my2do.idm.model.User
import com.my2do.idm.connector.util.{ConnectorObjectWrapper, ICFAttributes}

/**
 *
 * Map input variable to multiple output variables in the user object
 *
 * User: warren
 * Date: 3/26/11
 * Time: 8:40 PM
 * 
 */


class AttributeMapper(var funcList:List[(User, ICFAttributes) => Unit]  = Nil) {
  def doMap(u:User, s: ICFAttributes) = {
    funcList.foreach( action => action(u,s) )
  }

  def addMapping(func:((User,  ICFAttributes) => Unit) ) = {
    funcList +:= func
  }
}

