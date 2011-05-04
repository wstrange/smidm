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
import com.my2do.idm.dao.ResourceDAO
import com.my2do.idm.objects.{ObjectClass}

/**
 * 
 * User: warren
 * Date: 4/12/11
 * Time: 5:47 PM
 * 
 */

object Resource {



  val flatfile1 = new Resource("flatfile1", FlatFile_TestFile1, FFRule)
  val ldapTest = new Resource("LdapTest", LDAP_Test, LDAPRule, List(ObjectClass.account, ObjectClass.group))

  val resourceList:List[Resource] = List(flatfile1,ldapTest)

  def getResourceByInstanceKey(key:String) = Resource.resourceList.find{ r =>
    println("r=" + r)
    if( r == null ) false
      else key.equals(r.config.instanceKey)}
}

/**
 *
 */
class Resource(val instanceName:String,val config:ConnectorConfig,val rule:AccountRule,
               val supportedObjectClasses:List[ObjectClass] = List(ObjectClass.account)) {

  private val facade =  ComponentRegistry.connectorManager.getFacade(config)
  private val icfacade = new ICFacade(facade,config)

  val manageGroups = supportedObjectClasses.contains(ObjectClass.group)


  def collectionForObjectClass(o:ObjectClass) =  MongoUtil.db(o.name + config.instanceKey)


  val dao = new ResourceDAO(this)

  def resourceKey = config.instanceKey

  //def resourceKey(o:ObjectClass) = collectionForObjectClass(o).name

  def getFacade = icfacade

  override def toString:String = "Resource(" + instanceName + ", blah blah, objectclasses=" + supportedObjectClasses


}