package com.outr.scalapass

import fabric.parse.{Json, JsonWriter}
import fabric.rw._
import profig.Profig

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
  * Uses PBKDF2 for password hashing. Uses the PBKDF2WithHmacSHA512 algorithm.
  *
  * Primary reasons to consider over Argon2 is for FIPS compliance. Defaults are compliant with FIPS 140-2.
  */
case class PBKDF2PasswordFactory(saltBytes: Int = PBKDF2PasswordFactory.saltBytes,
                                 saltStrong: Boolean = PBKDF2PasswordFactory.saltStrong,
                                 iterationCount: Int = PBKDF2PasswordFactory.iterations,
                                 keyLength: Int = PBKDF2PasswordFactory.keyLength) extends PasswordFactory {
  override def hash(password: String): String = {
    val salt = PasswordFactory.generateSalt(saltBytes, saltStrong)
    hash(password, salt)
  }

  private def hash(password: String, salt: Salt): String = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val spec = new PBEKeySpec(password.toCharArray, salt.salt.toArray, iterationCount, keyLength)
    val key = factory.generateSecret(spec)
    val bytes = key.getEncoded
    val hashAndSalt = HashAndSalt(
      hash = PasswordFactory.base64(bytes),
      salt = PasswordFactory.base64(salt.salt)
    )
    val json = Json.format(hashAndSalt.toValue, JsonWriter.Compact)
    json
  }

  override def verify(attemptedPassword: String, hash: String): Boolean = {
    val hashAndSalt = Json.parse(hash).as[HashAndSalt]
    val salt = Salt(PasswordFactory.base64(hashAndSalt.salt))
    val attempted = this.hash(attemptedPassword, salt)
    attempted == hash
  }
}

object PBKDF2PasswordFactory {
  /**
    * The default salt bytes to use (Defaults to 128)
    *
    * Configuration property: `scalapass.pbkdf2.saltBytes`
    */
  lazy val saltBytes: Int = Profig("scalapass.pbkdf2.saltBytes").asOr[Int](128)

  /**
    * Default if strong salt generation should be used (Defaults to true)
    *
    * Configuration property: `scalapass.pbkdf2.saltStrong`
    */
  lazy val saltStrong: Boolean = Profig("scalapass.pbkdf2.saltStrong").asOr[Boolean](true)

  /**
    * The default number of iterations to use (Defaults to 10,000)
    *
    * Configuration property: `scalapass.pbkdf2.iterations`
    */
  lazy val iterations: Int = Profig("scalapass.pbkdf2.iterations").asOr[Int](10_000)

  /**
    * The default key length to use (Defaults to 512)
    *
    * Configuration property: `scalapass.pbkdf2.keyLength`
    */
  lazy val keyLength: Int = Profig("scalapass.pbkdf2.keyLength").asOr[Int](512)
}