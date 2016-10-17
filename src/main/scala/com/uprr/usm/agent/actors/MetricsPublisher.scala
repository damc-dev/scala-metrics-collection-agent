package com.uprr.usm.agent.actors

import com.uprr.usm.agent.dto.Metric
import akka.actor.ActorLogging
import akka.actor.Actor

object MetricsPublisher {
  case class MetricsMessage(metric: Metric)
  case object StopMessage
}

class MetricsPublisher extends Actor with ActorLogging {
  import MetricsPublisher._
  def receive = {
    case MetricsMessage(metric) =>
      log.info(s"Received Metric: $metric")
    case StopMessage =>
      log.info("ShuttingDown MetricsPublisher")
      context.stop(self)
  }
}
