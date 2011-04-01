package com.my2do.repo

import org.springframework.data.jpa.repository.JpaRepository
import com.my2do.idm.model.User
import org.springframework.transaction.annotation.Transactional
import java.{ util => ju}

/**
 * 
 * User: warren
 * Date: 3/23/11
 * Time: 3:34 PM
 * 
 */

@Transactional(readOnly = true)
trait UserRepository  extends JpaRepository[User,java.lang.Long]   {
  def findByLastName(lastName: String): ju.List[User]
  def findByAccountId(accountId: String): User
}
