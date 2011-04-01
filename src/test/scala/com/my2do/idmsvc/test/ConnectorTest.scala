package com.my2do.idmsvc.test

/**
 *
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 *
 */
import scala.collection.JavaConversions._

import java.io.File
import org.identityconnectors.framework.common.objects.{ConnectorObject, ObjectClass, ResultsHandler}
import org.junit.{Before, Test}
import com.my2do.idm.model.LDAPAccount
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import com.my2do.idm.connector._
import config._
import org.identityconnectors.common.logging.Log
import util.SLF4JLogger
import javax.inject.Inject
import com.my2do.repo._
import com.my2do.svc.UserRepoService
import com.my2do.idm.model.{User, ConnectorEntity }
import org.junit.Assert._

class ConnectorTest extends TestBase {


  @Inject
  var userSvc: UserRepoService = _

  @Inject
  var entityRepo: LDAPAccountRepository = _


  //@Test
  def testFlatFile() = {
     val icf = FlatFile_TestFile1.getICFWrapper

    icf.foreachAccount{ obj:ConnectorEntity =>
      println("Got object=" + obj )
      true
    }
  }

  //@Test
  @Transactional
  def testRecon()= {

    val icf = LDAP_Prod.getICFWrapper

    icf.foreachAccount {  entity:ConnectorEntity  =>
        val ldap = entity.asInstanceOf[LDAPAccount]
        // create ldap object from connector objects

        ldap.unmarshall()
        info("LDAP Account obj=" + ldap)
        //info("Sync LDAP uid=" + entity.syncAttributes.uid)
        ldap.snycRequired = false
        // flush do db
        em.persist(ldap)
        em.flush()
        // now go the other way -
        // convert a pojo to a collection of connector attributes
        //val attrs = ldap.getConnectorAttributes
        //println("Attrs = "+attrs)
    }
  }


  @Test
  def testCreate() = {
    val l = new LDAPAccount()

    l.uid = "test123"
    l.cn = "Test Tester"
    l.givenName = "Test"
    l.sn = "Tester"

    val icf = LDAP_Prod.getICFWrapper

    info("Create ldap account " + l)
    icf.create(l)

    val l2 = new LDAPAccount()
    l2.accountUid = l.accountUid

    icf.getEntity(l.accountUid, classOf[LDAPAccount]) match {
      case Some(e:LDAPAccount)  =>  println("Found entry =" +e)
      case None => fail("Could not read object back")
    }
    // todo: Search for it back again

    //now delete
    info("Delete ldap account " + l)
    icf.delete(l)
  }
}

