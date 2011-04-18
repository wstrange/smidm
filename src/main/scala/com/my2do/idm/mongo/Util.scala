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

package com.my2do.idm.mongo

import com.mongodb.casbah.commons.MongoDBObject
import org.identityconnectors.framework.common.objects.ConnectorObject
import com.my2do.idm.connector.util.ICAttributes

/**
 * 
 * User: warren
 * Date: 4/2/11
 * Time: 1:22 PM
 * 
 */

class Util  {

  def toMongoObject(a:ICAttributes) = {
    val  builder = MongoDBObject.newBuilder
    a.attributeMap.foreach{ case(attrname,attrValue) => builder += (attrname -> attrValue) }
    builder.result
  }
}