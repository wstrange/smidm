package com.my2do.idmsvc


import net.liftweb.common.Logger
import com.my2do.idm.model.UserRepo


import javax.ejb.Stateless;

@Stateless
class UserEJB {
	
	def getUser() = {
		new UserRepo("fred","fred@foo.com")
	}
}
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
			
				info("Saving user " + user)     
				//DB.repo.insertOrUpdate(user)
			
		}
		
		def deleteUser(user:UserRepo) = {
			 
				 info("Deleted user" + user)
				 //DB.repo.delete(user.id)
			
		}
	}
}