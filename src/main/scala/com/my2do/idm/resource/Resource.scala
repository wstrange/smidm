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

package com.my2do.idm.resource

import com.my2do.idm.connector.{ConnectorConfig}
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.mongo.{MongoUtil, ICFacade}
import config.{LDAP_Test, FlatFile_TestFile1}
import com.my2do.idm.rules.{LDAPRule, FFRule, AccountRule}

/**
 * 
 * User: warren
 * Date: 4/12/11
 * Time: 5:47 PM
 * 
 */

object Resource {

  val resourceList:List[Resource] = List(
    new Resource("flatfile1", FlatFile_TestFile1, FFRule,"ff1"),
    new Resource("LDAPProd", LDAP_Test, LDAPRule,"ldap1"))

  val flatfile1 = new Resource("flatfile1", FlatFile_TestFile1, FFRule,"ff1")
  val ldapTest = new Resource("LDAPProd", LDAP_Test, LDAPRule,"ldap1")

  def getResourceByInstanceKey(key:String) = Resource.resourceList.find{ r =>
    println("r=" + r)
    if( r == null ) false
      else key.equals(r.config.instanceKey)}

}

class Resource(val instanceName:String, val config:ConnectorConfig,val rule:AccountRule, id:String ) {

  private val facade =  ComponentRegistry.connectorManager.getFacade(config)
  private val icfacade = new ICFacade(facade,config)
  val mongoCollection =  MongoUtil.collectionForResource(this)

  def resourceKey = config.instanceKey

  def getFacade = icfacade

  def collectionName = mongoCollection.getName

}