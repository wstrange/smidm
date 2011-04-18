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
import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource
import com.my2do.idm.dao.RoleDAO


class RoleTest extends FunTest {

  val syncManager: SyncManager = ComponentRegistry.syncManager

  test("Basic Role Test") {
    MongoUtil.dropAndCreateDB
    // Create a test user to correlate against
    val u = User("test1", "Test", "Tester", department = "olddept", employeeId = "99")

    val ldap = Resource.ldapTest
    val ldapkey = ldap.resourceKey
    val e1 = Entitlement(ldap.resourceKey, "department", "newdept", AssignmentType.REPLACE)
    val e2 = Entitlement(ldap.resourceKey, "sn", "Added SN", AssignmentType.MERGE)
    val r1 = Role("TestRole", None, entitlements = List(e1, e2))
    RoleDAO.save(r1)

    var uv = new UserView(u)
    uv.ensureHasResource(ldap)

    // check view for expected old values (before the role is added)
    var dept = uv(ldap, "department")
    var sn1 = uv(ldap, "sn").asInstanceOf[Seq[AnyRef]]

    debug("Dept=" + dept)
    assert("olddept".equals(dept))
    // add the role
    uv.addRole(r1)
    // check that entitlements got set
    dept = uv(ldap, "department")
    assert("newdept".equals(dept))
    var sn = uv(ldap, "sn").asInstanceOf[Seq[String]]
    assert(sn.contains("Added SN"))
    //uv.printAccounts

    uv.flush() // persist
    // re-reconstruct the view - fetches values from store
    uv = UserView(u.id)
    // ensure the entitlements got persisted / restored
    sn = uv(ldap, "sn").asInstanceOf[Seq[String]]
    assert(sn.contains("Added SN"))

    // now remove a role
    //uv2.
  }


}
