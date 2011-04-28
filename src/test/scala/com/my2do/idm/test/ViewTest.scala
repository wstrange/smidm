/*
 * Copyright (c) 2011 Warren Strange
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

import com.my2do.idmsvc.test.TestData
import com.my2do.idm.objects.User

/**
 * 
 * User: warren
 * Date: 4/27/11 
 */

class ViewTest extends FunTest {

  test("reflect test") {

    println( "setters=" + User.calculateSetters() )
    val u = TestData.user(0)

    u("firstName") = "newname"

    assert(u.firstName == "newname")

    assert( u("firstName").equals("newname"))

     // va. ro = uv.account("ldapProd")
    //  uv.user("firstName") = "foo"
    // uv("account.ldapProd.
    //uv("ldap.foo")
    // val ldapResource.
    // val  ldap = uv(
  }


}