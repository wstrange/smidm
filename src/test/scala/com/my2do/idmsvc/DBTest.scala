package com.my2do.idmsvc

import com.my2do.idm.model._

import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
import org.scalatest.junit._

import org.junit.Test;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import com.my2do.idm.model._

import net.liftweb.common.Logger



//@RunWith(classOf[JUnitRunner])
@RunWith(classOf[Arquillian])
class DBTest extends JUnitSuite  with Logger {
   @PersistenceContext var em:EntityManager = _
   
   @Inject var utx: UserTransaction = _
	
	@Deployment
	def createDeployment()= {
	   
	   debug("Creating deployment..")
		ShrinkWrap.create(classOf[WebArchive], "test.war")
            .addPackage(classOf[Bundle].getPackage())
            .addManifestResource("test-persistence.xml", "persistence.xml")
            .addWebResource(EmptyAsset.INSTANCE, "beans.xml")
	}
  
	
  //@Test
  def testDB() = {
	  
	  info("testDB")
      utx.begin();
      em.joinTransaction();
    
      
      //printStatus("Clearing the database...");
      //em.createQuery("delete from Game").executeUpdate();
    
      //printStatus("Inserting records...");
      val b = new Bundle()
      em.persist(b)
    
      utx.commit();
   }

    

  
  

}

