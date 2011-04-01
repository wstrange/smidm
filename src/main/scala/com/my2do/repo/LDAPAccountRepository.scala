package com.my2do.repo

import org.springframework.transaction.annotation.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import com.my2do.idm.model.LDAPAccount

/**
 * 
 * User: warren
 * Date: 3/31/11
 * Time: 11:07 AM
 * 
 */

@Transactional(readOnly = true)
abstract trait LDAPAccountRepository extends JpaRepository[LDAPAccount, java.lang.Long]  {
  def findByAccountUid(Uid: String): LDAPAccount
}