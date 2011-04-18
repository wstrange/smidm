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

package com.my2do.idm.mongo

import com.mongodb.casbah.commons.conversions.MongoConversionHelper
import com.mongodb.BasicDBList
import scala.collection.JavaConversions._
import collection.mutable.ListBuffer
import org.bson.{BSON, Transformer}

/**
 * Test to de-serialize BasicDBList as a Scala collection
 *
 * This does not work - as it conflicts with Salat's deserialization...
 *
 * User: warren
 * Date: 4/16/11
 * Time: 5:22 PM
 * 
 */


object RegisterScalaCollectionConversionHelpers extends ScalaCollectionHelper {
  def apply() = {
    log.debug("Registering Scala Collection Conversions.")
    super.register()
  }
}

object DeregisterScalaCollectionConversionHelpers extends ScalaCollectionHelper {
  def apply() = {
    log.debug("Unregistering Scala Collection Conversions.")
    super.unregister()
  }
}

trait ScalaCollectionHelper extends ScalaCustomDeserializer


trait ScalaCustomDeserializer extends MongoConversionHelper {
  private val encodeType = classOf[BasicDBList]
  private val transformer = new Transformer {
    log.trace("Decoding JDK Dates .")

    def transform(o: AnyRef): AnyRef = o match {
      case list:BasicDBList =>  ListBuffer(list)
      case d: List[AnyRef] => d
      case _ => o
    }
  }

   override def register() = {
    log.debug("Hooking up Scala Collection deserializer")
    /** Encoding hook for MongoDB To be able to read JodaDateTime DateTime from MongoDB's BSON Date */
    BSON.addDecodingHook(encodeType, transformer)
    super.register()
  }

  override def unregister() = {
    log.debug("De-registering Scala Collection deserializer.")
    BSON.removeDecodingHooks(encodeType)
    super.unregister()
  }

}