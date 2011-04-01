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

