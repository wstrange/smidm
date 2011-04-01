package com.my2do.idm.model

import scala.reflect.BeanProperty
import javax.persistence._


@Entity
@serializable
class User extends BaseEntity {

  @BeanProperty
  @Column(unique = true, nullable=false)
  var accountId: String = _
  
  @BeanProperty
  var firstName: String = _
  
  @BeanProperty
  var lastName: String = _

  @BeanProperty
  var email:String = _

  @BeanProperty
  var employeeId: String = _

  @BeanProperty
  var department: String = _

  @BeanProperty
  var managerEmpId:String = _

  override def toString() =
    "user: " + id + " accountId=" + accountId + " " + firstName + "," + lastName



  /**
   * Role Assignments along with status
   */
  @BeanProperty @OneToMany(fetch=FetchType.EAGER, cascade = Array(CascadeType.ALL))
  var roleAssignments:java.util.List[RoleAssignment] =  new java.util.ArrayList[RoleAssignment]()


  // put all joined account types here..

  @BeanProperty
  //@OneToMany(mappedBy="user", cascade = Array(CascadeType.ALL))
  @OneToMany( cascade = Array(CascadeType.ALL))
	var  ldapAccounts:java.util.List[LDAPAccount] = new java.util.ArrayList[LDAPAccount]()



}
