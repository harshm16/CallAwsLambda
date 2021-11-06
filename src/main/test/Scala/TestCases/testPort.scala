import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class testPort extends AnyFlatSpec with Matchers {
  val config: Config = ConfigFactory.load("lambdaJson")

  behavior of "Check if pattern is a String"

  it should "obtain a string" in {
    config.getInt("lambdaJson.port") shouldBe a [Int]
  }
}
