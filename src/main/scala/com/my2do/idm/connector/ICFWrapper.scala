package com.my2do.idm.connector


import com.my2do.idm.model.ConnectorEntity
import org.identityconnectors.framework.common.objects.filter.{FilterBuilder, Filter}
import org.identityconnectors.framework.common.objects._

/**
 *
 * Convenience wrapper on top of ICF facade. The intent of this
 * is the "scalaify" some of the ICF methods
 *
 * User: warren
 * Date: 3/19/11
 * Time: 10:45 PM
 * 
 */

class ICFWrapper(connector: ConnectorConfig)  {
  def facade = connector.getFacade

  /**
   * return some kind of iterator
   */
  def foreachAccount( f: (ConnectorEntity) => Unit )= foreachObject(ObjectClass.ACCOUNT,f)

  def foreachGroup( f: (ConnectorEntity) => Unit )=  foreachObject(ObjectClass.GROUP,f)


  def foreachObject(objectClass:ObjectClass, f: ((ConnectorEntity) => Unit ))= {
    facade.search(objectClass,null, new ResultsHandler() {
        def handle(obj:ConnectorObject) = {
          val entity = connector.newConnectorEntity(objectClass)
          entity.setConnectorObject(obj)
          f(entity)
          true
        }
      }, null)
  }

  /**
   * todo: how to scalify?
   * Should accept a closure to handle the iterations - allow breaking out
   */

  def search[ T <: ConnectorEntity](clazz:Class[T], criteria:String, f: ((ConnectorEntity) => Unit )):List[T] = {
    val attr = AttributeBuilder.build("sn","Test")
    val filter = FilterBuilder.startsWith(attr)

    val objectClass = ObjectClass.ACCOUNT

    val options = new OperationOptionsBuilder()
    //b.setScope(string).setAttributesToGet(attr name....)

    facade.search(objectClass,filter, new ResultsHandler() {
        def handle(obj:ConnectorObject) = {
          val entity = connector.newConnectorEntity(objectClass)
          entity.setConnectorObject(obj)
          f(entity)
          true
        }
      }, options.build)

    Nil
  }

  def create(entity:ConnectorEntity)= {
    //entity.connectorNAME = entity.uid
    val u = facade.create(entity.connectorObjectClass, entity.marshall, null)
    entity.accountUid = u.getUidValue
  }

  def delete(entity:ConnectorEntity) = {
    val uid = new Uid(entity.accountUid)
    facade.delete(entity.connectorObjectClass, uid, null)
  }

  def getEntity[T <: ConnectorEntity](uid:String, clazz:Class[T]):Option[T] = {
    val entity = clazz.newInstance
    val obj = facade.getObject(entity.connectorObjectClass, new Uid(uid), null)
    if( obj != null ) {
      entity.setConnectorObject(obj)
      entity.unmarshall
      return Some(entity)
    }
    None
  }
}