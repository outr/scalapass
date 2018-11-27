package com.outr.scalapass

import java.nio.charset.Charset
import java.security.SecureRandom
import java.util.Base64

import de.mkammerer.argon2.{Argon2Factory, Argon2Helper}
import profig.Profig

/**
  * PasswordFactory provides functionality for creating and verifying passwords using Argon2id algorithm
  *
  * Mostly follows best practices here: https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet
  * Utilizes the Argon2 binding for the JVM here: https://github.com/phxql/argon2-jvm
  */
object PasswordFactory {
  private lazy val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

  /**
    * The maximum milliseconds to spend in the algorithm (Defaults to 1000L)
    *
    * Configuration property: `scalapass.maxMillis`
    */
  lazy val maxMillis: Long = Profig("scalapass.maxMillis").as[Long](1000L)

  /**
    * The memory for the algorithm to consume (Defaults to 65536)
    *
    * Configuration property: `scalapass.memory`
    */
  lazy val memory: Int = Profig("scalapass.memory").as[Int](65536)

  /**
    * The number of parallel threads to use for the algorithm (Defaults to 8)
    *
    * Configuration property: `scalapass.parallelism`
    */
  lazy val parallelism: Int = Profig("scalapass.parallelism").as[Int](8)

  /**
    * Flag to determine whether `iterations` should calculate the ideal number of iterations if no setting is supplied.
    * Note: This will drastically increase the initialization time of this factory when first used. (Defaults to false)
    *
    * Configuration property: `scalapass.determineIdealIterations`
    */
  lazy val determineIdealIterations: Boolean = Profig("scalapass.determineIdealIterations")
    .as[String]("false").toBoolean

  /**
    * The default number of iterations to use if `determineIdealIterations` is set to false (Defaults to 50)
    *
    * Configuration property: `scalapass.defaultIterations`
    */
  lazy val defaultIterations: Int = Profig("scalapass.defaultIterations").as[Int](50)

  /**
    * The number of iterations to utilize in the algorithm. Calculates the ideal if unset and `determineIdealIterations`
    * is set to true. Otherwise, uses `defaultIterations`.
    *
    * Configuration property: `scalapass.iterations`
    */
  lazy val iterations: Int = Profig("scalapass.iterations").as[Int](if (determineIdealIterations) {
    Argon2Helper.findIterations(argon2, maxMillis, memory, parallelism)
  } else {
    defaultIterations
  })

  /**
    * The default number of bytes to generate salts for if unspecified (Defaults to 64)
    *
    * Configuration property: `scalapass.saltBytes`
    */
  lazy val saltBytes: Int = Profig("scalapass.saltBytes").as[Int](64)

  /**
    * The default value for using strong SecureRandom to generate salts if unspecified (Defaults to false)
    *
    * Configuration property: `scalapass.saltStrong`
    */
  lazy val saltStrong: Boolean = Profig("scalapass.saltStrong").as[String]("false").toBoolean

  /**
    * The algorithm to use for generating weak (non-strong) salts (Defaults to SHA1PRNG)
    *
    * Configuration property: `scalapass.saltWeakAlgorithm`
    */
  lazy val saltWeakAlgorithm: String = Profig("scalapass.saltWeakAlgorithm").as[String]("SHA1PRNG")

  /**
    * The charset to use for generating and validating hashes (Defaults to "UTF-8")
    *
    * Configuration property: `scalapass.charset`
    */
  lazy val charset: Charset = Charset.forName(Profig("scalapass.charset").as[String]("UTF-8"))

  /**
    * Generates a salt for use in password hashing
    *
    * @param bytes the number of bytes to generate the salt (defaults to saltBytes)
    * @param strong whether to use Secure Random's strong instance (be warned that enabling this has a dramatic impact
    *               on performance, defaults to saltStrong)
    * @return generated Salt
    */
  def generateSalt(bytes: Int = saltBytes, strong: Boolean = saltStrong): Salt = {
    val secureRandom = if (strong) {
      SecureRandom.getInstanceStrong
    } else {
      SecureRandom.getInstance(saltWeakAlgorithm)
    }
    val salt = new Array[Byte](bytes)
    secureRandom.nextBytes(salt)
    val base64 = Base64.getEncoder.encodeToString(salt)
    new Salt(base64)
  }

  /**
    * Hashes the password with an optionally supplied salt. Argon2 automatically generates an internal salt, so
    * providing an additional salt isn't strictly necessary.
    *
    * @param password the password to generate a hash for
    * @param salt the salt to use to increase the complexity of the hash (defaults to None)
    * @param charset the charset to use when generating the hash (defaults to charset)
    * @param iterations the number of iterations to apply to the algorithm (defaults to iterations)
    * @param memory the amount of memory to utilize in the algorithm (defaults to memory)
    * @param parallelism the number of parallel threads to use with the algorithm (defaults to parallelism)
    * @return hashed password
    */
  def hash(password: String,
           salt: Option[Salt] = None,
           charset: Charset = this.charset,
           iterations: Int = this.iterations,
           memory: Int = this.memory,
           parallelism: Int = this.parallelism): String = {
    argon2.hash(iterations, memory, parallelism, salted(password, salt))
  }

  /**
    * Verifies an attempted password against a hashed password
    *
    * @param attemptedPassword the password to attempt
    * @param hash the generated hash to check the password against
    * @param salt the salt that was used to generate the hash (defaults to None)
    * @param charset the charset that was used when creating the hash (defaults to charset)
    * @return true if the attempted password matches the hash and salt
    */
  def verify(attemptedPassword: String,
             hash: String,
             salt: Option[Salt] = None,
             charset: Charset = this.charset): Boolean = {
    argon2.verify(hash, salted(attemptedPassword, salt))
  }

  private def salted(password: String, salt: Option[Salt]): String = {
    salt.map(s => s"${s.base64}$password").getOrElse(password)
  }
}