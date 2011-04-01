package com.my2do.idm.model

import java.{util => ju}
import reflect.BeanProperty
import javax.persistence._
import javax.persistence.Column._

/**
 *
 * Is it really a group?
 *
 * User: warren
 * Date: 3/24/11
 * Time: 5:40 PM
 * 
 */

object RoleLevel {
  val BUSINESS_ROLE = 100
  val IT_ROLE = 50
  val ENTITLEMENT = 10
}

object Role {
  def apply(name:String) = { val r = new Role(); r.name= name; r}
}


@Entity
class Role extends BaseEntity {

  @BeanProperty @Column(unique = true, nullable=false) var name:String  = _


  @BeanProperty
  var description:String = _

  @BeanProperty
  var assignmentType:Int = _

  /** some rule to be executed? Ties it to a scala rule.... */
  @BeanProperty
  var scalaRule:String = _

  /**
   * Role level. We can only assign child roles at a lower level
   *
   *
   */
  @BeanProperty
  var level:Int = RoleLevel.BUSINESS_ROLE

  @ManyToOne
  @BeanProperty
  var roleParent: Role = _

  @OneToMany(mappedBy="roleParent")
  var childRoles: ju.Collection[Role] = new ju.ArrayList[Role]()

  @OneToMany
  var resources:ju.Collection[Resource]  = new ju.ArrayList[Resource]

}