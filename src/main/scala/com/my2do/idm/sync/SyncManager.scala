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

package com.my2do.idm.sync

import com.my2do.idm.objects._
import net.liftweb.common.Logger
import com.my2do.idm.connector.util.ICAttributes
import scala._
import com.my2do.idm.resource.Resource
import com.my2do.idm.dao.{ResourceDAO, UserDAO, AccountIndexDAO}
import com.my2do.idm.rules.{RoleAssignmentRule, CorrelationRule}

/**
 *
 * User: warren
 * Date: 3/25/11
 * Time: 4:33 PM
 *
 */

class SyncManager extends Logger {

  type SyncFunc = (User, ICAttributes) => Unit


  def loadFromResource(resource: Resource, rule: CorrelationRule, createUserIfMissing: Boolean = false): Int = {
    var count = 0

    val icf = resource.getFacade

    val collection = resource.mongoCollection

    val rdao = ResourceDAO(resource)

    icf.foreachAccount {
      a =>
        val name = a.getName


        rdao.findByAccountName(name) match {
          case Some(x: ResourceObject) =>
            info("Account exists - it will not be reloaded. account=" + name)
          case _ => // does not exist - create a new entry
            val o = ResourceObject(name, "__ACCOUNT__", a.getUuid, attributes = a.attributeMap)
            debug("Saving resource object  =" + o)
            rdao.save(o)
            count += 1
        }

        var user = rule.correlate(a)

        info("Correlated user =" + user)
        if (user == None && createUserIfMissing) {
          user = resource.rule.createUserFromAccountAttributes(a)
          user match {
            case Some(u: User) =>
              u.directlyAssignedResources  = resource.resourceKey :: u.directlyAssignedResources
              info("Creating user from resource account. User=" + u)
              val r = UserDAO.save(u)
              if (!r.ok) {
                error("Error trying to save user:" + r.getErrorMessage)
                user = None
              }
            case None => error("Could not create user from account. attrs=" + a)
          }
        }
        updateAccountIndex(user, resource, name)
    }
    return count
  }

  def updateAccountIndex(user: Option[User], resource: Resource, accountName: String) = {
    val r = AccountIndexDAO.findByAccountName(resource.resourceKey, accountName)

    val userId = user match {
      case Some(u: User) => Some(u.id)
      case None => None
    }

    if (r.count > 1)
      throw new IllegalStateException("Account Index is corrupt - contains multiple matches for the same account: "
        + resource.resourceKey + "," + accountName)

    if (r.count == 0) {
      val a = AccountIndex(userId, resource.resourceKey, accountName, needsSync = false)
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

  def sync(resource: Resource, transformFunction: (UserView, ICAttributes) => Unit,
           correlationRule: CorrelationRule,
           roleAssignmentRule: RoleAssignmentRule,
           createMissingAccounts: Boolean = true) = {

    resource.getFacade.foreachAccount {
      a: ICAttributes =>
        var user = correlationRule.correlate(a)
        if (user.isEmpty && createMissingAccounts)
          user = resource.rule.createUserFromAccountAttributes(a)

        if (user.isDefined) {
          val u = new UserView(user.get)
          transformFunction(u, a)
          val (rolesToAdd, rolesToRemove) = roleAssignmentRule.evaluateRoles(u, a.attributeMap)
          debug("Roles to add=" + rolesToAdd + " Roles to remove =" + rolesToRemove)
          u.flush()
        }
        else
          info("Skipped update because no user correlation found. attrs=" + a)
    }
  }


}