package com.uprr.usm.agent.dto

class Metric(
    assetName: String,
    assetType: String,
    environment: String,
    name: String,
    dataType: String,
    value: String,
    unitOfMeasure: String) {

  override def toString: String = {
    return s"Metric[$assetName, $assetType, $environment, $name, $dataType, $value, $unitOfMeasure]"
  }
}
