package com.my2do.idm.correlate



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