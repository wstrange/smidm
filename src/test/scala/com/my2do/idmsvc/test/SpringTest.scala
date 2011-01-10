
package com.my2do.idmsvc.test

import com.my2do.idm.model.User


import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4._
import org.springframework.test.context._
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation ._

import javax.inject._

import org.springframework.transaction.annotation.Transactional

import org.junit.Test;
import com.my2do.idmsvc.UserRepoDAO

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("applicationContext.xml"))
class SpringTest {
	@Autowired var applicationContext:ApplicationContext = _
	
	
	@PersistenceContext
	var em:EntityManager = _
	
	 //@Resource(name ="userRepoDAO")
     @Inject var userRepoDAO:UserRepoDAO = _
     //@Autowired var userRepoDAO:UserRepoDAO = _
	
	
	@Test
	//@Transactional 
	def doTest2() = {
		var u = new User()
		/*
		u.userName = "test"
		em.persist(u)
		em.flush()
		*/
		
		//assert( u.id > 0 )
		println("**** User=" + u.id)
		
		userRepoDAO.save( u)
		
	}
}
