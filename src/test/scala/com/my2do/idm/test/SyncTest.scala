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

import com.my2do.idm.connector.util._
import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource
import com.my2do.idm.rules.{correlateByLDAPUid, correlateByAccountName}
import com.my2do.idmsvc.test.TestData
import com.my2do.idm.sync.{ReconManager, SyncManager}

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
    // todo: What do we want to test?
  }


  /**
   * Tests sync from a flat file
   */
  test("Sync from Flat File") {
    MongoUtil.dropAndCreateDB
    TestData.defineRoles




    val flatfile = Resource.flatfile1
    val ldapResource = Resource.ldapTest


    // load any old users in ldap - this will
    var count = syncManager.loadFromResource(ldapResource, correlateByLDAPUid, createUserIfMissing = true)


    // create a transform closure to perform the sync transformations
    // this is where you implement your sync logic to decide which attribute goes where

    val transform = {
      // userview is the runtime view of the user, ICAttributes are the incoming attributes to sync
      (u: UserView, a: ICAttributes) =>
      // update user attributes
        u.user("department") = a("department")
        u.user("email") = a("email")
        u.user("managerId") = a("managerId")
        // test setting an extended attribute
        u.user("attribute.attr1") = 1.asInstanceOf[AnyRef]

        u.ensureHasResource(ldapResource)
        // set the LDAP employeeNumber according to some funky calculation..
        // assumes the user has an ldap account
        // if the createMissingAccounts flag is set on the sync process the account will get created
        //u(ldapResource, "employeeNumber") = "C" + u.user.department
        val ldapAccount = u.getResourceObjects(ldapResource)(0)
        ldapAccount("employeeNumber") = "C" + u.user.employeeId


        // test some extended attributes
        u.user.attributes.put("attr2", "foo")

        // should have the ldap account assigned
        assert(u.user.isResourceDirectlyAssigned(ldapResource))
        u.printDebug
    }


    syncManager.sync(flatfile, transform, correlateByAccountName, RuleDefinitions.DeptAssignment, createMissingAccounts = true) // trigger creation of any uncorrelated accounts

    val u = UserDAO.findByAccountName("test1")
    debug("Got back a user=" + u.get)
    val uv2 = new UserView(u.get)
    uv2.printDebug()

    // check to see if the user got put in the Marketing Group as a result of the Role Assignment
    //val g = uv2(ldapResource,"member").asInstanceOf[Seq[String]]
    //assert( g.contains(TestData.marketingGroupEntitlement.attrVal))

    // run the recon manager to flush out changes

    new ReconManager().recon(ldapResource,ObjectClass.account)
  }
}
