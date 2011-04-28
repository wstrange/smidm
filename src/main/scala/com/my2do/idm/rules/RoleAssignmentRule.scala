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

package com.my2do.idm.rules

import com.my2do.idm.objects.{Role, UserView}
import collection.mutable.ArrayBuffer

/**
 *
 * User: warren
 * Date: 4/26/11
 */


abstract class RoleAssignmentRule(val priority:Int = 100) {
  /**
   * Determine which roles to add and remove to the user view based on the state of the user's attributes
   *
   * Implementations extend this trait and implement customized behavior
   *
   * @param userView - the user view. Assumes that the view has been fetched
   * @param attrMap - an optional map of attribute name/value pairs used to make the assignment decision
   * @return a pair (toAdd,toRemove) of roles to add to the user and to remove
   */
  def evaluateRoles(userView:UserView, attrMap:Map[String,AnyRef] = Map() ):Pair[List[Role],List[Role]] = {
    new Pair(Nil,Nil)
  }
}

object nullRoleAssignmentRule extends RoleAssignmentRule  // default - no roles added / deleted

class RoleAssignmentChain(rules:List[RoleAssignmentRule]) extends RoleAssignmentRule {
   override def evaluateRoles(userView:UserView, attrMap:Map[String,AnyRef] = Map() ):Pair[List[Role],List[Role]]  = {
     val toAdd = new ArrayBuffer[Role]
     val toRemove = new ArrayBuffer[Role]
     val x = rules.foreach{ r =>
      val x = r.evaluateRoles(userView,attrMap)
      toAdd.appendAll(x._1)
      toRemove.appendAll(x._2)
     }
     //
     new Pair(toAdd.toList,toRemove.toList)
   }
}
