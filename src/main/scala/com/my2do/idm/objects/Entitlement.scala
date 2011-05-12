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



object AssignmentType extends Enumeration {
  type AssignmentType = Value
  val MERGE, REPLACE, REMOVE, RULE = Value
}


case class Entitlement(resourceKey: String,
                       attribute: String,
                       attrVal: AnyRef,
                       assignmentType: AssignmentType.Value,
                       objectClass: ObjectClass = ObjectClass.account,
                       @Key("_id") id: ObjectId = new ObjectId())  {


  import AssignmentType._

  /**
   * Apply this entitlement to the resource object
   *
   * todo: Should we always assume MERGE entitlements will be lists?
   */
  def assign(ro:ResourceObject) = {
    assignmentType match {
      case REPLACE => ro.put(attribute,attrVal)
      case MERGE =>  var list = ro.get(attribute).asInstanceOf[Seq[AnyRef]]
          // todo: change to seq?
          if( attrVal.isInstanceOf[Seq[AnyRef]]) {
            val l2 = attrVal.asInstanceOf[Seq[AnyRef]]
            list =  l2 ++ list
          }
          else
            list = list.+:(attrVal)
          ro.put(attribute,list  )

    }
  }

  /**
   * Unassign this entitlement
   */
  def unassign(ro:ResourceObject) = {
     assignmentType match {
      case REPLACE => ro.remove(attribute)
      case MERGE =>  var list = ro.get(attribute).asInstanceOf[Seq[AnyRef]]
          // todo: change to seq?
          if( attrVal.isInstanceOf[Seq[AnyRef]]) {
            val l2 = attrVal.asInstanceOf[Seq[AnyRef]]
            list =  l2.filterNot( l2 contains)
          }
          else
            list = list.filterNot(_ == attrVal)
          ro.put(attribute,list  )
    }
  }
}






