package com.outr.scalapass

import fabric.Arr
import fabric.define.{DefType, Definition}
import fabric.rw.RW.from
import fabric.rw._
import profig.Profig

import java.security.SecureRandom
import scala.reflect.ClassTag

case class Bytes private(private[scalapass] val array: Array[Byte]) extends AnyVal {
  def length: Int = array.length
}

object Bytes {
  /**
    * The algorithm to use for generating weak (non-strong) bytes (Defaults to SHA1PRNG)
    *
    * Configuration property: `scalapass.weakAlgorithm`
    */
  lazy val DefaultWeakAlgorithm: String = Profig("scalapass.weakAlgorithm").asOr[String]("SHA1PRNG")

  // TODO: Remove after the next release of Fabric - this is built-in
  private implicit def arrayRW[V: RW: ClassTag]: RW[Array[V]] = from[Array[V]](
    v => Arr(v.map(_.json).toVector),
    v => v.asVector.map(_.as[V]).toArray,
    Definition(DefType.Arr(implicitly[RW[V]].definition))
  )
  implicit val rw: RW[Bytes] = RW.gen

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