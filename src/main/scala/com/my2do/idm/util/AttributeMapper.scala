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

package com.my2do.idm.util

import com.my2do.idm.objects.User
import com.my2do.idm.connector.util.{ConnectorObjectWrapper, ICAttributes}

/**
 *
 * Map input variable to multiple output variables in the user objects
 *
 * User: warren
 * Date: 3/26/11
 * Time: 8:40 PM
 * 
 */


class AttributeMapper(var funcList:List[(User, ICAttributes) => Unit]  = Nil) {
  def doMap(u:User, s: ICAttributes) = {
    funcList.foreach( action => action(u,s) )
  }

  def addMapping(func:((User,  ICAttributes) => Unit) ) = {
    funcList +:= func
  }
}

