package com.my2do.idmsvc

import com.my2do.idm.model.DB
import net.liftweb.common.Logger
import com.my2do.idm.model.UserRepo
import org.squeryl.PrimitiveTypeMode._


/**
 * todo: add auditing here..
 * 
 * @author warren
 *
 */
trait UserServiceComponent  {	
	val userService: UserService 
	
	class UserService extends Logger {
		def saveOrUpdateUser(user: UserRepo) = {
			transaction {
				info("Saving user " + user)     
				DB.repo.insertOrUpdate(user)
			}
		}
		
		def deleteUser(user:UserRepo) = {
			 transaction {
				 info("Deleted user" + user)
				 DB.repo.delete(user.id)
			 }
		}
	}
}