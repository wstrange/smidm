package com.my2do.idm.model

import scala.reflect.BeanProperty
import org.squeryl.annotations.{Column}



/**
 * An LDAP account
 * how do we map this to a connector schema? Annotations or a map? 
 * @author warren
 *
 */
class LDAPAccount(val accountId:String,
		val repoId:Long,
		@Column("LDAP_UID") 
		@BeanProperty var uid:String, 
		@BeanProperty var cn:String) extends Account(accountId,repoId) {
}


class LDAPGroup extends BaseDbObject {
	 
}

class ADAccount(val accountId:String,
		val repoId:Long,
		@Column("AD_UID") 
		@BeanProperty var cn:String) extends Account(accountId,repoId) {
}