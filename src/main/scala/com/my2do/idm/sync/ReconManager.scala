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

import com.my2do.idm.resource.Resource
import net.liftweb.common.Logger
import com.my2do.idm.dao.{ResourceDAO, AccountIndexDAO}
import com.mongodb.casbah.Imports._
import com.my2do.idm.objects.AccountIndex
import com.my2do.idm.mongo.{ICFacade, MongoUtil}

/**
 *
 * User: warren
 * Date: 4/17/11
 * Time: 1:34 PM
 *
 */

class ReconManager extends Logger {

  def recon(resource: Resource) = {
    val icf = resource.getFacade
    var count = 0

    val q = AccountIndexDAO.findByResourceNeedSynced(resource)

    while (q.hasNext) {
      val ai = q.next

      val obj = ResourceDAO.getResourceObject(ai).get

      if( ai.delete )
        deleteResourceObject(icf,ai,obj)
      else
        if( updateResourceObject(icf,ai,obj) ) count += 1
    }
    count
  }


  def updateResourceObject(icf:ICFacade, ai:AccountIndex,o:MongoDBObject):Boolean= {
   debug("recon account index =" + ai + " obj=" + o)

    val uid = icf.save(o)
    if (uid.isDefined) {
      ai.needsSync = false
      ai.lastSync = System.currentTimeMillis
      AccountIndexDAO.save(ai)
      o.putAll(MongoUtil.makeUidAttribute(uid.get))

      // todo: We could optimize this to only save if UID has changed?
      ResourceDAO.saveResourceObject(ai.resourceKey,o)
      debug("Synced account " + ai.accountName)
      return true
    }
    else
      info("Could not update/create resource " +o)
    false
  }

  def deleteResourceObject(icf:ICFacade,ai:AccountIndex,obj:MongoDBObject) = {
    icf.delete(obj)
    ResourceDAO.remove(ai.resourceKey,obj)
    // todo: Put remove method in AI DAO - so it cascades
    AccountIndexDAO.remove(ai)
  }
}