package com.my2do.idm.model
import org.identityconnectors.framework.api.ConnectorKey
import scala.reflect.BeanProperty

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
class Bundle(
  @Id @GeneratedValue @BeanProperty var id:Long = 0,
  @BeanProperty var instanceName: String = "",
  @BeanProperty var configurationPropertiesXML:String = null ,
  @BeanProperty var keyBundleName:String = "",
  @BeanProperty var keyBundleVersion: String = "",
  @BeanProperty var keyConnectorName: String ="" 
  )  {
  def connectorKey =  new ConnectorKey(keyBundleName,keyBundleVersion,keyConnectorName)
  override def toString() = "Bundle("+instanceName+","+keyBundleName+"...)"

}
