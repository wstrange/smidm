package com.my2do.idm.test

/**
 *
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 *
 */

import com.my2do.idm.objects._

import com.my2do.idm.mongo.MongoUtil

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.resource.Resource
import com.my2do.idm.sync.{ReconManager, SyncManager}

class ReconTest extends FunTest {
  val reconManager = new ReconManager()
  val resource = Resource.ldapTest

  test("Basic recon Test") {
    MongoUtil.dropAndCreateDB  // for testing create a fresh repo
    // Create a test user to correlate against
    val uv = new UserView(User("test1", "Test", "Tester", employeeId = "99"))
    // directly assign the ldap resource.
    uv.ensureHasResource(resource)
    uv.flush()


    // run the recon manager
    // this will trigger sync out to the real resource
    // check the ldap logs to see the create
    val count = reconManager.recon(resource)
    uv.refreshView

    assert(count == 1) // how many accounts were reconed

    // lets unassign the resource
    uv.removeResource(resource)
    assert( ! uv.user.isResourceDirectlyAssigned(resource))
    uv.flush()

    // run recon manager again - will trigger delete of the account
    reconManager.recon(resource)
  }
}
