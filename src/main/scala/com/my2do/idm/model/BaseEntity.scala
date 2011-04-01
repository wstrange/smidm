package com.my2do.idm.model

import reflect.BeanProperty
import javax.persistence._
import java.util.Date

/**
 * 
 * User: warren
 * Date: 3/24/11
 * Time: 3:41 PM
 * 
 */

@MappedSuperclass
class BaseEntity    {

  @Id
  @GeneratedValue
  @BeanProperty var id: java.lang.Long = _

  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP) var created: Date = new java.util.Date()

  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP) var updated: Date = new java.util.Date()

}