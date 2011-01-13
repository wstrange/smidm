package com.my2do.idmsvc.test

import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.test.context.junit4._
import com.my2do.idm.model.User
import com.mysema.query.scala.Conversions._
import com.mysema.query.jpa.impl.JPAQuery
import com.mysema.query.types.path._
import org.junit.Assert._

import org.junit.Test

@RunWith(classOf[SpringJUnit4ClassRunner])
class QueryDSLTest extends TestBase {

  var u:User = _
  
  @Before
  def setUp() = {
    u = new User()
    u.userName = "Freddy"
    u.firstName = "Fred"
    u.lastName + "Flinstone"
    userRepoDAO.save(u)
  }

  @After
  def tearDown() = {
	  userRepoDAO.delete(u)
  }

  @Test
  def Various() {
    val user = alias(classOf[User])

    println("Person alias=" + user)

    assertEquals(1, query from user count);

    assertEquals(1, query from user list user size);

    assertEquals("Freddy", query from user where (user.userName $eq "Freddy") uniqueResult user.userName);

    /*
    // list
    query from person where (person.firstName $like "Rob%")
    	.list person

    // unique result
    query from person where (person.firstName $like "Rob%") 
      .unique person

    // long where
    query from person 
      .where (person.firstName $like "Rob%", person.lastName $like "An%") 
      .list person

    // order
    query from person orderBy (person.firstName asc) list person

    // not null 
    query from person 
      .where (person.firstName $isEmpty, person.lastName $isNotNull) 
      .list person
      */
  }

  def query() = new JPAQuery(getEntityManager())

}
