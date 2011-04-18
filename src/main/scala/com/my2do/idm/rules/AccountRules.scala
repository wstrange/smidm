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

import com.my2do.idm.objects.User
import com.my2do.idm.dao.UserDAO

import com.my2do.idm.connector.util.ICAttributes
import net.liftweb.common.Logger
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.my2do.idm.mongo.MongoUtil

/**
 *
 * Basic functions to correlate users against an account, create a user object from account attributes
 * and create a stub account given a user object
 *
 * Subclasses will override this class and implement rules appropriate for the account type
 *
 * User: warren
 * Date: 4/7/11
 * Time: 4:35 PM
 * 
 */

trait AccountRules extends Logger {

  /**
   * Attempt to correlate this account to a user
   * the default is to correlate on Account Name
   * ConnectorConfig Classes should mix this trait in and override the implementation to be specfic to their attributes
   * For example - the ldap connector might try to correlate on first/lastname , employee number, etc.
   *
   */
  def correlateUser(attrs:ICAttributes):Option[User] = {
    UserDAO.findByAccountName(attrs.getName)
  }

  /**
   * Given a set of account attributes - create the base account.
   * The default is to create the account using the account name
   * This shouild be overridden in Config
   *
   * @return an Option[User] or None if the user can not be created
   */

  def createUserFromAccountAttributes(a:ICAttributes):Option[User] = {
    Some(User(a.getName,"",""))
  }

  def newResourceObject(u:User):DBObject = MongoUtil.makeNameAttribute(u.accountName)
}