package com.my2do.idm.correlate

import com.my2do.idm.dao.UserDAO
import com.my2do.idm.connector.util.ICAttributes
import com.my2do.idm.objects.User
import com.mongodb.casbah.commons.MongoDBObject._
import com.my2do.idm.mongo.MongoUtil
import com.mongodb.casbah.commons.MongoDBObject

/**
 *
 * User: warren
 * Date: 4/7/11
 * Time: 4:52 PM
 *
 */

object LDAPRules extends AccountRules {

  /**
   * Correlate on employeeNumber
   */
  override def correlateUser(attrs: ICAttributes): Option[User] = {
    UserDAO.findByEmployeeId(attrs.firstValueAsString("employeeNumber"))
  }

  override def createUserFromAccountAttributes(a: ICAttributes): Option[User] = {
    val accountName = a.firstValueAsString("uid")
    val givenName = a.firstValueAsString("givenName")
    val lastName = a.firstValueAsString("sn")
    val empId = a.firstValueAsString("employeeNumber")

    // check for unique empId?
    val existingU = UserDAO.findByEmployeeId(empId)
    if (existingU != None) {
      error("An exisiting user exists with thesame employee id!. id=" + empId)
      return None
    }
    Some(User(accountName, givenName, lastName, empId))
  }

  override def newResourceObject(u:User) = {
    var obj = MongoDBObject("uid" -> u.accountName,
          "cn" -> (u.firstName + " " + u.lastName),
          "sn" -> List(u.lastName),
          "employeeNumber" -> u.employeeId,
          "givenName" -> List(u.firstName))

    val name =  "uid=" + u.accountName + ",ou=People,dc=example,dc=com"
    obj.putAll(MongoUtil.makeNameAttribute(name))
    obj
  }

}