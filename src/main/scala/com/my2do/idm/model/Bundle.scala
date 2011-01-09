package com.my2do.idm.model
import org.identityconnectors.framework.api.ConnectorKey
import scala.reflect.BeanProperty



class Bundle(
  @BeanProperty var instanceName: String = "",
  var configurationPropertiesXML:String = null ,
  @BeanProperty var keyBundleName:String = "",
  @BeanProperty var keyBundleVersion: String = "",
  @BeanProperty var keyConnectorName: String =""
  ) extends BaseDbObject {

  def connectorKey =  new ConnectorKey(keyBundleName,keyBundleVersion,keyConnectorName)
  override def toString() = "Bundle("+instanceName+","+keyBundleName+"...)"

}
