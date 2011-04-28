/*
 * Copyright (c) 2011 - Warren Strange
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.my2do.idm.mongo


import org.identityconnectors.framework.api.ConnectorFacade
import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.connector.util.ConnectorObjectWrapper
import org.identityconnectors.framework.common.objects._
import net.liftweb.common.Logger
import com.my2do.idm.objects.ResourceObject

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

class ICFacade(val facade: ConnectorFacade, config: ConnectorConfig) extends Logger {


  /**
   * Iterate over accounts
   */
  def foreachAccount(f: (ICAttributes) => Unit) = foreachObject(ObjectClass.ACCOUNT, f)

  /**
   * iterate over groups
   */
  def foreachGroup(f: (ICAttributes) => Unit) = foreachObject(ObjectClass.GROUP, f)

  /**
   * iterate over objects of the given objectclass
   */

  def foreachObject(objectClass: ObjectClass, f: ((ICAttributes) => Unit)) = {
    facade.search(objectClass, null, new ResultsHandler() {
      def handle(obj: ConnectorObject) = {
        f(new ConnectorObjectWrapper(obj, config.schemaForObjectClass(objectClass)))
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

  /**
   * Update the object or create if it does not exists.
   *
   */

  def save(obj: ResourceObject): Option[String] = {
    val attrs = getObj(obj.uid)
    attrs match {
      case Some(ica: ICAttributes) =>
        update(obj, ica)
      case _ =>
        create(obj)
    }
  }

  def create(obj: ResourceObject): Option[String] = {
    try {
      val attrs =  resourceObjectToAttrSet(obj)
      attrs.add( normalize(Name.NAME,obj.accountName))
      debug("Create Op name=" + obj.accountName + "\n\t\tvalues=" + attrs)
      val uid = facade.create(ObjectClass.ACCOUNT,attrs , null)
      Some(uid.getUidValue)
    }
    catch {
      case e: Exception => error("Exception trying to create account " + obj, e)
      None
    }
  }

  def update(obj: ResourceObject, existingAttrs: ICAttributes): Option[String] = {
    val attrs =  resourceObjectToAttrSet(obj, existingAttrs.attributeMap)

    debug("Update Op name=" + obj.accountName + "\n\t\tAttrs=" + attrs)
    val u = facade.update(ObjectClass.ACCOUNT, new Uid(obj.uid), attrs, null)

    if (u != null && u.getUidValue != null)
      Some(u.getUidValue)
    None
  }

  def getObj(id: String): Option[ICAttributes] = {
    if (id == null)
      return None
    val uid = new Uid(id)
    val obj = facade.getObject(ObjectClass.ACCOUNT, uid, null)
    if (obj == null)
      return None
    Some(new ConnectorObjectWrapper(obj, config.schemaForObjectClass(ObjectClass.ACCOUNT)))
  }

  private def resourceObjectToAttrSet(obj: ResourceObject, existingAttributes:Map[String,AnyRef] = Map()): java.util.Set[Attribute] = {
    val s = new java.util.HashSet[Attribute]()
    obj.attributes.foreach {
      case (name, value) =>
        // must skip special attributes
        if( ! isSpecial(name)) {
          // optimization:
          // if the attribute already exists and the value is unchanged - skip it
          // todo: This dooes not work on Multi-valued attributes cuz equals will not work
          val a = existingAttributes.get(name)
          if( ! (a.isDefined  && a.get.equals(value)))
            s.add(normalize(name, value))
        }
    }
    //s.add(normalize(Name.NAME, obj.accountName))
    s
  }

  private def isSpecial(name:String) =  (name.equals(Uid.NAME) || name.equals(Name.NAME))

  private def normalize(name: String, v: AnyRef): Attribute = {
    val attr = new AttributeBuilder()
    attr.setName(name)
    v match {
      case x: Iterable[AnyRef] =>
        x.foreach(xx => attr.addValue(xx))
      case _ => attr.addValue(v)
    }
    attr.build
  }

  def delete(obj: ResourceObject) = {
    //val uid = new Uid(entity.accountUid)
    //facade.delete(entity.connectorObjectClass, uid, null)
    val uid = new Uid(obj.uid)
    val r = facade.delete(ObjectClass.ACCOUNT, uid, null)
  }


}