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

package config

/**
 * 
 * User: warren
 * Date: 3/23/11
 * Time: 9:31 AM
 * 
 */

object FlatFile_TestFile1 extends FlatFileConnectorDefaultConfig {
  override val instanceKey = "flatfile1_apr11"
  override val file = new java.io.File("src/test/resources/test1.csv")
  override val uniqueAttributeName = "accountName"
}