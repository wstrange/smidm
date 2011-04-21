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

package config

import org.identityconnectors.framework.api.ConnectorKey
/**
 * Configuration Base Class - auto generated
 * Subclass this and override any local connector parameters for each unique instance
 */
abstract class FlatFileConnectorDefaultConfig extends com.my2do.idm.connector.ConnectorConfig {
  override val connectorKey = new ConnectorKey("org.identityconnectors.flatfile", "1.0.2838", "org.identityconnectors.flatfile.FlatFileConnector")


  // textQualifier.display - textQualifier.help
  val textQualifier = '"'
  // fieldDelimiter.display - fieldDelimiter.help
  val fieldDelimiter = ','
  // Filename - File in the connector will attach to deliver changes.
  val file: java.io.File = null
  // uniqueAttributeName.display - uniqueAttributeName.help
  val uniqueAttributeName = ""
  // encoding.display - encoding.help
  val encoding = "UTF-8"
}
