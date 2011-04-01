package com.my2do.idm.event

import com.my2do.idm.model.{Resource, Role}
import org.identityconnectors.framework.common.objects.ConnectorObject

/**
 * 
 * User: warren
 * Date: 3/25/11
 * Time: 11:30 AM
 * 
 */

abstract class Event

//case class SyncEvent(source:String,target:SyncTarget,value:AnyRef = null)  extends Event

// why is a target needed?
case class SyncEvent(source:String, attributes:Map[String,AnyRef], uid:String = "") extends Event

case class ConnectorSyncEvent(obj:ConnectorObject) extends Event

// kludgy....

case class RoleAssignmentEvent(role:Role, resource:Resource,group:String ) extends Event

case class RoleDeAssignmentEvent(r:Role) extends Event

case class RoleCheckEvent(r:Role) extends Event
