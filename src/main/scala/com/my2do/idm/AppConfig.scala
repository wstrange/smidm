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

package com.my2do.idm

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.annotation.{AnnotationConfigUtils, Configuration}
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import collection.mutable.SynchronizedQueue
import net.liftweb.common.Logger

/**
 *
 *
 * From: http://www.infoq.com/articles/scala_and_spring
 * User: warren
 * Date: 3/31/11
 * Time: 12:43 PM
 * 
 */


@Configuration
class AppConfig extends ApplicationListener[ContextRefreshedEvent] with Logger {

  def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    AppConfig.autowiredAnnotationBeanPostProcessor =
       event.getApplicationContext
          .getBean(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME).asInstanceOf[AutowiredAnnotationBeanPostProcessor]

    debug("Configuring App Context")
    AppConfig.readyToAutowire = true
    AppConfig.autowireQueue.foreach(obj => AppConfig.inject(obj))
  }

}

object AppConfig {
  var readyToAutowire: Boolean = false
  var autowiredAnnotationBeanPostProcessor: AutowiredAnnotationBeanPostProcessor = null
  var autowireQueue = new SynchronizedQueue[Any]

  def inject(obj: Any) = {
    if (readyToAutowire) {
      autowiredAnnotationBeanPostProcessor.processInjection(obj)
    } else {
      autowireQueue += obj
    }
  }
}

