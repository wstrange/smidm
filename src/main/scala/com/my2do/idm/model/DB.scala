package com.my2do.idm.model


import org.squeryl.PrimitiveTypeMode._

import com.my2do.idm.connector.BundleInstance
import org.identityconnectors.framework.api.ConnectorKey
import org.squeryl.{SessionFactory, Session, Schema, KeyedEntity}
import net.liftweb.common.Logger
import reflect.BeanProperty
import org.squeryl.adapters.{OracleAdapter, H2Adapter}

class BaseDbObject extends KeyedEntity[Long] {
  val id: Long = 0
}



object DB extends Schema with Logger {
  val bundles = table[Bundle] ("BUNDLES")
  val repo = table[UserRepo]
  
  val ldapAccounts = table[LDAPAccount]
  
  val ADAccounts = table[ADAccount]
  
  val ldapAccountAssignments = oneToManyRelation(repo, ldapAccounts).
    via((r,l) => r.id === l.repoId)
    
  val ADAccountAssignments = oneToManyRelation(repo, ADAccounts).
    via((r,l) => r.id === l.repoId)
    
  
  on(bundles) (b =>
    declare(
    b.instanceName is(unique,indexed),
    b.configurationPropertiesXML is( dbType("clob"))))
  
  on(repo) ( x => 
   declare(
    x.userName is(unique,indexed)))
  
  //on(repo) ( r => declare(r.userName is )

  override def drop = super.drop


  def initOracle = {
    Class.forName("oracle.jdbc.driver.OracleDriver")
    SessionFactory.concreteFactory = Some( () => createOraSession )
  }


  def initSchema = {
    debug("Creating Schema")
    DB.drop
    DB.create
  }

  def createOraSession = {
   //A Squeryl session is a thin wrapper over a JDBC connection :
    info("Creating SQL Session")
    val s = Session.create(
       java.sql.DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","vidm", "vidm"),
      //java.sql.DriverManager.getConnection("jdbc:h2:mem:","",""),
      //Currently there are adapters for Oracle, Postgres, MySql and H2 :
      new OracleAdapter)

   s.setLogger( { info(_) } )
   s
  }

  def createH2Session = {
   //A Squeryl session is a thin wrapper over a JDBC connection :
    info("Creating SQL Session")
    val s = Session.create(
       java.sql.DriverManager.getConnection("jdbc:h2:~/vidmtest", "sa", ""),

      new H2Adapter)
   s.setLogger( { info(_) } )
   s
  }

}
