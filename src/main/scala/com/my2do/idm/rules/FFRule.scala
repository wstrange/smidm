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
import net.liftweb.common.Logger

/**
 *
 * User: warren
 * Date: 4/7/11
 * Time: 4:52 PM
 *
 */

object FFRule extends AccountRule with Logger {


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