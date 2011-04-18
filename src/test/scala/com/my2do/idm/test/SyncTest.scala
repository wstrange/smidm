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
import com.my2do.idm.util.AttributeMapper
import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource

class SyncTest extends FunTest {

  val syncManager: SyncManager = ComponentRegistry.syncManager

  ignore("Basic Sync Test") {
    MongoUtil.dropAndCreateDB
    // Create a test user to correlate against
    val u = User("test1", "Test", "Tester", employeeId = "99")
    UserDAO.save(u)

    val resource = Resource.ldapTest

    var count = syncManager.loadFromResource(resource, createUserIfMissing = true)

    assert(count > 50)
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
    val f =  { (u:UserView, a:ICAttributes) =>
        // update user attributes
        u.user.department = a("department").asInstanceOf[String]
        u.user.email = a("email").asInstanceOf[String]
        u("email") = u.user.email
        // set the LDAP employeeNumber according to some funky calculation..
        u(ldapResource,"employeeNumber") = "C" + u.user.department

        // should have the ldap account assigned
        assert(u.user.isResourceDirectlyAssigned(ldapResource))
        u.printAccounts
    }
    syncManager.sync(flatfile, f, createMissingAccounts = true)  // trigger creation of any uncorrelated accounts


  }
}
