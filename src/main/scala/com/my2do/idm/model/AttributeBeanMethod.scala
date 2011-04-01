package com.my2do.idm.model

import java.lang.reflect.Method
import com.my2do.idm.connector.annotation.ConnectorAttribute


/**
 * Holds reflection getter/setters that know how to set/get a ICF connector attribute
 * from a Java ConnectorEntity object.
 *
 * @param name  the ICF attribute name
 * @param entityClass  - The Java/Scala class type to marshall from (the source)
 * @param valueClass  - the Class type of the target
 */
class AttributeBeanMethod(val name:String, entityClass:Class[_], valueClass:Class[_]) {
  var setter:Method = _
  var getter:Method = _
  private var connectorAttr:ConnectorAttribute = _


  init


  // todo: Add error handling here. If the annotation does not exist - throw an exception?
  def init = {
   val field = entityClass.getDeclaredField(name)
   connectorAttr = field.getAnnotation( classOf[ConnectorAttribute]).asInstanceOf[ConnectorAttribute]
   getter = entityClass.getMethod("get" + name.capitalize)
   setter = entityClass.getMethod("set" + name.capitalize, valueClass)
    /*
    if(connectorAttr.isUpdateable || connectorAttr.isCreatable )


    if( connectorAttr.isReadable )
    */

  }

  def connectorAttribute() = connectorAttr

  def getValue(obj:ConnectorEntity):AnyRef  = {
    getter.invoke(obj)
  }

  def setValue(obj:ConnectorEntity, x:AnyRef) = {
    setter.invoke(obj,x)
  }
}



