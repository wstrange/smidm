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
import com.my2do.idm.resource.Resource

/**
 *   Serves as a link to a ResourceObject
 * User: warren
 * Date: 4/2/11
 * Time: 3:07 PM
 *
 */

object ResourceReference {

  val ldapTest = ResourceReference("ldapTest", Resource.ldapTest.instanceName)
  val flatFile1 = ResourceReference("flatfile", Resource.flatfile1.instanceName)
}

/**
 * To do: Use this to store resource config?
 */
case class ResourceReference(resourceName:String, instanceKey:String,@Key("_id") id: ObjectId = new ObjectId()) {
  val resource = Resource.getResourceByInstanceKey(instanceKey)
}

