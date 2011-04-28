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

package com.my2do.idm.test

import com.my2do.idm.rules.RoleAssignmentRule
import com.my2do.idm.dao.RoleDAO
import com.my2do.idm.objects.{Role, UserView}

/**
 *
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 4/26/11
 * Time: 11:02 AM
 */

object RuleDefinitions {

  object DeptAssignment extends RoleAssignmentRule(100) {
    override def evaluateRoles(uv:UserView, attrMap:Map[String,AnyRef] = Map()) = {
      val toAdd = calculateDeptRole(uv.user.department)
      val toRemove = uv.assignedRolesByCategory("department").filterNot(toAdd contains)
      Pair(toAdd,toRemove)
    }

    def calculateDeptRole(dept:String) = {
      val x = dept match {
          case "marketing" =>   RoleDAO.findByName("MarketingUser")
          case "engineering" => RoleDAO.findByName("EngineeringUser")
          case _ => None
      }
      x.toList
    }
  }

}