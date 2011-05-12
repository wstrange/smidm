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
import com.mongodb.casbah.Imports._
import com.my2do.idm.mongo.{ICFacade, MongoUtil}
import com.my2do.idm.dao.{ResourceDAO, SyncIndexDAO}
import com.my2do.idm.objects.{ObjectClass, ResourceObject, SyncIndex}

/**
 *
 * User: warren
 * Date: 4/17/11
 * Time: 1:34 PM
 *
 */

class ReconManager extends Logger {

  def recon(resource: Resource, objectClass:ObjectClass) = {

    var count = 0

    val q = SyncIndexDAO.findByResourceNeedsSynced(resource,objectClass)

    while (q.hasNext) {
      val ai = q.next
      debug("Sync object=" + ai)
      val obj = resource.dao.findByName(ai.accountName,objectClass).get

      if (ai.delete)
        deleteResourceObject(resource, ai, obj)
      else
      if (updateResourceObject(resource, ai, obj)) count += 1
    }
    count
  }


  def updateResourceObject(resource:Resource, ai: SyncIndex, o: ResourceObject): Boolean = {
    debug("recon account index =" + ai + " obj=" + o)

    val uid = resource.getFacade.save(o)  // save to the real resource using ICF framework
    if (uid.isDefined) {
      ai.needsSync = false
      ai.lastSync = System.currentTimeMillis
      SyncIndexDAO.save(ai)


      // todo: We could optimize this to only save if UID has changed?
      resource.dao.save(o)
      debug("Synced account " + ai.accountName)
      return true
    }
    else
      info("Could not update/create resource " + o)
    false
  }

  def deleteResourceObject(resource:Resource, ai: SyncIndex, obj: ResourceObject) = {
    resource.getFacade.delete(obj)
    resource.dao.remove(obj)
    // todo: Put remove method in AI DAO - so it cascades
    SyncIndexDAO.remove(ai)
  }
}