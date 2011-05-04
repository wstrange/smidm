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
import com.novus.salat.annotations._
import org.identityconnectors.framework.common.objects.{ ObjectClass => icfObj }



case class ObjectClass(name:String)

object ObjectClass {

  val account = new ObjectClass( icfObj.ACCOUNT_NAME )
  val group = new ObjectClass(icfObj.GROUP_NAME)
}

/**
  * Holds meta data and and set of values associated with a resource object instance.
  *
  * User: warren
  * Date: 4/19/11
  * Time: 5:56 PM
  *
  * @param accountName - The unique account name as defined by the ICF. Example: uid=foo,ou=People,dc=test,dc=com
  * @param uid -  unique __UID__ returned by the ICF framework. Not the same as accountName. This is often a GUID
  * @param attributes - map of key/value pairs for attributes. e.g. "firstName" - > "Bill", etc.
  * @param member - member. If this resource object is an account -the values are the static groups that this
  *   user belongs to (accountName of the group is stored). If this resource object is a group, the values
  *     are the users that belong to the group (accountNames). The  ICF connector must
  *     support a GROUP schema for the object
  *
  *
  * TODO: Should member be overloaded??
  *
  */

case class ResourceObject(@Key("_id") accountName: String,
                          uid: String,
                          var attributes: Map[String, AnyRef],
                          var member: Option[List[String]] = None) {
  var isDirty: Boolean = false

  // todo: do we need to persist this?
  // should we make this an enum  ?
  // do we need this?
  var objectClass:ObjectClass = ObjectClass.account

  // After construction
  normalizeAttributes()


  /**
    *
    * When the object is read from Mongo collections are of type BasicDBList
    * This normalizes all list attributes back to a Scala collection Type
    * This allows us to treat attributes in a consistent fashion
    */
  def normalizeAttributes() = {
    attributes = attributes.map {
      case (key, value) => (key, normalize(value))
    }
  }

  /**
    * Convert to a Scalaish types
    */
  private def normalize(v: AnyRef) = {
    v match {
      case x: BasicDBList => val ml: MongoDBList = x
      ml.toList
      case _ => v
    }
  }

  /**
    * Map like put interface - so we can use  map expressions on the attributes
    * @param attrName - Attribute Name (e.g. employeeNumber, etc..)
    * @param v - attribute value
    */
  def put(attrName: String, v: AnyRef) = {
    attributes = attributes + (attrName -> v)
    isDirty = true
  }

  def get(attrName: String) = attributes(attrName)

  def remove(attrName: String) = attributes = (attributes - attrName)

  /**
    * Syntactic sugar
    * So you can do userView("firstName")
    */
  def apply(attrName: String) = get(attrName)

  /**
    * Syntactic sugar
    * So you can do userView("firstName") = "Freddy"
    */
  def update(attrName: String, v: AnyRef) = put(attrName, v)

}