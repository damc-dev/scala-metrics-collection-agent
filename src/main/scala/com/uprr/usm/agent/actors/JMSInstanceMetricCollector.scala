package com.uprr.usm.agent.actors

import scala.concurrent.duration._

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props

import java.lang.management.ManagementFactory

import com.uprr.usm.agent.config.JMSInstanceMetricsCollectorConfig
import com.uprr.usm.agent.dto.Metric


object JMSInstanceMetricCollector {
  def props(conf: JMSInstanceMetricsCollectorConfig, metricsPublisher: ActorRef) = Props(new JMSInstanceMetricCollector(conf, metricsPublisher))
  case object Collect
  case class UpdateConfig(newConfig: JMSInstanceMetricsCollectorConfig)
  case object ReloadConfigMessage
  case object StopMessage
}

class JMSInstanceMetricCollector(conf: JMSInstanceMetricsCollectorConfig, metricsPublisher: ActorRef) extends Actor with ActorLogging {
  import JMSInstanceMetricCollector._
  import context.dispatcher
  
  val rb = ManagementFactory.getRuntimeMXBean();

  private var frequency = conf.frequency
  private var scheduledCollection: Cancellable = _

  override def preStart() = {
    scheduleCollection()
  }

  override def postStop() = {
    scheduledCollection.cancel()
  }

  def scheduleCollection() =
    scheduledCollection = context.system.scheduler.
      schedule(0.seconds, frequency.millis, self, Collect)

  def updateConfiguration(conf: JMSInstanceMetricsCollectorConfig) = {
    scheduledCollection.cancel()
    frequency = conf.frequency
    scheduleCollection()
  }

  def receive = {
    case UpdateConfig(newConf) => 
      log.info(s"Updating instance config: $newConf")
      updateConfiguration(newConf)
    case Collect =>  
      metricsPublisher ! MetricsPublisher.MetricsMessage(new Metric(
    "test-asset",
    "JAVA_DEPLOYMENT_UNIT",
    "DEVELOPMENT",
    "uptime",
    "integer",
    String.valueOf(rb.getUptime),
    "millisecond"))
  }
}