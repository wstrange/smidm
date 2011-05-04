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


import com.novus.salat._
import com.novus.salat.global._
import com.my2do.idm.resource.Resource

/**
 *
 * User: warren
 * Date: 4/2/11
 * Time: 4:20 PM
 *
 */


object ResourceDAO extends Logger {
  def apply(resourceKey:String) = {
    assert(resourceKey != null)
    val r = Resource.resourceList.find{r:Resource => r.resourceKey.equals(resourceKey)}
    new ResourceDAO(r.get)
  }
}

class ResourceDAO(resource:Resource) extends Logger {
  val _grater = grater[ResourceObject]


  def save(ro:ResourceObject) = {
    val c = resource.collectionForObjectClass(ro.objectClass)
    val dbo = _grater.asDBObject(ro)
    debug("Saving object=" + dbo)
    val result = c.save(dbo)
    result
  }

  def findByAccountName(id:String, o:ObjectClass):Option[ResourceObject] = {
    val c =   resource.collectionForObjectClass(o)
    val r = c.findOneByID(id)
    r match {
      case x:DBObject => Some(_grater.asObject(x))
      case _ => None
    }
  }

  def remove(ro:ResourceObject) = {
    val c =   resource.collectionForObjectClass(ro.objectClass)
    val result = c.remove(MongoDBObject("_id" -> ro.accountName))
  }

}




