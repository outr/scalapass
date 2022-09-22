package com.outr.scalapass

import fabric.io._
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
    val salt = Bytes.generate(saltBytes, if (saltStrong) Bytes.Algorithm.Strong else Bytes.Algorithm.Weak())
    hash(password, salt)
  }

  private def hash(password: String, salt: Bytes): String = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val spec = new PBEKeySpec(password.toCharArray, salt.array, iterationCount, keyLength)
    val key = factory.generateSecret(spec)
    val bytes = key.getEncoded
    val hashAndSalt = HashAndSalt(
      hash = PasswordFactory.base64(bytes),
      salt = salt
    )
    val json = JsonFormatter.Compact(hashAndSalt.json)
    json
  }

  override def verify(attemptedPassword: String, hash: String): Boolean = {
    val hashAndSalt = JsonParser(hash, Format.Json).as[HashAndSalt]
    val salt = hashAndSalt.salt
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
  lazy val iterations: Int = Profig("scalapass.pbkdf2.iterations").asOr[Int](10000)

  /**
    * The default key length to use (Defaults to 512)
    *
    * Configuration property: `scalapass.pbkdf2.keyLength`
    */
  lazy val keyLength: Int = Profig("scalapass.pbkdf2.keyLength").asOr[Int](512)
}