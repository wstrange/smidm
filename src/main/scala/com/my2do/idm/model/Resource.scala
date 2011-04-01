package com.my2do.idm.model
import org.identityconnectors.framework.api.ConnectorKey
import scala.reflect.BeanProperty

import java.io.Serializable;
import javax.persistence._
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size
import collection.mutable.HashSet
;


@Entity
class Resource extends BaseEntity  {

  @BeanProperty var instanceName: String = _

  // we don't need this now that we are keeping connector props as scala objects?
  @BeanProperty var configurationPropertiesXML:String = _

  @BeanProperty var keyBundleName:String = _
  @BeanProperty var keyBundleVersion: String = _
  @BeanProperty var keyConnectorName: String = _

  @Transient
  val entityClasses  = new HashSet[Class[ConnectorEntity]]


  def connectorKey =  new ConnectorKey(keyBundleName,keyBundleVersion,keyConnectorName)
  override def toString() = "Resource("+instanceName+","+keyBundleName+"...)"
}
