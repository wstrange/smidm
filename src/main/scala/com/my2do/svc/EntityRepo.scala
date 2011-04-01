package com.my2do.svc

import javax.inject.Inject
import com.my2do.repo.LDAPAccountRepository
import com.my2do.idm.AppConfig

/**
 *
 * Demonstrate how to inject a scala object singleton
 * User: warren
 * Date: 3/31/11
 * Time: 12:48 PM
 * 
 */

object EntityRepo  {
  @Inject var ldapRepo:LDAPAccountRepository = _

  AppConfig.inject(EntityRepo)

}