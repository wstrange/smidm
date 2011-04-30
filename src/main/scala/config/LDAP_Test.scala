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

import org.identityconnectors.common.security.GuardedString


object LDAP_Test extends LdapConnectorDefaultConfig {

  override val instanceKey = "ldapProd_apr11"
  //override val host = "localhost"
  override val principal = "cn=Directory Manager"
  override val port = 1389
  override val credentials:GuardedString = new GuardedString("password".toCharArray)
  override val baseContexts = Array("dc=example,dc=com")
}

