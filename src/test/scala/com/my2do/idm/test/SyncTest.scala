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

import com.my2do.idm.connector.util._
import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource
import com.my2do.idm.rules.{correlateByLDAPUid, correlateByAccountName}
import com.my2do.idmsvc.test.TestData
import com.my2do.idm.sync.{ReconManager, SyncManager}
import com.my2do.idm.objects._ 
import com.my2do.idm.dao.{ResourceDAO, UserDAO, RoleDAO}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith


@RunWith(classOf[JUnitRunner])
class SyncTest extends FunTest {

  val syncManager: SyncManager = ComponentRegistry.syncManager


  /**
    * Load users from ldap resource.
   */

  ignore("Load from Resource") {
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
    TestData.defineGroups

    val flatfile = Resource.flatfile1
    val ldapResource = Resource.ldapTest


    // load any existings users in ldap. Brings the repo in sync with the resource before any test
    var count = syncManager.loadFromResource(ldapResource, correlateByLDAPUid, createUserIfMissing = true)
    syncManager.loadGroupsFromResource(ldapResource)

    // create a transform closure to perform the sync transformations
    // this is where you implement your sync logic to decide which attribute goes where

    val transform = {
      // userview is the runtime view of the user, ICAttributes are the incoming attributes to sync
      (u: UserView, in: ICAttributes) =>
        // update user attributes
        u.user("department") = in("department")
        u.user("email") = in("email")
        u.user("managerId") = in("managerId")
        u.user("employeeId") = in("employeeId")
        // test setting an extended attribute - dot notation
        u.user("attribute.attr1") = 1.asInstanceOf[AnyRef]
        // test extended attributes - alternate syntax
        u.user.attributes.put("attr2", "foo")

        u.ensureHasResource(ldapResource)
        // set the LDAP employeeNumber according to some funky calculation..
        // assumes the user has an ldap account
        // if the createMissingAccounts flag is set on the sync process the account will get created

        val ldapAccount = u.getResourceObjects(ldapResource)(0)

        // alternate syntax?? The problem is that there may be more than one resource account..
        //u(ldapResource, "employeeNumber") = "C" + u.user.department
        ldapAccount("employeeNumber") = "C" + u.user.employeeId

        //val groupObj = ResourceDAO.getGroup("cn=foo,asdf...")

        ldapAccount.addToGroup(TestData.group(0).accountName)


        // should have the ldap account assigned
        assert(u.user.isResourceDirectlyAssigned(ldapResource))
        u.printDebug
    }

    // run the sync process.  Input is the flatfile. Correlate by account name. Use a set of rules to assign roles by department, create non correlated accounts
    syncManager.sync(flatfile, transform, correlateByAccountName, RuleDefinitions.DeptAssignment, createMissingAccounts = true)

    // run the recon manager to flush out changes - this triggers "real" provisioing to ldap
    new ReconManager().recon(ldapResource,ObjectClass.account)

    val uv2 = UserView("test1")
    uv2.printDebug()

    // compare with known values in flatfile
    assert( uv2.user.employeeId == "9000")

    assert( uv2.hasRoleName("MarketingUser"))

    val ldapAccount = uv2.getResourceObjects(ldapResource)(0)
    assert( ldapAccount("employeeNumber") == "C9000" )


    assert(ldapAccount.isMemberOfGroup(TestData.group(0).accountName))

    // check to see if the user got put in the Marketing Group as a result of the Role Assignment
    //val g = uv2(ldapResource,"member").asInstanceOf[Seq[String]]
    //assert( g.contains(TestData.marketingGroupEntitlement.attrVal))



    // now clean up


  }
}
