package com.my2do.idm.connector.util

import scala.collection.JavaConversions._

import net.liftweb.common.Logger
import collection.mutable.HashSet
import org.identityconnectors.framework.common.objects.{ObjectClass, AttributeInfo, ConnectorObject, Attribute}

/**
 *
 * Needed ??  Just some syntactic sugar...
 *
 * User: warren
 * Date: 3/23/11
 * Time: 12:50 PM
 * 
 */


/**
 * Convenience wrappers for accessing an ICF ConnectorObject in a more scala-ish fashion
 */

trait ICAttributes {
  def apply(name:String) = value(name)
  def value(name:String):AnyRef
  def asString(name:String):String
  def firstValueAsString(name:String):String
  def attributeMap:Map[String,AnyRef]

  def getUuid:String
  def getName:String

  def connectorObject:ConnectorObject
}

/**
 * Implements ICFAttributes trait
 * @param obj - the wrapped connector objects
 * @param attrInfo - map of attribute schema info. Needed so we know the data type of the attr
 */
class ConnectorObjectWrapper(obj:ConnectorObject, attrInfo:Map[String,AttributeInfo]) extends ICAttributes with Logger {

  private var map:Map[String,AnyRef] = _

  def attributeMap() = {
    if(map == null) {
       val m = new scala.collection.mutable.HashMap[String,AnyRef]
      obj.getAttributes.foreach( a =>  m.put(a.getName, attrValue(a)))
      map = m toMap
    }
    map
  }

  def value(name:String) = attrValue(obj.getAttributeByName(name))
  def connectorObject = obj


  /**
   *  The ICF framework stores single values in lists.
   *  If the attribute is not multi-valued we flatten it to a scalar
   */
   private def attrValue(a:Attribute):AnyRef = {
    val n = a.getName
    val info = attrInfo.get(n)
    val attrVal = a.getValue
    info match {
      case Some(i) => if( i.isMultiValued) attrVal else attrVal.head
      case None =>  attrVal.head // __UID__ will not have attrInfo associated with it
    }
  }

  /**
   * Use this when you absolutely know the value is a string.
   *
   */
  def asString(name:String) = value(name).asInstanceOf[String]
  def firstValueAsString(name:String):String = obj.getAttributeByName(name).getValue.head.asInstanceOf[String]
  def getUuid = obj.getUid.getUidValue
  def getName = obj.getName.getNameValue


  override def toString() = "Attrs=" + attributeMap.toString()

}