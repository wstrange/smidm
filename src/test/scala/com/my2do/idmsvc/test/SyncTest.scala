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
import org.identityconnectors.framework.common.objects.ConnectorObject
import org.junit.{Before, Test}
import com.my2do.idm.model.LDAPAccount
import org.springframework.transaction.annotation.Transactional
import com.my2do.idm.connector._
import config._
import javax.inject.Inject
import com.my2do.repo._
import com.my2do.svc.UserRepoService
import com.my2do.idm.model._
import com.my2do.idm.sync.SyncManager
import com.my2do.idm.event.SyncEvent
import com.my2do.idm.connector.util._
import com.my2do.idm.rule._
import com.my2do.idm.util.{AttributeMapper, ReflectUtil}


class SyncTest extends TestBase {

  @Inject
  var userSvc: UserRepoService = _

  @Inject
  var entityRepo: LDAPAccountRepository = _

  @Inject
  var syncManager:SyncManager = _


  @Transactional
  @Test
  def testSync()= {

    val icf = FlatFile_TestFile1.getICFWrapper

    // create mapper function
    val mapper = new AttributeMapper(List(
        { (u:User,in:ICAttributes) =>
          u.department = in.asString("department")
          u.firstName = in.asString("firstName")
          u.lastName = in.asString("lastName")
          u.managerEmpId = lookupMgr(in.uidAttribute)
          u.ldapAccounts.foreach( a =>  a.departmentNumber = in.asString("department"))
        }
      ))

    // todo; Fix
    //icf.foreachAccount{ entity:ConnectorEntity => syncManager.sync(entity,mapper)}
  }

  def lookupMgr(u:String) = "5678"
}

