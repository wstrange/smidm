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

package com.my2do.idm.dao

import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.objects._
import com.novus.salat.dao.SalatDAO
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._

/**
  *
  * User: warren
  * Date: 4/2/11
  * Time: 4:20 PM
  *
  */

object RoleDAO extends SalatDAO[Role, ObjectId](collection = MongoUtil.roleCollection) {
  def findByName(name: String): Option[Role] = findOne(Map("name" -> name))

  def findByCategory(category: String) = find(Map("category" -> category))
}


