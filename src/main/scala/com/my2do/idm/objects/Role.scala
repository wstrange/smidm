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

package com.my2do.idm.objects

import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._

/**
 *
 * User: warren
 * Date: 4/12/11
 * Time: 6:25 PM
 *
 */

object Role {
  val ITROLE = 10
  val BIZROLE = 20

}

case class Role(name: String,
                category:String,
                var parentId: Option[ObjectId] = None,
                var childRoles: List[ObjectId] = Nil,
                var entitlements: List[Entitlement] = Nil,
                var description: String = "",
                @Key("_id") id: ObjectId = new ObjectId())  {

  def addEntitlement(e:Entitlement) = entitlements = e :: entitlements
  def assignedResourceKeys = entitlements.map ( e => e.resourceKey)
}


