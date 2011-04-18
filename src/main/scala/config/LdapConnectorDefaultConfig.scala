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
import org.identityconnectors.framework.common.objects.ObjectClass
import org.identityconnectors.framework.api.ConnectorKey
import com.my2do.idm.connector.ConnectorConfig
/**
  * Configuration Base Class - generated
  * Subclass this and override any local connector parameters for each unique instance
  */
abstract class LdapConnectorDefaultConfig extends ConnectorConfig {
   override val connectorKey = new ConnectorKey("org.identityconnectors.ldap","1.0.5754openidm","org.identityconnectors.ldap.LdapConnector")

   // LDAP Filter for Accounts to Synchronize - An optional LDAP filter for the objects to synchronize. Because the change log is for all objects, this filter updates only objects that match the specified filter. If you specify a filter, an objects will be synchronized only if it matches the filter and includes a synchronized objects class.
   val accountSynchronizationFilter = ""
   // Password Attribute to Synchronize - The name of the password attribute to synchronize when performing password synchronization.
   val passwordAttributeToSynchronize = ""
   // Enable Password Synchronization - If true, the connector will synchronize passwords. The Password Capture Plugin needs to be installed for password synchronization to work.
   val synchronizePasswords = false
   // Remove Log Entry Object Class from Filter - If this property is set (the default), the filter used to fetch change log entries does not contain the "changeLogEntry" objects class, expecting that there are no entries of other objects types in the change log.
   val removeLogEntryObjectClassFromFilter = true
   // Filter Out Changes By - The names (DNs) of directory administrators to filter from the changes. Changes with the attribute "modifiersName" that match entries in this list will be filtered out. The standard value is the administrator name used by this adapter, to prevent loops. Entries should be of the format "cn=Directory Manager".
   val modifiersNamesToFilterOut = Array("")
   // Password Decryption Key - The key to decrypt passwords with when performing password synchronization.
   val passwordDecryptionKey = null
   // Password - Password for the principal.
   val credentials = new GuardedString("changeme".toCharArray)
   // Change Log Block Size - The number of change log entries to fetch per query.
   val changeLogBlockSize = 100
   // Base Contexts to Synchronize - One or more starting points in the LDAP tree that will be used to determine if a change should be synchronized. The base contexts attribute will be used to synchronize a change if this property is not set.
   val baseContextsToSynchronize = Array("")
   // Attributes to Synchronize - The names of the attributes to synchronize. This ignores updates from the change log if they do not update any of the named attributes. For example, if only "department" is listed, then only changes that affect "department" will be processed. All other updates are ignored. If blank (the default), then all changes are processed.
   val attributesToSynchronize = Array("")
   // Change Number Attribute - The name of the change number attribute in the change log entry.
   val changeNumberAttribute = "changeNumber"
   // Password Decryption Initialization Vector - The initialization vector to decrypt passwords with when performing password synchronization.
   val passwordDecryptionInitializationVector = null
   // Filter with Or Instead of And - Normally the the filter used to fetch change log entries is an and-based filter retrieving an interval of change entries. If this property is set, the filter will or together the required change numbers instead.
   val filterWithOrInsteadOfAnd = false
   // Object Classes to Synchronize - The objects classes to synchronize. The change log is for all objects; this filters updates to just the listed objects classes. You should not list the superclasses of an objects class unless you intend to synchronize objects with any of the superclass values. For example, if only "inetOrgPerson" objects should be synchronized, but the superclasses of "inetOrgPerson" ("person", "organizationalperson" and "top") should be filtered out, then list only "inetOrgPerson" here. All objects in LDAP are subclassed from "top". For this reason, you should never list "top", otherwise no objects would be filtered.
   val objectClassesToSynchronize = Array("inetOrgPerson")
   // TCP Port - TCP/IP port number used to communicate with the LDAP server.
   val port = 389
   // VLV Sort Attribute - Specify the sort attribute to use for VLV indexes on the resource.
   val vlvSortAttribute = "uid"
   // Password Attribute - The name of the LDAP attribute which holds the password. When changing an user's password, the new password is set to this attribute.
   val passwordAttribute = "userPassword"
   // Use Blocks - When performing operations on large numbers of accounts, the accounts are processed in blocks to reduce the amount of memory used by the operation. Select this option to process accounts in blocks.
   val useBlocks = true
   // Maintain POSIX Group Membership - When enabled and a user is renamed or deleted, update any POSIX groups to which the user belongs to reflect the new name. Otherwise, the LDAP resource must maintain referential integrity with respect to group membership.
   val maintainPosixGroupMembership = false
   // Failover Servers - List all servers that should be used for failover in case the preferred server fails. If the preferred server fails, JNDI will connect to the next available server in the list. List all servers in the form of "ldap://ldap.example.com:389/", which follows the standard LDAP v3 URLs described in RFC 2255. Only the host and port parts of the URL are relevant in this setting.
   val failover = Array("")
   // SSL - Select the check box to connect to the LDAP server using SSL.
   val ssl = false
   // Principal - The distinguished name with which to authenticate to the LDAP server.
   val principal = ""
   // Base Contexts - One or more starting points in the LDAP tree that will be used when searching the tree. Searches are performed when discovering users from the LDAP server or when looking for the groups of which a user is a member.
   val baseContexts = Array("")
   // Read Schema - If true, the connector will read the schema from the server. If false, the connector will provide a default schema based on the objects classes in the configuration. This property must be true in order to use extended objects classes.
   val readSchema = true
   // ConnectorEntity Object Classes - The objects class or classes that will be used when creating new user objects in the LDAP tree. When entering more than one objects class, each entry should be on its own line; do not use commas or semi-colons to separate multiple objects classes. Some objects classes may require that you specify all objects classes in the class hierarchy.
   val accountObjectClasses = Array("top", "person", "organizationalPerson", "inetOrgPerson")
   // ConnectorEntity User Name Attributes - Attribute or attributes which holds the account's user name. They will be used when authenticating to find the LDAP entry for the user name to authenticate.
   val accountUserNameAttributes = Array("uid", "cn")
   // Host - The name or IP address of the host where the LDAP server is running.
   val host = "localhost"
   // Group Member Attribute - The name of the group attribute that will be updated with the distinguished name of the user when the user is added to the group.
   val groupMemberAttribute = "uniqueMember"
   // LDAP Filter for Retrieving Accounts - An optional LDAP filter to control which accounts are returned from the LDAP resource. If no filter is specified, only accounts that include all specified objects classes are returned.
   val accountSearchFilter = ""
   // Password Hash Algorithm - Indicates the algorithm that the Identity system should use to hash the password. Currently supported values are SSHA, SHA, SMD5, and MD5. A blank value indicates that the system will not hash passwords. This will cause cleartext passwords to be stored in LDAP unless the LDAP server performs the hash (Netscape Directory Server and iPlanet Directory Server do).
   val passwordHashAlgorithm = ""
   // Use Paged Result Control - When enabled, the LDAP Paged Results control is preferred over the VLV control when retrieving accounts.
   val usePagedResultControl = false
   // Block Size - The maximum number of accounts that can be in a block when retrieving accounts in blocks.
   val blockSize = 100
   // Uid Attribute - The name of the LDAP attribute which is mapped to the Uid attribute.
   val uidAttribute = "entryUUID"
   // Maintain LDAP Group Membership - When enabled and a user is renamed or deleted, update any LDAP groups to which the user belongs to reflect the new name. Otherwise, the LDAP resource must maintain referential integrity with respect to group membership.
   val maintainLdapGroupMembership = false
   // Respect Resource Password Policy Change-After-Reset - When this resource is specified in a Login Module (i.e., this resource is a pass-through authentication target) and the resource's password policy is configured for change-after-reset, a user whose resource account password has been administratively reset will be required to change that password after successfully authenticating.
   val respectResourcePasswordPolicyChangeAfterReset = false
}
