package com.my2do.idm.connector

import com.my2do.idm.model.DB
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.my2do.idm.model.Bundle

import scala.collection.Set
import scala.collection.JavaConversions._

import org.identityconnectors.framework.api.{ConnectorInfoManagerFactory, ConnectorInfo, ConnectorKey}
import net.liftweb.common.Logger

// singleton - need only one connector manager for each app
object ConnectorManager  {
  val instance = new ConnectorManager()
}

class ConnectorManager {
  val connectorMap = new scala.collection.mutable.HashMap[ConnectorKey, ConnectorInfo]
  val log = Logger("vidm")

  /**
   * Load in the bundles specified by the given set of URLs
   * These URLs will typically be discovered via iterating over the
   * resources in WEB-INF/bundles
   *
   * This will reload any existing bundles
   */
  def loadBundleMap(bundleURLs: Set[java.net.URL]) = {
    val cimf = ConnectorInfoManagerFactory.getInstance();
    cimf.clearLocalCache()
    connectorMap.clear()   // do we always want to reload the map?
    val cim = cimf.getLocalManager(bundleURLs.toSeq: _*);

    for (ci <- cim.getConnectorInfos()) {
      connectorMap.put(ci.getConnectorKey(), ci)
      log.debug("Loaded bundle: " + ci.getConnectorKey())
    }

    connectorMap
  }

  def connectorKeys = connectorMap.keySet

  /**
   * Get teh API config object for a given key
   *
   */
  def apiConfigForKey(key: ConnectorKey) = {
    val ci = getConnector(key)
    log.debug("Get APi Config for key=" + key + " ConnectorInfo=" + ci)
    ci.get.createDefaultAPIConfiguration
  }

  def getConnector(key: ConnectorKey) = connectorMap.get(key)


  /**
   * Return all of the configured bundles. The bundles are initialized
   * with the API implementation - assuming a matching bundle is available
   * in WEB-INF/bundles.
   */
  def getConfiguredBundles() = {
    val bundles = Bundle.allBundles
    // todo: instantiate bundles from the db
    val bundleInstances:Seq[BundleInstance]    = Nil
    bundles.map( new BundleInstance(_))
  }

}

