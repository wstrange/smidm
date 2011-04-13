package com.my2do.idm.resource

import com.my2do.idm.connector.ConnectorConfig
import com.my2do.idm.correlate.AccountRules
import config.FlatFile_TestFile1
import com.my2do.idm.mongo.ICFacade
import javax.inject.Inject

/**
 * 
 * User: warren
 * Date: 4/12/11
 * Time: 5:47 PM
 * 
 */

class Resource(config:ConnectorConfig,rules:AccountRules ) {

  //var underlyingFacade =  _
  var facade:ICFacade =  _

  /*
  def getFacade() = {
    val facade = connectorManager.getFacade(config)
    new ICFacade(facade, config)
  }

*/

}