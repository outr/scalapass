package com.outr.scalapass

import fabric.Arr
import fabric._
import fabric.rw._
import profig.Profig

import java.security.SecureRandom

class Bytes(private[scalapass] val array: Array[Byte]) extends AnyVal {
  def length: Int = array.length
}

object Bytes {
  /**
    * The algorithm to use for generating weak (non-strong) bytes (Defaults to SHA1PRNG)
    *
    * Configuration property: `scalapass.weakAlgorithm`
    */
  lazy val DefaultWeakAlgorithm: String = Profig("scalapass.weakAlgorithm").asOr[String]("SHA1PRNG")

  implicit val rw: RW[Bytes] = RW(
    b => Arr(b.array.toVector.map(b => num(b.toLong))),
    v => new Bytes(v.asVector.map(_.asByte).toArray)
  )

  sealed trait Algorithm

  object Algorithm {
    case object Strong extends Algorithm

    case class Weak(algorithm: String = DefaultWeakAlgorithm) extends Algorithm
  }

  def apply(array: Array[Byte]): Bytes = new Bytes(array)

  def generate(bytes: Int, algorithm: Algorithm): Bytes = {
    val random = algorithm match {
      case Algorithm.Strong => SecureRandom.getInstanceStrong
      case Algorithm.Weak(a) => SecureRandom.getInstance(a)
    }
    val array = new Array[Byte](bytes)
    random.nextBytes(array)
    new Bytes(array)
  }
}