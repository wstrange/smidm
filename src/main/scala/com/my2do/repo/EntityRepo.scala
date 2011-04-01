package com.my2do.repo

import com.my2do.idm.model.ConnectorEntity

/**
 * 
 * User: warren
 * Date: 3/31/11
 * Time: 10:04 AM
 * 
 */

trait EntityRepo {
  def findbyAccountUid[T <: ConnectorEntity](uid:String):T
}