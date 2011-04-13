package com.my2do.idm.connector

import java.lang.reflect.Method
import scala.collection.JavaConversions._
import org.identityconnectors.framework.api._
import org.identityconnectors.framework.common.objects.{AttributeInfo, ObjectClass}
import config._
import net.liftweb.common.Logger
import collection.mutable.{HashSet, HashMap, ListBuffer}
/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 2/5/11
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 *
 */

object ConnectorConfig {


  /**
   * We create connector val (really methods) by reflection. We need to filter out
   * all the methods in the base class and in Object. this filter tells us
   * which methods to prune
   */
  val filterMethods = classOf[ConnectorConfig].getMethods ++ classOf[Object].getMethods

  /**
   * Return a list of Configuration objects. If you add a new connector you will need to add a new objects here
   * todo: Should a connector have qa version in the name - sowe can load two differnet versions of LDAP (for example)
   *
   * todo: Should we make this more dynamic? Inject with Spring? reflect on connector.* to get configs
   */
  val connectors: List[ConnectorConfig] = List(LDAP_Prod, FlatFile_TestFile1)

  /**return a set of all the Connector Keys configured in the system */
  def configObjectsKeySet = connectors.map(co => co.connectorKey)

}

/**
 * All  connector connector config classes MUST be subclassed from this base
 * todo: We should probably seperate out the configuration data from the
 * facade instances. The facade is a dynamic thing (could need to be re-initialized,etc.)
 *
 * move facade stuff to ConnectorManager - it should
 */
abstract class ConnectorConfig extends Logger {

  val instanceName: String

  val connectorKey: ConnectorKey
  // subclass must define this

  var objectClasses = new HashSet[ObjectClass]
  private var apiConfig: APIConfiguration = _


  var isConfigured = false

  /**Caches schema information for each objects class */
  val schemaMap = new HashMap[ObjectClass, HashMap[String, AttributeInfo]]()

  /**
   * @return a map of AttributeInfos for the given objects class. The map is keyed on the attribute name
   */
  def schemaForObjectClass(o: ObjectClass): Map[String, AttributeInfo] = schemaMap.getOrElse(o,
    throw new IllegalArgumentException("No Schema found for objectclass " + o)).toMap

  /**
   * @return the AttributeInfo for the attributeName in the given objectclass.
   */
  def attributeInfo(o: ObjectClass, attributeName: String) = schemaForObjectClass(o).get(attributeName)


  def initSchema(facade: ConnectorFacade) = {
    val objClassInfos = facade.schema.getObjectClassInfo
    //objectClasses = objClassInfos.map ( info => new ObjectClass(info.getType)) toSet
    objClassInfos.foreach {
      i =>
        val objClass = new ObjectClass(i.getType)
        objectClasses.add(objClass)
        val attrMap = new HashMap[String, AttributeInfo]
        schemaMap.put(objClass, attrMap)
        i.getAttributeInfo.foreach {
          attrInfo =>
            attrMap.put(attrInfo.getName, attrInfo)
        }
    }
  }


  /**
   * Initialize and configure this Connector
   */
  def init(api: APIConfiguration) = {
    this.apiConfig = api
    setConfigProperties(api.getConfigurationProperties)
  }

  /**
   * Set the connector parameters by invoking our bean methods
   * to return the property values. This uses reflection on the subclass to find the connector bean methods
   *
   * @param cfp = connector parameters that get set as a side effect.
   *
   */
  private def setConfigProperties(cfp: ConfigurationProperties): Unit = {
    // get all the Config Object getter Methods
    val getters = this.getClass.getMethods.filter(method => !ConnectorConfig.filterMethods.contains(method))

    var setProps = new ListBuffer[String]()

    getters.foreach {
      method =>
        val prop = cfp.getProperty(method.getName)
        if (prop != null) {
          val obj = method.invoke(this)
          //println("Set " + method.getName + "=" + obj)
          prop.setValue(obj)
          setProps += method.getName
        }
        else
          println("Warning no property found for '" + method.getName + "'")
    }
  }


  // for debugging
  def showMethods() = {
    val all = this.getClass.getMethods

    val subset = all.filter(method => !ConnectorConfig.filterMethods.contains(method))
    ConnectorConfig.filterMethods.foreach(m => printMethod(m))
    println("Done********")
    subset.foreach(m => printMethod(m))
  }

  def printMethod(m: Method) = println("name=" + m.getName + " num parms=" + m.getParameterTypes.length)

}

