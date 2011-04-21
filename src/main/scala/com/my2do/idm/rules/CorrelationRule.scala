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

package com.my2do.idm.rules

import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.objects.User
import com.my2do.idm.dao.UserDAO
import com.mongodb.casbah.Imports._
import net.liftweb.common.Logger

/**
 * 
 * User: warren
 * Date: 4/20/11
 * Time: 4:06 PM
 * 
 */

trait CorrelationRule {
  def correlate(a:ICAttributes):Option[User]
}

class CorrelateByAttribute(icfAttrName:String,userAttrName:String) extends CorrelationRule with Logger {
  def correlate(a:ICAttributes) = {
   try {
      var x = a.value(icfAttrName)
      if (x != null )  {
        val r = UserDAO.find( MongoDBObject( userAttrName -> x))
        if( r.size > 1)
            throw new IllegalStateException("Correlation rule matched more than one User matched=" + r.size)
        if( r.hasNext) Some(r.next) else None
      }
      else
        None
    }
    catch {
      case _ => None
    }
  }
}

object correlateByEmployeeNumber extends CorrelateByAttribute("employeeNumber","employeeId")

object correlateByAccountName extends CorrelationRule {
  def correlate(a:ICAttributes) = UserDAO.findByAccountName(a.getName)
}

object correlateByLDAPUid extends CorrelationRule with Logger {
  def correlate(a:ICAttributes) = {
    try {
      // uid is multi valued.....
      var x = a.firstValueAsString("uid")
      debug("Correlate against username=" + x)
      if (x != null )
        UserDAO.findByAccountName(x)
      else
        None
    }
    catch {
      case _ => None
    }
  }
}



