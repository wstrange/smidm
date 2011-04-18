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

package com.my2do.idm.util

import java.lang.reflect.{Method, Field}
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