package com.my2do.idm.rule

import com.my2do.idm.objects.User

/**
 *
 * Context passed to rule eval function
 * User: warren
 * Date: 3/25/11
 * Time: 10:55 AM
 * 
 */




case class Context(val user:User, val event:Event, val principal:String = "SYSTEM") {

}