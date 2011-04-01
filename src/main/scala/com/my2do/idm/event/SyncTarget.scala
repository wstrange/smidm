package com.my2do.idm.event

import com.my2do.idm.model.{User, Resource}

/**
 *  Represents the target of an event.
 * The target is a resource (e.g. LDAP_Production1) and an Attribute (e.g. employeeNumber)
 *
 * User: warren
 * Date: 3/24/11
 * Time: 12:22 PM
 * 
 */


case class SyncTarget(val resource:String, val attributeName:String) {
  /***
   * Event matching using wildcards
   * A '*' cab be used as a wildcard in the resource name or the attributeName
   *
   * @return true if this event matches against the other event
   */
  def matches(otherTarget:SyncTarget):Boolean = {
    if( otherTarget.equals(this)) true

    val resourceMatch =  (this.resource == otherTarget.resource) ||
            isResourceWildCard || otherTarget.isResourceWildCard
    val attributeMatch = (this.attributeName == otherTarget.attributeName) ||
        isAttributeWildCard || otherTarget.isAttributeWildCard

    return resourceMatch && attributeMatch
  }

  def isResourceWildCard = "*".equals(resource)
  def isAttributeWildCard = "*".equals(attributeName)
  def isWildCardPresent = isResourceWildCard || isAttributeWildCard
}





