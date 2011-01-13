package com.my2do.idmsvc

import org.springframework.stereotype.Service
import javax.inject._
import org.springframework.stereotype.Repository
import javax.annotation.Resource
import com.my2do.idm.model.User
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository

// must define an interface for proxying
trait UserRepoDAO {
	def save(u:User):Unit
	def delete(u:User):Unit
}

@Named
@Transactional
class UserRepoDAOImpl extends UserRepoDAO {
	 @PersistenceContext
	 var em:EntityManager = _
    
    def save(u:User) = {
		 em.persist(u)
		 em.flush()
	 }
	 
	def delete(u:User) = {
		val o = em.merge(u)
		em.remove(o)
		em.flush()
	}
    
}
