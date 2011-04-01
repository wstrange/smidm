package com.my2do.idm.util

import java.lang.reflect.{Method, Field}
import com.my2do.idm.connector.annotation.ConnectorAttribute
import com.my2do.idm.model.AttributeBeanMethod
import collection.mutable.HashMap._
import net.liftweb.common.Logger


/**
 * 
 * User: warren
 * Date: 3/25/11
 * Time: 6:48 PM
 * 
 */

object ReflectUtil extends Logger {

  def setObjectValues(obj:AnyRef, m:Map[String,AnyRef]) = {
    val clazz:Class[_] = obj.getClass

    m.foreach{ case (name , value) =>
      val n = "set" + name.capitalize
      try {
        val method = clazz.getDeclaredMethod(n,classOf[String])
        method.invoke(obj,value)
//        /field.set(obj,value)
      } catch {
        case e:NoSuchMethodException => info("No Method found for " + n)
      }
    }
  }
}