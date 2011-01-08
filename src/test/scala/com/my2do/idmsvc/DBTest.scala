package com.my2do.idmsvc

import com.my2do.idm.model.UserRepo

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._
import com.my2do.idm.model._

@RunWith(classOf[JUnitRunner])
class DBTest  extends BaseTest {

  test("Basic DB Test") {

    transaction  {
    	
      // un-comment to drop/create the schema
      DB.initSchema
      val b =  DB.bundles.insert( new Bundle("name","<somexml/>", "version1.0","connectorName", "instanceName"))

      info("Inserted id=" + b.id)
      val x = DB.bundles.lookup(b.id)
      assert(x.get.id == b.id)
      

      //info("all=" + all)
      Bundle.allBundles.foreach( result => info("Result=" + result))
      DB.bundles.delete(b.id)
      
      val id = new UserRepo("fflinstone", "fred@test.com") 
      //id.employeeId should equal("")
      //id.employeeId = " "
      val r = DB.repo.insert( id )
      
      var accounts = r.ldapAccounts 
      assert( accounts.size == 0)
      
      val ldapAccount = new LDAPAccount("cn=Directory Manager",r.id, "Dir manager","cn=Directory Manager")
      DB.ldapAccounts.insert( ldapAccount)
      
      accounts = r.ldapAccounts 
      assert(accounts.size == 1)
      assert(accounts.head.accountId == ldapAccount.accountId)
      
      
      try { 
    	  DB.repo.delete( r.id)
    	  fail("Expected referential integrity violation")
      }
      catch {
      	case e:Exception =>  //info("OK - expected exception =" + e)
      }
      
      DB.ldapAccounts.delete(accounts.head.id)
      DB.repo.delete( r.id)      
    }

  }
  
  test("User Repo Test") {
	  
	  transaction {
		  val u1 = DB.repo.insert(new UserRepo("test01", "test01@test.com"))
		  val u2 = DB.repo.insert(new UserRepo("test02", "test02@test.com"))
		  assert( u1.id > 0 )
		  assert( u1.userName == "test01")
	  }
  }
  

}

