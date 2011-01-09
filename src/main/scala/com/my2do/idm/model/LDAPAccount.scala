package com.my2do.idm.model

import scala.reflect.BeanProperty


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * An LDAP account
 * how do we map this to a connector schema? Annotations or a map? 
 * @author warren
 *
 */
class LDAPAccount(val accountId:String,
		val repoId:Long,
		
		@BeanProperty var uid:String, 
		@BeanProperty var cn:String) extends Serializable {
}
