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

/**
 *
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 *
 */

import com.my2do.idm.objects._

import com.my2do.idm.sync.SyncManager
import com.my2do.idm.connector.util._
import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource
import com.my2do.idm.rules.{correlateByLDAPUid, correlateByAccountName}

class SyncTest extends FunTest {

  val syncManager: SyncManager = ComponentRegistry.syncManager

  test("Load from Resource") {
    MongoUtil.dropAndCreateDB
    // Create a test user to correlate against
    val u = User("test1", "Test", "Tester", employeeId = "99")
    UserDAO.save(u)

    val resource = Resource.ldapTest

    var count = syncManager.loadFromResource(resource, correlateByLDAPUid, createUserIfMissing = true)

    //assert(count > 50)
    // run a second time
    //syncManager.loadFromResource(LDAP_Prod, LDAPCorrelator, createUserIfMissing = true)
    // todo: What do we test?
  }


  /**
   * Tests sync from a flat file
   */
  test("Sync from Flat File") {
    MongoUtil.dropAndCreateDB

    val flatfile = Resource.flatfile1
    val ldapResource = Resource.ldapTest

    // create a closure to perform the sync transformations
    // this is where you implement your sync logic to decide which attribute goes where
    val f = {
      (u: UserView, a: ICAttributes) =>
      // update user attributes
        u.user.department = a("department").asInstanceOf[String]
        u.user.email = a("email").asInstanceOf[String]
        u("test") = u.user.email
        u.user.managerId = a("managerId").asInstanceOf[String]
        // set the LDAP employeeNumber according to some funky calculation..
        u(ldapResource, "employeeNumber") = "C" + u.user.department

        debug("Roles =" + a("roles"))

        // test some extended attributes

        u.user.attributes = Map("attr1" -> "foo", "attr2" -> 1.asInstanceOf[AnyRef])

        // should have the ldap account assigned
        assert(u.user.isResourceDirectlyAssigned(ldapResource))
        u.printAccounts
    }
    syncManager.sync(flatfile, f, correlateByAccountName, createMissingAccounts = true) // trigger creation of any uncorrelated accounts

    val u = UserDAO.findByAccountName("test1")
    debug("Got back a user=" + u.get)

  }
}
