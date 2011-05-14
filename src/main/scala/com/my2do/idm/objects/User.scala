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
import collection.mutable.HashMap
import java.lang.reflect.Method
import scala.collection.mutable

/**
 *
 * User: warren
 * Date: 4/2/11
 * Time: 3:07 PM
 *
 */

object User {
  //val setterMap =
  private val userMethods = classOf[User].getMethods
  private val setterMap = calculateSetters()


  def calculateSetters() = {
    var sm = new HashMap[String,Method]
    userMethods.foreach { m =>
      if( m.getName.endsWith("_$eq")) {
        sm.put(m.getName.stripSuffix("_$eq"),m)
      }
    }
    sm.toMap
  }

}

case class User(var accountName: String,
                var firstName: String,
                var lastName: String,
                var employeeId: String = null,
                var department: String = "",
                var email: String = "",
                var managerId:String = "", // managers employee Id
                val attributes:mutable.Map[String,AnyRef] = new mutable.HashMap[String,AnyRef] (),
                var directlyAssignedResources:List[String] = Nil,
                var roleAssignedResources:List[String] = Nil,
                var roleIdList:List[ObjectId] = Nil,
                @Key("_id") id: ObjectId = new ObjectId()) {

  def isResourceDirectlyAssigned(resource:Resource) = directlyAssignedResources.contains(resource.resourceKey)

  def isResourceRoleAssigned(resource:Resource) = roleAssignedResources.contains(resource.resourceKey)

  //def unassignResource(resource:Resource) = directlyAssignedResources = directlyAssignedResources.filterNot( x => x.equals(resource.resourceKey))


  def update(field:String, v:AnyRef) = {
    // special case attribute.foo - access extended attributes
    if( field.startsWith("attribute.")) {
      val name = field.stripPrefix("attribute.")
      attributes.put(name,v)
    }
    else {
      val m = User.setterMap.get(field).get
      m.invoke(this,v.asInstanceOf[AnyRef])
    }
  }

  def apply(field:String) = {
    if( field.startsWith("attribute.")) {
      val name =  field.stripPrefix("attribute.")
      attributes.get(name)
    }
    else {
      val m = User.userMethods.find( _.getName == field).get
      m.invoke(this)
    }
  }
}



