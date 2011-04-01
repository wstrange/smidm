package com.my2do.svc

import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import com.my2do.idm.model.User
import javax.inject.Inject
import com.my2do.repo.UserRepository


/**
 * 
 * User: warren
 * Date: 3/23/11
 * Time: 3:43 PM
 * 
 */

trait UserRepoService {
  def save(u:User):User
}

@Repository
@Transactional(readOnly = true)
class UserRepoServiceImpl extends UserRepoService {
  @Inject
	var repository: UserRepository = _

  def save(u:User) = { repository.save(u)}
}