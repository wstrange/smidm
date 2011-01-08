package com.my2do.idmsvc

import org.scalatest.junit.AssertionsForJUnit
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._
import com.my2do.idm.model._
import net.liftweb.common.Logger

abstract class BaseTest  extends FunSuite with AssertionsForJUnit with Logger {
	
	import com.my2do.idm.model._
	DB.initOracle
}

