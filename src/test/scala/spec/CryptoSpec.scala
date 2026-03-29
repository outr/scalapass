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
      // 12 byte nonce + 13 byte plaintext + 16 byte GCM auth tag = 41
      e.data.length should be(41)
      e.salt.length should be(8)
    }
    "decrypt a message" in {
      val result = instance.decrypt(encrypted.get)
      result should be(decrypted)
    }
    "produce unique ciphertext for the same plaintext" in {
      val e1 = instance.encrypt(decrypted)
      val e2 = instance.encrypt(decrypted)
      // Data lengths are equal but content differs due to unique nonces
      e1.data.length should be(e2.data.length)
      instance.decrypt(e1) should be(instance.decrypt(e2))
      // The encrypted data itself must differ (different nonces)
      e1.data should not be e2.data
    }
    "encrypt and decrypt an empty string" in {
      val e = instance.encrypt("")
      // 12 byte nonce + 0 byte plaintext + 16 byte GCM auth tag = 28
      e.data.length should be(28)
      instance.decrypt(e) should be("")
    }
    "encrypt and decrypt a large string" in {
      val large = "A" * 100000
      val e = instance.encrypt(large)
      instance.decrypt(e) should be(large)
    }
    "fail to decrypt with wrong key" in {
      val e = instance.encrypt(decrypted)
      val wrongCrypto = Crypto(CryptoSecrets.generate("wrong-password"))
      val wrongInstance = wrongCrypto.instance(e.salt)
      an[Exception] should be thrownBy {
        wrongInstance.decrypt(e)
      }
    }
  }
}
