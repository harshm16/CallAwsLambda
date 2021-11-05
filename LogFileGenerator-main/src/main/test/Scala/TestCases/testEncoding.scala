import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.URLEncoder

class testEncoding extends AnyFlatSpec with Matchers {
  val config: Config = ConfigFactory.load("lambdaJson")

  behavior of "Check if Encoding is proper"

  it should "obtain encoded timestamp" in {
    URLEncoder.encode(config.getString("lambdaJson.timestamp"), "UTF-8") shouldBe a [String]
  }
}

