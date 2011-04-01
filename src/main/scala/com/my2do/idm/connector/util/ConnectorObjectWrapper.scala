package com.my2do.idm.connector.util

import scala.collection.JavaConversions._

import net.liftweb.common.Logger
import collection.mutable.HashSet
import org.identityconnectors.framework.common.objects.{AttributeInfo, ConnectorObject, Attribute}


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

trait ICFAttributes {
  def apply(name:String) = value(name)
  def value(name:String):AnyRef
  def asString(name:String):String
  def attributeMap:Map[String,AnyRef]
  def uidAttribute:String
  def nameAttribute:String
}

/**
 * Implements ICFAttributes trait
 */
class ConnectorObjectWrapper(obj:ConnectorObject, attrInfo:Map[String,AttributeInfo]) extends ICFAttributes with Logger {
  def value(name:String) = attrValue(obj.getAttributeByName(name))


  /**
   *  The ICF framework stores single values in lists. This flattens single values out
   */
   private def attrValue(a:Attribute):AnyRef = {
    val n = a.getName
    val info = attrInfo.get(n)
    val attrVal = a.getValue
    info match {
      case Some(i) => if( i.isMultiValued) attrVal else attrVal.head
      case None =>  debug("Warning. Missing attrInfo for attr name= " + n)
        attrVal.head
    }
  }

  /**
   * Use this when you absolutely know the value is a string.
   *
   */
  def asString(name:String) = value(name).asInstanceOf[String]
  def uidAttribute = obj.getUid.getUidValue
  def nameAttribute = obj.getName.getNameValue


  /**
   * Create a map of attr name / value pairs
   */
  def attributeMap() = {
    val m = new scala.collection.mutable.HashMap[String,AnyRef]
    obj.getAttributes.foreach( a =>  m.put(a.getName, attrValue(a)))
    m toMap
   }


  override def toString() = "Attrs=" + attributeMap.toString()

}