package com.my2do.idm.sync

import javax.inject.Inject
import com.my2do.repo.UserRepository
import org.springframework.stereotype.Component
import com.my2do.idm.model._
import com.my2do.idm.event.SyncEvent
import com.my2do.idm.rule.{Context, Rule}
import com.my2do.idm.util._
import net.liftweb.common.Logger
import com.my2do.idm.connector.util.ICFAttributes
import scala._
import com.my2do.idm.connector.{ConnectorConfig, ICFWrapper}

/**
 * 
 * User: warren
 * Date: 3/25/11
 * Time: 4:33 PM
 * 
 */

@Component
class SyncManager extends Logger {

  type SyncFunc = (User, ICFAttributes) => Unit

  @Inject var userRepo:UserRepository = _

  type AttributeMap = Map[String,AnyRef]

  def correlateAccountId(id:String):Option[User] = {
    val user = userRepo.findByAccountId(id)
    if( user != null) Some(user)
    else
      None
  }


  /**
   * todo: How to specify correlation rules/
   * what if there are multiple matches?
   */
  def sync(entity:ConnectorEntity ,  mapper:AttributeMapper, attrs:ICFAttributes, createOnMissing:Boolean = true) = {
    info("Sync attrs=" + attrs)
    val uid = attrs.uidAttribute
    correlateAccountId(uid) match {
      case Some(user:User) => updateOp(user,mapper,attrs)
      case None => if( createOnMissing )
                    createAccount(uid, mapper,attrs)
    }
  }

  def updateOp(u:User, mapper:AttributeMapper , attrs:ICFAttributes) = {
    //syncFunc(u,attrs)
    mapper.doMap(u, attrs)
    userRepo.save(u)
  }

  def createAccount(uid:String,mapper:AttributeMapper,attrs:ICFAttributes) = {
    val u = new User()
    u.accountId = uid;
    info("Creating User Account uid=" + uid)
    //userRepo.save(u)
    updateOp(u,mapper,attrs)
  }



  /*
  def updateAccount(user:User,event:SyncEvent,syncFunc:Rule)  = {
    // update the attributes
    // call some Rule?
    // find the rule for this type
    info("Update Account = " +user)
    val c = Context(user,event,"SYNC")
    syncRule.eval(c)
    info("Account After update=" + user)
  }

  def createAccount(event:SyncEvent,syncRule:Rule) = {
    val u = new User()
    u.accountId = event.uid;
    info("Creating User Account =" + u)
    userRepo.save(u)
    updateAccount(u,event,syncRule)
  }

  */
}