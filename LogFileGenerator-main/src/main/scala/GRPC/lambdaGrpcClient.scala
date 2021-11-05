package GRPC

import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.examples.helloworld.logAnalysis.GreeterGrpc.GreeterBlockingStub
import io.grpc.examples.helloworld.logAnalysis.{GreeterGrpc, LambdaRequest}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

object lambdaGrpcClient {
  private[this] val logger = Logger.getLogger(classOf[lambdaGrpcClient].getName)

  val user_config: Config = ConfigFactory.load("lambdaJson.conf")

  val url: String = user_config.getString("lambdaJson.url")
  val bucket = (user_config.getString("lambdaJson.Bucket"))
  val key = (user_config.getString("lambdaJson.Key"))
  val timestamp = (user_config.getString("lambdaJson.Timestamp"))
  val interval = (user_config.getString("lambdaJson.Interval"))
  val pattern = (user_config.getString("lambdaJson.Pattern"))

  val port: Int = user_config.getInt("lambdaJson.port")

  /**
   *
   * @param host localhost
   * @param port port given in parameters.conf
   * @return
   */
  def apply(host: String, port: Int): lambdaGrpcClient = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = GreeterGrpc.blockingStub(channel)
    new lambdaGrpcClient(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val client = lambdaGrpcClient("localhost", port)
    try {
      client.callServer(bucket, key, timestamp, interval, pattern)
    }
    finally {
      client.stop()
    }
  }
}

class lambdaGrpcClient private(private val channel: ManagedChannel, private val blockingStub: GreeterBlockingStub) {
  private[this] val logger = Logger.getLogger(classOf[lambdaGrpcClient].getName)

  def stop(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  /**
   *
   * @param timestamp timestamp provided by the client
   * @param interval the interval which helps to search log statements
   */
  def callServer(bucket: String, key:String, timestamp: String, interval: String, pattern: String): Unit = {
    val request = LambdaRequest(bucket, key, timestamp, interval, pattern)
    try{
      val response = blockingStub.findLog(request)
      logger.info("Result= " + response.result)
      response.result
    }
    catch {
      case e:StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }
}
