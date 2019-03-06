package com.example.streaming

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object StreamApp {


  def main(args:Array[String]) {

    val conf = new SparkConf()
    .setAppName(getClass.getName)
    .setIfMissing("spark.master", "local[*]")

    val sc = new SparkContext(conf)

    val checkpointDir = "spark-checkpoint"
    val rawStorage = "raw-storage"

    def createSSC() = {
      val ssc =  new StreamingContext(sc, Seconds(5))
      ssc.checkpoint(checkpointDir)
      ssc
    }

    val ssc = StreamingContext.getOrCreate(checkpointDir, createSSC)

    val raw = ssc.socketTextStream("localhost"
      , 9999, StorageLevel.MEMORY_ONLY)

    raw.saveAsTextFiles(rawStorage + "/stream", "")

    raw.print()

    ssc.start()
    ssc.awaitTermination()

  }
}