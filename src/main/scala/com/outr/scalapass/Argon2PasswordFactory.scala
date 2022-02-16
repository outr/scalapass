package com.outr.scalapass

import de.mkammerer.argon2.Argon2Helper
import profig.Profig

/**
  * Uses Argon2 for password hashing
  *
  * Mostly follows best practices here: https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet
  * Utilizes the Argon2 binding for the JVM here: https://github.com/phxql/argon2-jvm
  */
case class Argon2PasswordFactory(iterations: Int = Argon2PasswordFactory.iterations,
                                 memory: Int = Argon2PasswordFactory.memory,
                                 parallelism: Int = Argon2PasswordFactory.parallelism,
                                 argon2: Argon2 = Argon2.id,
                                 saltLength: Int = Argon2.saltLength,
                                 hashLength: Int = Argon2.hashLength) extends PasswordFactory {
  private lazy val instance = argon2.create(saltLength, hashLength)

  override def hash(password: String): String =
    instance.hash(iterations, memory, parallelism, password.getBytes("UTF-8"))

  override def verify(attemptedPassword: String, hash: String): Boolean =
    instance.verify(hash, attemptedPassword.toCharArray)
}

object Argon2PasswordFactory {
  /**
    * The maximum milliseconds to spend in the algorithm (Defaults to 1000L)
    *
    * Configuration property: `scalapass.argon2.maxMillis`
    */
  lazy val maxMillis: Long = Profig("scalapass.argon2.maxMillis").asOr[Long](1000L)

  /**
    * The memory for the algorithm to consume (Defaults to 65536)
    *
    * Configuration property: `scalapass.argon2.memory`
    */
  lazy val memory: Int = Profig("scalapass.argon2.memory").asOr[Int](65536)

  /**
    * The number of parallel threads to use for the algorithm (Defaults to 8)
    *
    * Configuration property: `scalapass.argon2.parallelism`
    */
  lazy val parallelism: Int = Profig("scalapass.argon2.parallelism").asOr[Int](8)

  /**
    * Flag to determine whether `iterations` should calculate the ideal number of iterations if no setting is supplied.
    * Note: This will drastically increase the initialization time of this factory when first used. (Defaults to false)
    *
    * Configuration property: `scalapass.argon2.determineIdealIterations`
    */
  lazy val determineIdealIterations: Boolean = Profig("scalapass.argon2.determineIdealIterations")
    .asOr[Boolean](false)

  /**
    * The default number of iterations to use if `determineIdealIterations` is set to false (Defaults to 50)
    *
    * Configuration property: `scalapass.argon2.defaultIterations`
    */
  lazy val defaultIterations: Int = Profig("scalapass.argon2.defaultIterations").asOr[Int](50)

  /**
    * The number of iterations to utilize in the algorithm. Calculates the ideal if unset and `determineIdealIterations`
    * is set to true. Otherwise, uses `defaultIterations`.
    *
    * Configuration property: `scalapass.argon2.iterations`
    */
  lazy val iterations: Int = Profig("scalapass.argon2.iterations").asOr[Int](if (determineIdealIterations) {
    Argon2Helper.findIterations(Argon2.id.create(), maxMillis, memory, parallelism)
  } else {
    defaultIterations
  })
}