package config

/**
 * 
 * User: warren
 * Date: 3/23/11
 * Time: 9:31 AM
 * 
 */

object FlatFile_TestFile1 extends FlatFileConnectorDefaultConfig {
  override val file = new java.io.File("src/test/resources/test1.csv")
  override val uniqueAttributeName = "accountId"
}