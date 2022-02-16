package com.outr.scalapass

import de.mkammerer.argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types
import profig.Profig

sealed trait Argon2 {
  protected def `type`: Argon2Types

  def create(saltLength: Int = Argon2.saltLength, hashLength: Int = Argon2.hashLength): argon2.Argon2 =
    Argon2Factory.create(`type`, saltLength, hashLength)
}

object Argon2 {
  /**
    * The default salt length to use (Defaults to 16)
    *
    * Configuration property: `scalapass.argon2.saltLength`
    */
  lazy val saltLength: Int = Profig("scalapass.argon2.saltLength").asOr[Int](16)

  /**
    * The default hash length to use (Defaults to 32)
    *
    * Configuration property: `scalapass.argon2.hashLength`
    */
  lazy val hashLength: Int = Profig("scalapass.argon2.hashLength").asOr[Int](32)

  case object i extends Argon2 {
    override def `type`: Argon2Types = Argon2Factory.Argon2Types.ARGON2i
  }
  case object d extends Argon2 {
    override def `type`: Argon2Types = Argon2Factory.Argon2Types.ARGON2d
  }
  case object id extends Argon2 {
    override def `type`: Argon2Types = Argon2Factory.Argon2Types.ARGON2id
  }
}