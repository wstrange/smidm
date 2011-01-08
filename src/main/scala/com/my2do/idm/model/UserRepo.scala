package com.my2do.idm.model

import scala.reflect.BeanProperty

import java.util.Date


/**
 * The Identity Repo Table
 * @author warren
 *
 */
class UserRepo(	@BeanProperty var userName:String,
			@BeanProperty var userEmail: String
			) extends BaseDbObject {
	def this() = this("","")
	
	
	@BeanProperty var firstName:String = "_"
	@BeanProperty var lastName:String = "_"
	@BeanProperty var employeeId:String= "_"
		
	lazy val ldapAccounts = DB.ldapAccountAssignments.left(this)
	lazy val ADAccounts = DB.ADAccountAssignments.left(this)
	/*
	def allAccounts:List[Account] = {
		
		ldapAccounts.toList :: ADAccounts.toList
	}
	*/

}

class Account(accountId:String, repoId:Long)  extends BaseDbObject

/** 
 * Link a user to an Account. A user can own more than one account on a resource
 * @author warren
 *
 */
class AccountLink( val userId: Long, val accountId: Long, val dateLinked:Date )

//class ResourceData(val accountType:Long, val ) extends BaseDbObject

//class AccountAttribute(val accountId:Long, val attributeId:Long, val attrVal:String)
