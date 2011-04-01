package com.my2do.idmsvc.test

import net.liftweb.common.Logger
import javax.inject.Inject



import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4._
import org.springframework.test.context._
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation ._
import com.my2do.repo.UserRepository
import com.my2do.idm.connector.util.SLF4JLogger
import org.identityconnectors.common.logging.Log
import java.io.File
import org.junit.{Before, Ignore, Test}
import com.my2do.idm.connector.{ConnectorManager, ConnectorConfig}

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/applicationContext.xml"))
class TestBase extends Logger {
	@Inject var applicationContext:ApplicationContext = _

  val logc:Class[SLF4JLogger] = classOf[SLF4JLogger]
  System.setProperty(Log.LOGSPI_PROP,  logc.getCanonicalName)


  @Inject var cm:ConnectorManager = _

  //var cm:ConnectorManager = ConnectorManager("src/test/resources/bundles")


 // set of connector object keys
  val keyset = ConnectorConfig.configObjectsKeySet

  @Before
  def setUpConnectors() =  {
    // todo: Set this up via injection....
    //ConnectorManager.baseUrls = bundleFiles.map(f => f.toURI.toURL)
    //info( "Bundles loaded from URLS=" + ConnectorManager.baseUrls)
    //cm = ConnectorManager.connectorManager
  }


	@PersistenceContext
	var em:EntityManager = _
	
	def getEntityManager() = em

  @Inject
  var userRepo:UserRepository = _

  @Ignore
  @Test
  def doNothing() = {}

}