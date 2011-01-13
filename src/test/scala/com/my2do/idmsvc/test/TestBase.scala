package com.my2do.idmsvc.test

import org.junit.Ignore
import org.junit.Test
import com.my2do.idmsvc.UserRepoDAO
import javax.inject.Inject



import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4._
import org.springframework.test.context._
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation ._

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/applicationContext.xml"))
class TestBase {
	@Autowired var applicationContext:ApplicationContext = _
		
	@PersistenceContext
	var em:EntityManager = _
	
	def getEntityManager() = em
	
	 //@Resource(name ="userRepoDAO")
     @Inject var userRepoDAO:UserRepoDAO = _
     //@Autowired var userRepoDAO:UserRepoDAO = _
     
     
     @Ignore
     @Test
     def doNothing() = {}
    
     
}