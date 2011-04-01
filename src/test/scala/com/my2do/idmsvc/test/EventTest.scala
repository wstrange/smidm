
package com.my2do.idmsvc.test

import org.junit.Test
import org.junit.Assert._
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import javax.inject.Inject
import com.my2do.svc.UserRepoService
import com.my2do.repo.UserRepository
import com.my2do.idm.event._
import scala.collection.JavaConversions._
import com.my2do.idm.rule._
import com.my2do.idm.model._

/**
 * Test of scala jpa extensions. This does not work as
 * it conflicts with Spring's transaction management.
 * We need to pick one or the other (or switch to EE6 CDI, etc.?)
 *
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/applicationContext.xml"))
class EventTest extends TestBase  {

  @Inject
  var userSvc: UserRepoService = _


	@Test()
  def eventTest():Unit = {

    val target = declareRules

    val user =  DataGenerator.createSampleUser("test1")
    user.department = "xxxx"
    val newDept = "1234"

    //val ctx = new Context(user,SyncEvent("FF",target,newDept))
    val ctx = new Context(user,SyncEvent("FF", Map("department" -> newDept)))


    EventManager.fireEvent(ctx,target)


    assertEquals("Department number changed", user.department, newDept)
    val ldap = user.ldapAccounts.get(0)
    assertEquals("ldap dept got changes", ldap.departmentNumber,newDept )
    //e.action(event)


    try {
      EventManager.fireEvent(ctx,SyncTarget("Foo", "Bar") )
      fail("Expected to get exception for non matching rule")
    } catch {
      case e:Exception => // expected OK
    }

  }

  @Test
  def roleTest():Unit = {
    val r1 = declareRoleRules

    val user =  DataGenerator.createSampleUser("test1")

    val role = new Role()
    val e = new RoleAssignmentEvent(role, new Resource(), "cn=foo")
    r1.eval( Context(user,e))
  }

  def declareRules() = {
    val target = SyncTarget("User", "department")

    val r1 = new Rule("Update Department Code", { c:Context =>
      val u = c.user
      val attrs = c.event.asInstanceOf[SyncEvent].attributes
      //val attrs = c.event.attributes

      val dept =  attrs.getOrElse("department","").asInstanceOf[String]

      u.department =  dept
      //todo: How to minimize sync
      u.ldapAccounts.foreach( account => account.departmentNumber = dept)

      })

    EventManager.addRule(r1,target)
    target
  }


  def declareRoleRules():Rule = {

    val r1 = new Rule("ManagerRole", {  c:Context =>
      val gname = "cn=test,cn=groups,dc=blah"
      val ldap = c.user.ldapAccounts.get(0)
      val groups = ldap.groups
      val g = new LDAPGroup

      c.event match {
        case x:RoleAssignmentEvent =>

            val y = x.group // some group
            // val g = lookjup LDAPGroup by string y
            groups.add(g)

        case x:RoleDeAssignmentEvent => groups.remove(g)
        case x:RoleCheckEvent => groups.contains(g)
        case _   =>   // trouble
      }
    })


    r1
  }
  /*
  how to express "dept 1234 should be assigned role Dept1234UserRole

    assignRoles
     declare; Attribute Name, value
      triggers assignment action

      support expression language?

      User.department == "12335"  { roles.toDelete = other department roles
              roles.toAdd += "Dept12345Role"


      Roles assigned based on a) direct assignment
      or b) some rule trigger based on User attributes?



roles - explicit assignment  OR rule assignment  ??

EntitlementRule("ADManagerGroup", { context =>
  context.rule, user, etc.
  val group = "cn=managers,cn=blah"
  if(context.isAssignment) user.accounts("AD_Prod").groups +=  group
  else ...

  or conveniuen functions for easy cases?

  define funtion:

  EntitlementRule("ADManagerGroup",  handleAssignment("AD_Prod", "cn=foo,cn=blah"))



What about de-assignment?
   */
}
