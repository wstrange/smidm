package com.my2do.idm.event

import net.liftweb.common.Logger
import scala.collection.JavaConversions._
import scala.collection.Map
import reflect.BeanProperty
import org.identityconnectors.framework.common.serializer.SerializerUtil
import org.identityconnectors.framework.api._
import com.my2do.idm.model.Resource

class BundleInstance(@BeanProperty var bundle: Resource)  {
	val log = Logger("vidm")
	  
	// on initialization - dersialize our properties
	deserializePropsFromXML()
	

	log.debug("Config Props=" + configProperties + " Resource=" + bundle + "  api="+ connectorAPIConfiguration)
	
  /**
   *   This is a a transient property set dynamically when the bundle is loaded. It is not
   * persisted to the database. We derive instance info (connector type, etc.)
   */
  // why do we need this/
  // var connectorInfo: ConnectorInfo = _

  // The API connector - used to get a facade connection
  //lazy val connectorAPIConfiguration: APIConfiguration = ConnectorManager.connectorManager.defaultAPIConfigForKey(bundle.connectorKey)
  lazy val configProperties = connectorAPIConfiguration.getConfigurationProperties()


  def getPropertyValue(name: String) = configProperties.getProperty(name)
  def setPropertyValue(name: String, obj: Object) = configProperties.setPropertyValue(name, obj)
  def setPropertyValues(s: scala.collection.Set[(String, Any)]) = {
    s.foreach { v: (String, Any) =>
      val property = configProperties.getProperty(v._1)
      if (property != null)
        setPropertyValue(v._1, v._2.asInstanceOf[AnyRef])
    }
  }

  def serializePropsToXML() = {
    bundle.configurationPropertiesXML = SerializerUtil.serializeXmlObject(configProperties, true)
  }

  def deserializePropsFromXML() = {
    if (bundle.configurationPropertiesXML != null) {
      val cfp =
        SerializerUtil.deserializeXmlObject(bundle.configurationPropertiesXML, true).asInstanceOf[ConfigurationProperties]
      // now merge in the serialized values over top of the current values
      for (name <- cfp.getPropertyNames) {
    	//log.debug("deserial prop =" + name)
        setPropertyValue(name, cfp.getProperty(name).getValue)
      }
    }

  }

  /**
   * Create a Connector Facade using the bundle configuration
   * The returned Facade can be used to perform connector operations
   * on a target
   */
  def getFacade() = {
    if (connectorAPIConfiguration != null)
      ConnectorFacadeFactory.getInstance().newInstance(connectorAPIConfiguration)
    else
      null
  }

}