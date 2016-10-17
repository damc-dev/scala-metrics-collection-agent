package com.uprr.usm.agent

import java.lang.management.ManagementFactory

import com.uprr.usm.agent.config.JMSInstanceMetricsCollectorConfig
import com.uprr.usm.agent.dto.Metric

import akka.actor._
import com.uprr.usm.agent.actors.{MetricsPublisher,JMSInstanceMetricCollector}

object Main extends App {
  val rb = ManagementFactory.getRuntimeMXBean();

  val system = ActorSystem("JmsAgentSystem")
  val metricsPublisher = system.actorOf(Props[MetricsPublisher], name = "metrics-publisher")
  val instanceMetricsCollector = system.actorOf(
      Props(
          new JMSInstanceMetricCollector(
              new JMSInstanceMetricsCollectorConfig(1000), 
          metricsPublisher)
          ), name="instanceMetricsCollector")
          
  Thread sleep 10000
  println("Changing Collection Interval")
  instanceMetricsCollector ! JMSInstanceMetricCollector.UpdateConfig(new JMSInstanceMetricsCollectorConfig(5000))
 
}