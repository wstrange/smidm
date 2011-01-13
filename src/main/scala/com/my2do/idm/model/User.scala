package com.my2do.idm.model

import scala.reflect.BeanProperty
import javax.persistence._


@Entity
class User {
  @BeanProperty
  @Id
  var id:Long = _
  
  
  
  @BeanProperty
  var userName: String = _;
  
  @BeanProperty
  var firstName: String = _;
  
  @BeanProperty
  var lastName: String = _;
  /*
  @BeanProperty
  @ManyToOne
  var department: Department = _;
  */
}
