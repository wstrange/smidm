package com.my2do.idm.correlate

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.objects.User
/**
 *
 * User: warren
 * Date: 4/7/11
 * Time: 4:52 PM
 *
 */

object FFRules extends AccountRules {

  /**
   * Correlate on employeeNumber
   */
  override def correlateUser(attrs: ICAttributes): Option[User] = {
    debug("Correlating attrs=" + attrs)
    UserDAO.findByAccountName(attrs.getName)
  }

  override def createUserFromAccountAttributes(a: ICAttributes): Option[User] = {
    debug("create user from attrs=" + a)
    val accountName = a.getUuid
    val firstName = a.asString("firstName")
    val lastName = a.asString("lastName")
    val email = a.asString("email")
    val empid = a.asString("employeeId")

    // check for unique empId?

    Some(User(accountName, firstName, lastName, email = email,employeeId = empid))
  }

}