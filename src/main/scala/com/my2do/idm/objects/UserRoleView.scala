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

package com.my2do.idm.objects

import collection.mutable.HashMap
import com.mongodb.casbah.Imports._
import net.liftweb.common.Logger
import com.my2do.idm.resource.Resource
import com.my2do.idm.dao.RoleDAO

/**
 * Trait that manages the view of the roles associated with a user profile
 * This does  NOT manage roles themselves - just the association of users to roles
 *
 * User: warren
 * Date: 4/18/11
 * Time: 10:16 AM
 * 
 */

trait UserRoleView extends Logger {  this: UserView =>
  var roleMap= new HashMap[ObjectId,Role]
  //val user:User // must be supplied by enclosing class

  def refreshRoleView = {
     roleMap.clear    // from RoleView Trait
     user.roleIdList.foreach( id => roleMap.put(id, RoleDAO.findOneByID(id).get))
  }

  def addRole(role:Role) = {
    val resourceKeys = role.assignedResourceKeys

    debug("User obj=" + user)
    resourceKeys.foreach{ key =>
      if( user.roleAssignedResources.contains(key) || user.directlyAssignedResources.contains(key)) {
        //debug("Resource key is already assigned. key=" + key)
        // skip resource assignment..
        // todo: Need to fix this - because the role assignmnet needs to record which role caused the assignment?
      }
      else {
        user.roleAssignedResources = key :: user.roleAssignedResources
        debug("Adding resource key=" + key)
        val resource = Resource.getResourceByInstanceKey(key).get
        ensureHasResource(resource,false)
        user.roleAssignedResources = resource.resourceKey :: user.roleAssignedResources
      }
    }
    applyEntitlements(role)
    roleMap.put(role.id,role)
    user.roleIdList = (role.id :: user.roleIdList).distinct
  }

  def applyEntitlements(role:Role) = {
    role.entitlements.foreach{ e =>
      resourceObjectsForResourceKey(e.resourceKey).foreach{ case(ai,ro) =>
        e.assign(ro)
      }
    }
  }

}