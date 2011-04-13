package com.my2do.idm.rule


/**
 * Rule to trigger some action
 *  Actions could include - assignment of an attribute / desassignment of an attribute, etc.
 * User: warren
 * Date: 3/25/11
 * Time: 10:53 AM
 * 
 */

class Rule(val name:String, val actionFunction: ( Context => Any), val description:String = "" ) {
  def apply(c:Context) = eval(c)
  def eval(c:Context) = actionFunction(c)

}


object ExceptionRule extends Rule("NullRule",
  { c:Context => throw new IllegalStateException("No Matching rule found =" + c)})
