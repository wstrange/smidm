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

package com.my2do.idm.test

import org.scalatest.FunSuite
import com.my2do.idm.ComponentRegistry
import com.my2do.idm.connector.util.SLF4JLogger
import org.identityconnectors.common.logging.Log
import net.liftweb.common.Logger

/**
 *
 * User: warren
 * Date: 4/12/11
 * Time: 8:05 PM
 *
 */

// add this to the launch config:
// -Dorg.identityconnectors.common.logging.class=com.my2do.idm.connector.util.SLF4JLogger

class FunTest extends FunSuite with Logger {

  val connectorManager = ComponentRegistry.connectorManager


  val loggerName = classOf[SLF4JLogger].getCanonicalName  // logger to use for ICF
  // unfortuneatly this does not seem to work. Gets set too late in the cycle
  System.setProperty(Log.LOGSPI_PROP,  loggerName)
  // this works...
  debug("To use slf4j for ICF Logging use -D" +Log.LOGSPI_PROP +"=" +  loggerName)

}