package com.my2do.idm.sync

import javax.inject.Inject

import org.springframework.stereotype.Component
import com.my2do.idm.objects._
import net.liftweb.common.Logger
import com.my2do.idm.connector.util.ICAttributes
import scala._
import com.my2do.idm.mongo.{MongoUtil, ICFacade}
import com.my2do.idm.correlate.AccountRules

//import com.mongodb.casbah.commons.MongoDBObject

import com.mongodb.casbah.Imports._
import com.my2do.idm.dao.{UserDAO, AccountIndexDAO}
import com.my2do.idm.connector.{ConnectorManager, ConnectorConfig}

/**
 *
 * User: warren
 * Date: 3/25/11
 * Time: 4:33 PM
 *
 */

@Component
class SyncManager extends Logger {

  type SyncFunc = (User, ICAttributes) => Unit
  @Inject var connectorManager: ConnectorManager = _

  def loadFromResource(resourceConfig: ConnectorConfig, correlator: AccountRules, createUserIfMissing: Boolean = false): Int = {
    var count = 0
    val facade = connectorManager.getFacade(resourceConfig)
    // todo: should get this from connectorManager..
    val icf = new ICFacade(facade, resourceConfig)
    val collection = MongoUtil.collectionForConnector(resourceConfig)

    icf.foreachAccount {
      a =>
        val name = a.getName
        val nameAttr = MongoUtil.makeNameAttribute(name)
        val result = collection.find(nameAttr)
        val obj = MongoDBObject(a.attributeMap toList)

        if (result.size > 0) {
          info("Account exists - it will not be reloaded. name=" + nameAttr)
        }
        else {
          debug("Saving account object  =" + obj)
          collection.save(obj)
          count += 1
        }

        var user = correlator.correlateUser(a)

        info("Correlated user =" + user)
        if (user == None && createUserIfMissing) {
          user = correlator.createUserFromAccountAttributes(a)
          user match {
            case Some(u: User) => val r = UserDAO.save(u)
            if (!r.ok) {
              error("Error trying to save user:" + r.getErrorMessage)
              user = None
            }
            case None => error("Could not create user from account. attrs=" + obj)
          }
        }
        updateAccountIndex(user, collection.getName, name)
    }
    return count
  }

  def updateAccountIndex(user: Option[User], collectionName: String, accountName: String) = {
    val r = AccountIndexDAO.findByAccountName(collectionName, accountName)

    val userId = user match {
      case Some(u: User) => Some(u.id)
      case None => None
    }

    if (r.count > 1)
      throw new IllegalStateException("Account Index is corrupt - contains multiple matches for the same account: "
        + collectionName + "," + accountName)

    if (r.count == 0) {
      val a = AccountIndex(userId, collectionName, accountName)
      debug("Creating AccountIndex entry a=" + a)
      AccountIndexDAO.save(a)
    }
    else {
      val account = r.next()
      debug("Found existing account=" + account)
      if (account.userId == None && userId.isDefined) {
        account.userId = userId
        info("Linking User " + user + " to account " + account)
        AccountIndexDAO.save(account)
      }
    }
  }


}