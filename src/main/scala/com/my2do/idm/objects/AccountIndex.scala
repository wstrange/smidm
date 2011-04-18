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

import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import collection.mutable.HashMap
import com.my2do.idm.dao.{ResourceDAO, AccountIndexDAO}
import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.mongo.MongoUtil
import com.my2do.idm.resource.Resource

/**
 * 
 * User: warren
 * Date: 4/2/11
 * Time: 3:07 PM
 * 
 */

case class AccountIndex(var userId:Option[ObjectId] = None,
                        resourceKey:String,
                        accountName:String = null,
                        var needsSync:Boolean = true,
                        var delete:Boolean = false,
                        var lastSync:Long = System.currentTimeMillis,
                        @Key("_id") var id: ObjectId = new ObjectId)  {

  /**
   *  Flag to indicate the index and the resource object need to be flushed
   *  By default, newly created instances are dirty
   *
   */

  var isDirty:Boolean = true
}


