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

package com.my2do.idm.dao

import com.my2do.idm.mongo.MongoUtil
import com.mongodb.casbah.Imports._
import com.my2do.idm.objects._
import net.liftweb.common.Logger
/**
 *
 * User: warren
 * Date: 4/2/11
 * Time: 4:20 PM
 *
 */

object ResourceDAO extends Logger {

  def getResourceObject(ai: AccountIndex): Option[DBObject] = {
    val collection = MongoUtil.collectionForResourceKey(ai.resourceKey)
    val obj:Option[DBObject] = collection.findOne[DBObject](MongoUtil.makeNameAttribute(ai.accountName))
    normalize(obj)
    obj
  }

  private def normalize(c:Option[DBObject]) = {
    c match {
      case Some(obj) =>
        obj.foreach{  a =>
          a._2 match {
            case x: BasicDBList =>
              val l = List[AnyRef]() ++ x
              debug("replacing db list =" + l)
              obj.put(a._1,l)
            case _ => // do nothing
          }
        }
      case None => // do nothing
    }
  }

    /**
     * Insert an account object into a resource collection.
     *
     * todo: Should we check for duplciates?
     *
     */
    def insertAccountObject(collection: MongoCollection, dbo: DBObject): Option[String] = {
      val nameAttr = checkNameAttribute(dbo)
      val result = collection.insert(dbo)
      if (result.getError != null) {
        throw new RuntimeException("Could not insert into collection: error=" + result.getError)
      }
      nameAttr
    }

    def saveResourceObject(resourceKey: String, dbo: DBObject) = {
      val c = MongoUtil.collectionForResourceKey(resourceKey)
      val result = c.save(dbo)
      if (result.getError != null) {
        throw new RuntimeException("Could not insert into collection: error=" + result.getError)
      }
    }

    def checkNameAttribute(dbo: DBObject) = {
      val nameAttr = dbo.getAs[String](MongoUtil.NAME_ATTRIBUTE_STRING)

      if (nameAttr.isEmpty) {
        error("Missing manadatory account name attribute for object " + dbo)
      }
      nameAttr
    }

  def remove(resourceKey:String, dbo:DBObject) = {
    val c = MongoUtil.collectionForResourceKey(resourceKey)
    val n = MongoUtil.accountName(dbo)
    c.remove( MongoUtil.makeNameAttribute(n))
  }

}





