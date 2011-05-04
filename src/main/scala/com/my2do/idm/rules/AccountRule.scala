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


import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.objects.{ResourceObject, User}

/**
  *
  * Trait that defines how to:
  *
  * - create a template/skeleton resource object from a user repo entry
  *
  * Subclasses will override this class and implement rules appropriate for the account type
  *
  * User: warren
  *
  */

trait AccountRule {


  /**
    * Given a set of account attributes - create the base account.
    * The default is to create the account using the account name
    * This should be overridden for most resource types
    *
    * @return an Option[User] or None if the user can not be created
    */

  def createUserFromAccountAttributes(a: ICAttributes): Option[User] = {
    Some(User(a.getName, "", ""))
  }

  def newResourceObject(u: User): ResourceObject = null // default - no object is created
}