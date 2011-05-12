/*
 * Copyright (c) 2011 Warren Strange
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

package com.my2do.idm.objects


import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao.SalatDAO
import com.my2do.idm.mongo.MongoUtil


/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 11/05/11
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */

case class ResourceRef(resourceKey:String,
                       accountName:String,
                       objectClass:ObjectClass = ObjectClass.account,
                       parentId: ObjectId,
                       @Key("_id") id:ObjectId = new ObjectId()) {
}


object ResourceRefDAO extends SalatDAO[ResourceRef,ObjectId] (collection = MongoUtil.db("resourceRef")) {
  val childObjects = new ChildCollection[ResourceRef,ObjectId](collection = MongoUtil.db("resourceRef"),
        parentIdField = "parentId" ) {}

}