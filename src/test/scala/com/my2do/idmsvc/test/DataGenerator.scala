package com.my2do.idmsvc.test

import com.my2do.idm.model._

/**
 * 
 * User: warren
 * Date: 3/24/11
 * Time: 2:46 PM
 * 
 */

object DataGenerator  {

  def createSampleUser(name:String, firstName:String = "First", lastName:String = "last") = {
    val u = new User()
    u.accountId = name
    u.firstName = firstName
    u.lastName = lastName

    val ldap = new LDAPAccount()
    ldap.uid = name
    ldap.user = u
    ldap.bundle = null  // todo: Associate with Resource

    u.ldapAccounts.add(ldap)
    u
  }
}