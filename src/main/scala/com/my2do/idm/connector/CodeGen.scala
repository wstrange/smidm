/*
 * Copyright (c) 2011 - Warren Strange
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.my2do.idm.connector


import java.lang.reflect.Method
import scala.collection.JavaConversions._
import org.identityconnectors.framework.api._
import org.identityconnectors.framework.common.objects.{AttributeInfo, ObjectClass, Schema}
import java.io.FileWriter
import net.liftweb.common.Logger
import org.identityconnectors.common.security.{GuardedByteArray, GuardedString}

/**
 * Utility methods to generate entity and configuration classes for interfacing
 * Scala code with the connector framework
 *
 * User: warren
 * Date: 2/20/11
 * Time: 4:01 PM
 *
 */

object CodeGen extends Logger {

  /**
   * Utility method to create a base class declaration for a connector connector objects
   * Run this from a unit test
   * Output is written to target/
   */
  def makeConfigClass(ci: ConnectorInfo) = {
    val key = ci.getConnectorKey
    val apiConfig = ci.createDefaultAPIConfiguration
    val props = apiConfig.getConfigurationProperties()
    val xx = key.getConnectorName
    val name = xx.substring( xx.lastIndexOf('.') +1)  + "DefaultConfig"
    var s = """
package connector

import org.identityconnectors.common.security.GuardedString
import org.identityconnectors.framework.api.ConnectorKey

/**
  * Configuration Base Class - auto generated
  * Subclass this and override any local connector parameters for each unique instance
  */
abstract class %s extends com.my2do.idm.connector.ConnectorConfig {
""".format(name)

    s +=  "    override val connectorKey = new ConnectorKey(\"%s\",\"%s\",\"%s\")\n".format(
    key.getBundleName, key.getBundleVersion, key.getConnectorName)


    props.getPropertyNames.foreach {
      propName =>
        val prop = props.getProperty(propName)
        s += "   // " + prop.getDisplayName(null) + " - " + prop.getHelpMessage(null) + "\n"
        s += "   val " + propName + " = " + propToScalaVal(prop) + "\n"
    }
    s += "}\n"
    setOutput("target/" + name + ".scala")
    p(s)
    closeOutput
  }

  def makeEntityClasses(objName:String, schema:Schema)  = {
   setOutput("target/%sEntity.scala".format(objName))
   p(importPrefix)
   schema.getObjectClassInfo.foreach{ oc =>
       oc.getType match {
        case ObjectClass.ACCOUNT_NAME  =>   makeEntity( objName + "Account", oc.getAttributeInfo)
        case ObjectClass.GROUP_NAME =>  makeEntity(objName + "Group", oc.getAttributeInfo)
        case _  => //println("Other" + _)
      }
   }
   closeOutput
  }

  private val importPrefix ="""package com.my2do.idm.model

import scala.reflect.BeanProperty
import javax.persistence._
import com.my2do.idm.model.ConnectorEntity
import com.my2do.idm.connector.annotation.ConnectorAttribute
"""

  // template for entity class prefix
  private val entityPrefixTemplate = """
/**
 * Auto generated class. Edit as required
 */
@Entity
class %s extends ConnectorEntity {
 """

  // template for printing entity attribute
  private val attrTemplate =
"""
   @ConnectorAttribute(name="%s", isCreatable=%b, isRequired=%b,isMultiValued=%b,isReadable=%b, isUpdateable=%b)
   @BeanProperty
   var %s:%s = _
 """

  private def makeEntity(name:String, attrs: java.util.Set[AttributeInfo]) = {
     p(entityPrefixTemplate.format(name))
     attrs.foreach{ attr =>
       // we skip special framework attributes __PASSWORD__, __NAME__, etc.
       // These are transient as far as the entity class is concerned
       if( ! attr.getName.startsWith(("__")))
        p(attrTemplate.format( attr.getName,  attr.isCreateable,
          attr.isRequired,attr.isMultiValued, attr.isReadable, attr.isUpdateable, attr.getName, pClass(attr.getType)))
    }
    pl("}")
  }

  // print a class as a Scala type declaration
  private def pClass(c:Class[_]) = {
    var s = ""
    if( c.isArray )
      s = "Array["
    s += javaTypeToScala(c.getCanonicalName)
    if( c.isArray)
      s += "]"
    s
  }

  // convert a java canonical type name to a scala type name
  private def javaTypeToScala(s:String) = {
    val t = s.stripSuffix("[]")
    t match {
      case "byte" => "Byte"
      // todo: Other types as we need them

      // default - the class name - but strip off common java.lang. prefixes
      case _ => t.stripPrefix("java.lang.")
    }
  }

  var writer:FileWriter = _

  private def setOutput(s:String) = {
    writer = new FileWriter(s)
  }
  private def closeOutput()  = { writer.close}

  private def p(s:String) = writer.write(s)
  private def pl(s:String) = p(s + "\n")


  /**
   * Turn the Config Property to a scala decl
   * This is a kludge and does not always work
   * Arrays are always treated as arrays of strings (which they almost always are...)
   * the output of this will need to be edited.
   */
  def propToScalaVal(prop: ConfigurationProperty): String = {
    val clz = prop.getType
    var result: String = ""

    //info("Config Property type=" + clz)
    if (clz.isArray) {
      result += "Array("
      val v = prop.getValue().asInstanceOf[Array[_]]
      var f: Boolean = false
      if (v.length == 0)
        result += "\"\""
      else
        v.foreach {
          x =>
            if (f) result += ", "
            result += quoteString(x.toString)
            f = true
        }
      result += ")"
    }
    else
    if (clz == classOf[GuardedString])
      result += "new GuardedString(\"changeme\".toCharArray)"
    else
    if (clz == classOf[GuardedByteArray])
      result += "new GuardedByteArray(\"changeme\".toCharArray)"
    else
    if (clz == classOf[java.lang.String]) {
      val s = if (prop.getValue == null) "" else prop.getValue.toString
      result += quoteString(s)
    }
    else
    if( clz == classOf[scala.Char]) {
      result += "'" + prop.getValue + "'"
    }
    else
    if( clz == classOf[scala.Boolean]) {
      result += prop.getValue
    }
    else
    if( clz == classOf[scala.Int]) {
      result += prop.getValue
    }
    else  {

      // default catch all just spits out the value and hopes that
      // scala's type inference does the right thing.
      warn("Warning: Type " + clz + " is not handled explictly. Check the generated scala file")
      result += prop.getValue
    }

    result
  }

  def quoteString(s: String) = "\"" + (if (s != null) s else "") + "\""
}