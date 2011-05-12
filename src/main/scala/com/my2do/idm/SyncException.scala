/*
 * Copyright (c) 2011 Warren Strange
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

package com.my2do.idm


/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 12/05/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */

class SyncException(msg:String, t:Throwable) extends RuntimeException(msg,t) {
  def this(msg:String) = this(msg,null)
  def this(t:Throwable) = this(null,t)
}