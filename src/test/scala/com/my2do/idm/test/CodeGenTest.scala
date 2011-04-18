/*
 * Copyright (c) 2011 - Warren Strange
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.my2do.idm.test

/**
 * Created by IntelliJ IDEA.
 * User: warren
 * Date: 2/5/11
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */

import com.my2do.idm.connector.CodeGen
import org.junit.Test


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


class CodeGenTest extends FunTest {

  /**
   * Calls code generation. Not really a test per se....
   * Generated code will be in the target/   directory
   */
  ignore("teset code gen") {
    val keys = connectorManager.connectorKeys
    assert(keys.size >= 1)

    println("Keys=" + keys.size + " set=" + keys)
    keys.foreach {
      key =>
      //val facade = cm.getFacade(key)
      // todo: flat file has no schema. Depends on file
      //CodeGen.makeEntityClasses(key.getBundleName, facade.schema)
        CodeGen.makeConfigClass(connectorManager.getConnectorInfo(key).get)
      //facade.test()
    }
  }

}

