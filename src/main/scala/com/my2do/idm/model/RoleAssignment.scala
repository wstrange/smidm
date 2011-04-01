package com.my2do.idm.model

import java.util.Date

import javax.persistence._
import reflect.BeanProperty
import java.io.Serializable

/**
 *
 * A record of the roles
 * User: warren
 * Date: 3/30/11
 * Time: 10:20 AM
 * 
 */

object RoleAssignment {
  val MAXDATE =   new Date(java.lang.Long.MAX_VALUE)
}


/*
@serializable
case class RoleAssignmentPK(@BeanProperty var role:Role, @BeanProperty user:User)  {
  def this() = this(new Role(),new User())
}
*/

@Entity
@serializable
//@IdClass(classOf[RoleAssignmentPK])
class RoleAssignment   {
  @Id @GeneratedValue var id:java.lang.Long = _

  @BeanProperty @ManyToOne  var user:User = _
  @BeanProperty @ManyToOne var role:Role  = _

  //def this() = this(null)




  // dow we need ManytoOne??  only for queries for which users have which roles?
  // but
  //@BeanProperty var role:Role = _


  /**
   * assignment state -
   */
  var provisioned:Boolean = false

  @Temporal(TemporalType.TIMESTAMP)
  var sunriseDate:Date =   new Date

  @Temporal(TemporalType.TIMESTAMP)
  var sunsetDate:Date =  RoleAssignment.MAXDATE

  // todo: What about approvals?

  var approved:Boolean = false

}