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

package com.my2do.idmsvc.test

import util.Random
import com.my2do.idm.resource.Resource
import net.liftweb.common.Logger
import com.my2do.idm.dao.{SyncIndexDAO, UserDAO, RoleDAO}
import com.my2do.idm.objects._
import com.my2do.idm.sync.ReconManager

/**
 *
 * User: warren
 * Date: 3/24/11
 * Time: 2:46 PM
 *
 */

object TestData extends Logger {


  val departments = Vector("marketing", "sales", "engineering")
  val ldap = Resource.ldapTest
  val marketingGroupEntitlement = Entitlement(ldap.resourceKey, "member", "cn=MarketingUsers,ou=Groups,dc=example,dc=com", AssignmentType.MERGE)

  def randomDept = departments(Random.nextInt(departments.size))

  val orgSuffix = ",dc=example,dc=com"
  val groupSuffix = ",ou=Groups" + orgSuffix
  val userSuffix = ",ou=People" + orgSuffix

  // some sample users
  val user = 0 to 2 map (i => User("user" + i, "First", "Last" + i, "e900" + i, randomDept, "user" + i + "@test.com"))

  def defineRoles = {

    //val e1 = Entitlement(ldap.resourceKey, "departmentNumber", "newdept", AssignmentType.REPLACE)

    val r1 = Role("MarketingUser", "DepartmentRole",  entitlements = List(marketingGroupEntitlement))

    val x = RoleDAO.save(r1)
     debug("Creating role=" +r1 + "x=" +x)
    RoleDAO.save(Role("EngineeringUser", "DepartmentRole", None))
    RoleDAO.save(Role("SalesUser", "DepartmentRole", None))

  }

  val group = 0 to 2 map {i =>
    ResourceObject("cn=testgrp" + i + groupSuffix,  "testgroup" +i,
              Map("uniqueMember" -> List("uid="+user(i).accountName+userSuffix)),objectClass = ObjectClass.group,ldap)}


  def defineGroups = {
    group.foreach{ g =>
      ldap.dao.save(g)
      SyncIndexDAO.save(SyncIndex(None, ldap.resourceKey, g.accountName, needsSync = true, objectClass = ObjectClass.group))
    }
  }

  def defineUsers = {
    user.foreach{u =>
      val uv = new UserView(u)
      uv.ensureHasResource(ldap)
      uv.flush()
    }
  }


  def deleteGroups = {
    group.foreach { g =>
      val r = SyncIndexDAO.findByAccountName(ldap.resourceKey,g.accountName)
      r.foreach{ i =>
        i.delete = true
        i.needsSync = true
        SyncIndexDAO.save(i)
      }
      new ReconManager().recon(ldap,ObjectClass.group)
    }

    def deleteUsers() = {

    }

  }




}