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

package com.my2do.idm.test


import com.my2do.idm.mongo.MongoUtil
import com.my2do.idm.objects._
import com.my2do.idm.dao._

/**
 *
 * User: warren
 * Date: 3/31/11
 * Time: 5:03 PM
 *
 */

class MongoTest extends FunTest {


  val db = MongoUtil.db


  test("SalatDAO test") {

    MongoUtil.dropAndCreateDB

    val u = User("test1", "test", "tester")
    val userWithSameId = User("test1", "test", "tester with new name")

    val a1 = AccountIndex(Some(u.id), "ldap1")
    val a2 = AccountIndex(Some(u.id), "ldap2")
    val a3 = AccountIndex(None, "ldap2")

    var r = UserDAO.insert(u)
    assert(r.isDefined)
    r = UserDAO.insert(userWithSameId)
    assert(r.isEmpty, "Should not be able to insert a duplicate user with same accountName")

    AccountIndexDAO.insert(a1, a2, a3)

    val iter = AccountIndexDAO.findByUserId(u.id)
    assert(iter.count == 2, "User should have two associated accoints")
    while (iter.hasNext) {
      println("a=" + iter.next)
    }
  }

  test("Role DAO") {
    MongoUtil.dropAndCreateDB
    val p = Role("parent")

    val itrole = Role("itrole1", Some(p.id))
    val itrole2 = Role("ITRole2", Some(p.id))
    p.childRoles = List(itrole.id,itrole2.id)

    val e1 = Entitlement("ldap","groups","cn=foo,bar=bah", AssignmentType.MERGE)

    itrole.entitlements = List(e1)
    RoleDAO.save(p)
    RoleDAO.save(itrole)
    RoleDAO.save(itrole2)
  }
}