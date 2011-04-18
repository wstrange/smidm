package com.my2do.idmsvc.test

/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */

import org.junit.runner.RunWith

import org.junit.Assert._

import scala.collection.JavaConversions._

import java.io.File
import java.net.URL
import org.identityconnectors.framework.api.ConfigurationProperty
import com.my2do.idm.connector.{ConnectorConfig, ConnectorManager, CodeGen}
import org.identityconnectors.framework.common.objects.{ConnectorObject, ObjectClass, ResultsHandler}
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.{Before, BeforeClass, Test}
import config.LDAP_Test

/*
objects ConnectorManagerTest {
  @BeforeClass
  def setUpConnectors() = {
    val cm = ConnectorManager.connectorManager
    val files = new File("src/test/resources/bundles").listFiles()
    val urls = new java.util.HashSet[URL]()
    files.foreach(f => urls.add(f.toURI().toURL()))
    //println("uurls=" + urls)
    cm.loadBundles(urls)
    cm.configureBundles
  }
}
*/

//@RunWith(classOf[SpringJUnit4ClassRunner])
class CodeGenTest  extends TestBase {

  /**
   * Calls code generation. Not really a test per se....
   * Generated code will be in the target/   directory
   */
  @Test
  def testCodeGen() = {

    val keys = connectorManager.connectorKeys
    assert( keys.size >= 1)

    println("Keys=" + keys.size + " set=" + keys)
    keys.foreach { key =>
      //val facade = cm.getFacade(key)
      // todo: flat file has no schema. Depends on file
      //CodeGen.makeEntityClasses(key.getBundleName, facade.schema)
      CodeGen.makeConfigClass(connectorManager.getConnectorInfo(key).get)
      //facade.test()
    }
  }

}

