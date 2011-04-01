package com.my2do.idmsvc.test


import org.junit.{ Test}
import com.my2do.idm.model._
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConversions._

/**
 * 
 * User: warren
 * Date: 3/30/11
 * Time: 12:27 PM
 * 
 */

class RoleTest  extends TestBase {

  @Test
  @Transactional
  def testRoleDBOps() = {

    val u = DataGenerator.createSampleUser("test1")


    val role = Role("test")
    //val role = new Role()
    //val role2 = new Role("test2")

    val ra = new RoleAssignment()
    ra.role = role
    ra.user = u
    //val ra2 = new RoleAssignment(u,role2)

    u.roleAssignments.add(ra)
    //u.roleAssignments.add(ra2)

    em.persist(role)
    em.persist(u)
    //em.persist(ra)    - should cascade

    em.flush()

    // todo: Create role svc object
  }


  def provisionRole(user:User, role:Role) = {
    role.resources.foreach( resource =>
      // check provisioned //
      // if resoruce is not provisioned, go create it
      println(resource)
    )

    // now run role rule...
    val r = role.scalaRule
    // persist the changes
    // userRepo.save()

    // tell the recon process to flush the user
    // Recon.updateUser(u)

  }
}