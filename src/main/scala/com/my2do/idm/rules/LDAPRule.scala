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

package com.my2do.idm.rules

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.objects.{ResourceObject, User}

/**
 *
 * User: warren
 * Date: 4/7/11
 * Time: 4:52 PM
 *
 */

object LDAPRule extends AccountRule {

  override def createUserFromAccountAttributes(a: ICAttributes): Option[User] = {
    val accountName = a.firstValueAsString("uid")
    val givenName = a.firstValueAsString("givenName")
    val lastName = a.firstValueAsString("sn")
    val empId = a.firstValueAsString("employeeNumber")


    // check for unique empId?
    val existingU = UserDAO.findByEmployeeId(empId)
    if (existingU != None) {
      error("An exisiting user exists with thesame employee id!. id=" + empId)
      return None
    }
    Some(User(accountName, givenName, lastName, empId))
  }

  /**
   * Create a template LDAP user
   */
  override def newResourceObject(u: User) = {

    val attrs = Map("uid" -> u.accountName,
      "cn" -> (u.firstName + " " + u.lastName),
      "sn" -> List(u.lastName),
      "employeeNumber" -> u.employeeId,
      "departmentNumber" -> u.department,          // not in ldap schema
      "givenName" -> List(u.firstName))

    val name = "uid=" + u.accountName + ",ou=People,dc=example,dc=com"

    // todo: Fix the __ACCOUNT__ hardwired....
    ResourceObject(name, "__ACCOUNT__", u.accountName, attrs)
  }

}