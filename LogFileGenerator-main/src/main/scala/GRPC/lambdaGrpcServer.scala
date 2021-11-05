package GRPC


import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.examples.helloworld.logAnalysis.GreeterGrpc.GreeterBlockingStub
import io.grpc.examples.helloworld.logAnalysis.{GreeterGrpc, LambdaRequest, LambdaReply}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Server, ServerBuilder, StatusRuntimeException}

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * Implements the Log Function gRPC service.
 */

object lambdaGrpcServer {

  private val logger = Logger.getLogger(classOf[lambdaGrpcServer].getName)
  val user_config: Config = ConfigFactory.load("lambdaJson.conf")

  val port: Int = user_config.getInt("lambdaJson.port")

  val url: String = user_config.getString("lambdaJson.url")
  def main(args: Array[String]): Unit = {
    val server = new lambdaGrpcServer(ExecutionContext.global)
    startServer(server)
    blockServerUntilShutdown(server)
  }

  def startServer(server: lambdaGrpcServer): Unit = {
    server.start()
  }

  def blockServerUntilShutdown(server: lambdaGrpcServer): Unit =  {
    server.blockUntilShutdown()
  }
}

class lambdaGrpcServer(executionContext: ExecutionContext) {
  self =>
  private[this] var server: Server = null
  private val logger = Logger.getLogger(classOf[lambdaGrpcServer].getName)

  private def start(): Unit = {
    server = ServerBuilder.forPort(lambdaGrpcServer.port).addService(GreeterGrpc.bindService(new Logimpl, executionContext)).build.start
    logger.info("Server started, listening on " + lambdaGrpcServer.port)
    sys.addShutdownHook {
      System.err.println("shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("server shut down")
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  class Logimpl extends GreeterGrpc.Greeter {
    override def findLog(req: LambdaRequest): Future[LambdaReply] = {
      //Call Lambda API Gateway

      Try(scala.io.Source.fromURL(lambdaGrpcServer.url + "/grpc?bucket=" + req.bucket + "&key=" + req.key + "&timestamp=" + req.timestamp + "&interval=" + req.interval + "&pattern=" + req.pattern)) match {
        case Success(response) => {
          val output_string = response.mkString
          val reply = LambdaReply(result = output_string)
          response.close()
          Future.successful(reply)
        }
        case Failure(response) => {
          val output_string = "Timestamp not found"
          val reply = LambdaReply(result = output_string)
          Future.successful(reply)
        }
      }
    }
  }
}
