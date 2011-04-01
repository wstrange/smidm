package com.my2do.idm.mongo



import org.identityconnectors.framework.common.objects._
import org.identityconnectors.framework.api.ConnectorFacade
import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.connector.util.ConnectorObjectWrapper
import com.my2do.idm.model.ConnectorEntity

/**
 *
 * Convenience wrapper on top of ICF facade. The intent of this
 * is the "scalaify" some of the ICF methods
 *
 * This is mongo oriented...
 *
 * User: warren
 * Date: 3/19/11
 * Time: 10:45 PM
 *
 */

class ICFacade(val facade: ConnectorFacade, config:ConnectorConfig) {


  /**
   * Iterate over accounts
   */
  def foreachAccount( f: (ICAttributes) => Unit )= foreachObject(ObjectClass.ACCOUNT,f)

  /**
   * iterate over groups
   */
  def foreachGroup( f: (ICAttributes) => Unit )=  foreachObject(ObjectClass.GROUP,f)

  /**
   * iterate over objects of the given objectclass
   */

  def foreachObject(objectClass:ObjectClass, f: ((ICAttributes) => Unit ))= {
    facade.search(objectClass,null, new ResultsHandler() {
        def handle(obj:ConnectorObject) = {
          f(new ConnectorObjectWrapper(obj,config.schemaForObjectClass(objectClass))
          true
        }
      }, null)
  }

  /**
   * todo: how to scalify?
   * Should accept a closure to handle the iterations - allow breaking out
   */

  /*
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
  */

  def create()= {
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