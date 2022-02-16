package spec

import com.outr.scalapass.{Argon2PasswordFactory, PBKDF2PasswordFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PasswordFactorySpec extends AnyWordSpec with Matchers {
  "PasswordFactory" when {
    "using PBKDF2" should {
      lazy val factory = PBKDF2PasswordFactory()
      var testing123Hash: String = ""
      "create a password without a salt specified" in {
        val hash = factory.hash("testing123")
        testing123Hash = hash
      }
      "verify valid password against the created hash" in {
        factory.verify("testing123", testing123Hash) should be(true)
      }
      "verify invalid password against the created hash" in {
        factory.verify("123testing", testing123Hash) should be(false)
      }
    }
    "using Argon2id" should {
      lazy val factory = Argon2PasswordFactory()
      var testing123Hash: String = ""
      "create a password without a salt specified" in {
        val hash = factory.hash("testing123")
        testing123Hash = hash
      }
      "verify valid password against the created hash" in {
        factory.verify("testing123", testing123Hash) should be(true)
      }
      "verify invalid password against the created hash" in {
        factory.verify("123testing", testing123Hash) should be(false)
      }
    }
  }
}
