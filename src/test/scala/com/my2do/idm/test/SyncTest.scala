package com.my2do.idmsvc.test

/**
 *
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 *
 */

import org.junit.Test
import org.junit.Assert._
import config._
import javax.inject.Inject

import com.my2do.idm.objects._

import com.my2do.idm.sync.SyncManager
import com.my2do.idm.connector.util._
import com.my2do.idm.util.AttributeMapper
import com.my2do.idm.mongo.{MongoUtil, ICFacade}
import com.my2do.idm.correlate.{FFRules, LDAPRules}
import com.my2do.idm.dao.UserDAO
class SyncTest extends TestBase {

  @Inject var syncManager: SyncManager = _


  //@Test
  def testLoad(): Unit = {

    MongoUtil.dropAndCreateDB
    // Create a test user to correlate against
    val u = User("test1", "Test", "Tester", employeeId = "99")
    UserDAO.save(u)

    var count = syncManager.loadFromResource(LDAP_Prod, LDAPRules, createUserIfMissing = true)

    assertTrue(count > 50)
    // run a second time
    //syncManager.loadFromResource(LDAP_Prod, LDAPCorrelator, createUserIfMissing = true)
    // todo: What do we test?

  }

  @Test
  def testSync(): Unit = {
    MongoUtil.dropAndCreateDB

    val facade = connectorManager.getFacade(FlatFile_TestFile1)
    val icf = new ICFacade(facade, FlatFile_TestFile1)

    icf.foreachAccount {
      a: ICAttributes =>
        var user = FFRules.correlateUser(a)
        if (user.isEmpty) {
          user = FFRules.createUserFromAccountAttributes(a)
        }

        // update user attributes first.
        val u: User = user.get

        // save the user
        UserDAO.save(u)

        u.fetchLinkedAccounts
        u.printAccounts

        val accountsToAdd = List(LDAP_Prod)

        if (!u.hasResourceAccount(LDAP_Prod)) {
          // add an ldap account
          UserDAO.addAccount(u, LDAP_Prod, LDAPRules.newResourceObject(u))
        }


        u.fetchLinkedAccounts
        assertEquals(1, u.accountMap.size)
        u.printAccounts

      // now update accounts
      // see what accounts should be assigned...
      // checkRoles
      // (rolesToAdd,rolesToDelete,existingRoles) = checkRoles
      // (accountsToAdd,accountsToDelete,accconts) = checkAccounts


      // check accounts - to see which one we should have

      //
      // "department" -> List("user.department", "Account_ldapProd.departmentNumber"),

      //doMap("department", "user.department", "Account_ldapProd.departmentNumber" )

      //mapper.doMap(user, a)
      //UserDAO.insert(user)
      //UserDAO.update()

    }
  }


  def lookupMgr(u: String) = "5678"

  val mapper = new AttributeMapper(List(
  {
    (u: User, in: ICAttributes) =>
      u.department = in.asString("department")
      u.firstName = in.asString("firstName")
      u.lastName = in.asString("lastName")
    //u.managerEmpId = lookupMgr(in.getUuid)
    //u.ldapAccounts.foreach( a =>  a.departmentNumber = in.asString("department"))
  }
  ))

}

