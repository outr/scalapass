package spec

import com.outr.scalapass._

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CryptoSpec extends AnyWordSpec with Matchers {
  "Crypto" should {
    val crypto = Crypto(CryptoSecrets.generate("testing"))
    val instance = crypto.instance()
    val decrypted = "Hello, World!"
    var encrypted: Option[Encrypted] = None

    "encrypt a message" in {
      val e = instance.encrypt(decrypted)
      encrypted = Some(e)
      e.data.length should be(16)
      e.salt.length should be(8)
    }
    "decrypt a message" in {
      val result = instance.decrypt(encrypted.get)
      result should be(decrypted)
    }
  }
}
