package com.my2do.idm.model
import org.squeryl.PrimitiveTypeMode._
import org.identityconnectors.framework.api.ConnectorKey
import scala.reflect.BeanProperty


object Bundle {
  def apply(name:String, key:ConnectorKey) = {
    new Bundle(name,null,key.getBundleName, key.getBundleVersion, key.getConnectorName)
  }
  def allBundles = {
    var x:Set[Bundle] = Set()
    inTransaction {
      val q = from(DB.bundles) ( b => select(b))
      q.foreach( result =>  x += result )
    }
    x
   }
}

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
