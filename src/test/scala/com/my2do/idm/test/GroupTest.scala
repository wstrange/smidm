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
import com.mongodb.casbah.Imports._


class GroupTest extends FunTest {

  val syncManager: SyncManager = ComponentRegistry.syncManager


  test("Load Group objects") {
    MongoUtil.dropAndCreateDB
    //TestData.defineGroups
    TestData.defineUsers


    // Create a test user to correlate against
    //val u = User("test1", "Test", "Tester", employeeId = "99")
    //UserDAO.save(u)

    val resource = Resource.ldapTest

    var count = syncManager.loadGroupsFromResource(resource)

    // todo - test sync back of group objects - test deserialization

    val dao = resource.dao

    //val objs = dao.find( MongoDBObject.empty )
    //objs.foreach { o => debug("Obj=" + "\n\t\t member=" + o.member.get)}


  }


}
