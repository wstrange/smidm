package com.my2do.idm.connector

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.my2do.idm.model.Resource

import scala.collection.Set
import scala.collection.JavaConversions._

import net.liftweb.common.Logger
import org.identityconnectors.framework.api._
import java.io.File

// singleton - need only one connector manager for each app
object ConnectorManager  {
  def getFacade(connectorAPIConfiguration: APIConfiguration): ConnectorFacade = {
    if (connectorAPIConfiguration != null)
      ConnectorFacadeFactory.getInstance().newInstance(connectorAPIConfiguration)
    else
      null
  }

  /**
   * Create a connector manager from a given directory
   */
  def apply(directory:String) = {
    val bundleFiles = new File(directory).listFiles()
    new ConnectorManager(  bundleFiles.map(f => f.toURI.toURL) )
  }

}

class ConnectorManager(bundleUrls:Array[java.net.URL]) extends Logger {

  private val connectorMap = new scala.collection.mutable.HashMap[ConnectorKey, ConnectorInfo]


  // call init to trigger loading of the bundles
  init

  def init() = {
    loadBundles()
    configureBundles
  }
  /**
   * Load in the bundles specified by the given set of URLs
   * These URLs will typically be discovered via iterating over the
   * resources in WEB-INF/bundles
   *
   * This *does not* cause the bundles to be configured
   *
   * This will reload any existing bundles
   */
  def loadBundles() = {
    val cimf = ConnectorInfoManagerFactory.getInstance();
    cimf.clearLocalCache()
    connectorMap.clear()   // do we always want to reload the map?
    val cim = cimf.getLocalManager(bundleUrls: _*);

    for (ci <- cim.getConnectorInfos()) {
      connectorMap.put(ci.getConnectorKey(), ci)
      info("Loaded bundle: " + ci.getConnectorKey())
    }
    connectorMap
  }

  /**
   * Configures the bundles
   * Assumes the bundles have been loaded
   *
   * Configure the bundles by setting the connector params from each ConnectorConfig listed in the companion object
   *
   */
  def configureBundles() = {
    ConnectorConfig.connectors.foreach{ connector => configureConnector(connector) }
  }

  def configureConnector(connector:ConnectorConfig) = {
      val key = connector.connectorKey
      info("Configuring connector =" + key)
      val apiConfig = defaultAPIConfigForKey(key)
      if( apiConfig == null)  {
        val msg =  "No matching bundle could be found for key= " + key
        error(msg)
        error("Perhaps the bundle location is not configured properly or the Configuraition has the wrong bundle name/version/")
        throw new RuntimeException(msg )
      }
      // initialzie the connector - cauases the facade to be created
      connector.configure(apiConfig)
  }

  def connectorKeys = connectorMap.keySet

  /**
   * Get teh API connector object for a given key
   *
   */
  def defaultAPIConfigForKey(key: ConnectorKey):APIConfiguration = {
    val ci = connectorMap.get(key).get
    debug("Get APi Config for key=" + key + " ConnectorInfo=" + ci)
    ci.createDefaultAPIConfiguration
  }

  // todo: Remove this - The CodeGen module is the only client...
  def getConnectorInfo(key: ConnectorKey) =  connectorMap.get(key)

}

