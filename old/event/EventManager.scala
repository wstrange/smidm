package com.my2do.idm.event

import com.my2do.idm.rule._
import collection.mutable.{HashMap, HashSet}


/**
 * 
 * User: warren
 * Date: 3/24/11
 * Time: 1:30 PM
 * 
 */

object EventManager  {

  val lock = new Object()

  val rulesByTarget =  new HashMap[SyncTarget,Rule]
  val rulesByResource= new HashMap[String,HashSet[Rule]]
  val rulesByAttributeName = new HashMap[String,HashSet[Rule]]


  def addRule(r:Rule, target:SyncTarget) = {
    // we sync just on rule addition
    // in theory rules will be created once at startup time - and probably not concurrently
    lock.synchronized {
      rulesByTarget.put(target,r)
      val rset= rulesByResource.getOrElseUpdate(target.resource, new HashSet[Rule]())
      rset += r
      val rset2 = rulesByAttributeName.getOrElseUpdate(target.attributeName,new HashSet[Rule]())
      rset2 += r
    }
  }

  def fireEvent(c:Context, t:SyncTarget)  = {

    var rules:Set[Rule] = Set()

    if( ! t.isWildCardPresent ) { //exact match
      val rule = rulesByTarget.getOrElse(t, ExceptionRule)
      rules += rule
    }
    else
    if( t.isAttributeWildCard ) {
      val rset = rulesByResource.getOrElse(t.resource, HashSet(ExceptionRule))
      rules = rules ++ rset
    }
    else
    if( t.isResourceWildCard) {
      val rset = rulesByAttributeName.getOrElse(t.attributeName, HashSet(ExceptionRule))
      rules = rules ++ rset
    }

    rules.foreach( rule =>  rule.eval(c))

  }



}