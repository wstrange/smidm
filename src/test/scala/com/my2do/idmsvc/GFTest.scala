package com.my2do.idmsvc

import com.my2do.idm.model._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.junit._

import org.junit.Test;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;


import com.my2do.idm.model._

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import net.liftweb.common.Logger


//@RunWith(classOf[JUnitRunner])
class GFTest extends JUnitSuite  with Logger {
   @PersistenceContext var em:EntityManager = _
   
   @Inject var utx: UserTransaction = _
   
   var ejbContainer:EJBContainer = _
   var ctx:Context = _
   
	
   
    def startUp() = {
	    val props = new java.util.Properties();
	    props.put(EJBContainer.MODULES, new java.io.File("target/classes"));
        props.put("org.glassfish.ejb.embedded.glassfish.installation.root", "glassfish");
    	ejbContainer = EJBContainer.createEJBContainer(props);
    	ctx = ejbContainer.getContext();
    	
    }
    	
  
	
  @Test
  def testDB() = {
	  
	  info("testDB")
	  startUp() 
	  val userEJB = ctx.lookup("java:global/classes/UserEJB!com.my2do.idm.model.UserEJB");
	  assert(userEJB != null);
   }

    

  
  

}

