package com.my2do.idmsvc

/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 10/29/10
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.collection.JavaConversions._

import com.my2do.idm.model.{ Bundle, DB }
import com.my2do.idm.connector.ConnectorManager
import java.io.File
import java.net.URL

@RunWith(classOf[JUnitRunner])
class ConnectorManagerTest extends BaseTest {
	

  test("Connector Manager Test") {

    val cm = ConnectorManager.instance

    println("cwd=" + System.getProperty("user.dir"))
    //cm.load
    val files = new File("src/test/resources/bundles").listFiles()
    val urls = new java.util.HashSet[URL]()
    files.foreach(f => urls.add(f.toURI().toURL()))

    println("uurls=" + urls)
    cm.loadBundleMap(urls)

    val keyset = cm.connectorKeys
    assert( keyset.size >= 1) 

    keyset.foreach { key =>
      val apiConfig = cm.apiConfigForKey(key)
      assert( apiConfig.getSupportedOperations.isEmpty === false)
    }
  }
  
  test("Bundle load: Test bundles loaded from DB can be initialized") {
	 val cm = ConnectorManager.instance 
	  
	 val bundleInstances= cm.getConfiguredBundles
	 // todo: figure out how to get bundles loaded into the DB
  }

}

