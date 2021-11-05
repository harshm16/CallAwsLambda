package REST

import com.typesafe.config.{Config, ConfigFactory}
import java.net.URLEncoder

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

object LambdaRestClient {

//  val logger = CreateLogger(classOf[LambdaRestClient])

  def main(args: Array[String]): Unit = {


    val user_config: Config = ConfigFactory.load("lambdaJson.conf")

    val url: String = user_config.getString("lambdaJson.url")
    val bucket = (user_config.getString("lambdaJson.Bucket"))
    val key = (user_config.getString("lambdaJson.Key"))
    val timestamp = (user_config.getString("lambdaJson.Timestamp"))
    val interval = (user_config.getString("lambdaJson.Interval"))
    val pattern = (user_config.getString("lambdaJson.Pattern"))
//
//    val restCall = HttpRequest(
//      uri = "https://2k5z8ufd05.execute-api.us-east-2.amazonaws.com/loganalysisAPI/pandas?bucket=logdata-bucket&key=log7.log&timestamp=23%3A13%3A18.595&interaval=0%3A01%3A00.0&pattern=(%5Ba-c%5D%5Be-g%5D%5B0-3%5D%7C%5BA-Z%5D%5B5-9%5D%5Bf-w%5D)%7B5%2C15%7D")
//    println(restCall)

    //Call the AWS Lambda function.
    //The string inputs need to be encoded while passing.
    val api_response = scala.io.Source.fromURL(url + "?bucket=" + URLEncoder.encode(bucket, "UTF-8") + "&key=" + URLEncoder.encode(key, "UTF-8") + "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") + "&interaval=" + URLEncoder.encode(interval, "UTF-8") + "&pattern=" + URLEncoder.encode(pattern, "UTF-8"))
    //val api_response = scala.io.Source.fromURL("https://2k5z8ufd05.execute-api.us-east-2.amazonaws.com/loganalysisAPI/pandas?bucket=logdata-bucket&key=log7.log&timestamp=23%3A13%3A18.595&interaval=0%3A01%3A00.0&pattern=(%5Ba-c%5D%5Be-g%5D%5B0-3%5D%7C%5BA-Z%5D%5B5-9%5D%5Bf-w%5D)%7B5%2C15%7D")

    val output_string = api_response.mkString
    println(output_string)


    }
  }
