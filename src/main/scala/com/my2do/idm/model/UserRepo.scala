package com.my2do.idm.model

import scala.reflect.BeanProperty

import java.util.Date

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The Identity Repo Table
 * @author warren
 *
 */

class UserRepo(	@BeanProperty var userName:String,
			@BeanProperty var userEmail: String
			)  {
	def this() = this("","")
	
	
	@BeanProperty var firstName:String = "_"
	@BeanProperty var lastName:String = "_"
	@BeanProperty var employeeId:String= "_"
		
	

}


