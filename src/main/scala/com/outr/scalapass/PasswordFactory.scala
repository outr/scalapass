package com.outr.scalapass

import java.security.SecureRandom
import java.util.Base64
import profig.Profig

/**
  * PasswordFactory provides functionality for creating and verifying passwords
  */
trait PasswordFactory {
  /**
    * Generates a hashed password that includes a generated salt
    */
  def hash(password: String): String

  /**
    * Hashes the attempted password and compares to the supplied hash
    */
  def verify(attemptedPassword: String, hash: String): Boolean
}

object PasswordFactory {
  /**
    * The algorithm to use for generating weak (non-strong) salts (Defaults to SHA1PRNG)
    *
    * Configuration property: `scalapass.saltWeakAlgorithm`
    */
  lazy val saltWeakAlgorithm: String = Profig("scalapass.saltWeakAlgorithm").asOr[String]("SHA1PRNG")

  /**
    * Generates a salt for use in password hashing
    *
    * @param bytes the number of bytes to generate the salt (defaults to saltBytes)
    * @param strong whether to use Secure Random's strong instance (be warned that enabling this has a dramatic impact
    *               on performance, defaults to saltStrong)
    * @return generated Salt
    */
  def generateSalt(bytes: Int, strong: Boolean): Salt = {
    val secureRandom = if (strong) {
      SecureRandom.getInstanceStrong
    } else {
      SecureRandom.getInstance(saltWeakAlgorithm)
    }
    val salt = new Array[Byte](bytes)
    secureRandom.nextBytes(salt)
    Salt(salt.toVector)
  }

  object base64 {
    def apply(value: String): Vector[Byte] = Base64.getDecoder.decode(value).toVector
    def apply(vector: Vector[Byte]): String = apply(vector.toArray)
    def apply(array: Array[Byte]): String = Base64.getEncoder.encodeToString(array)
  }
}