package com.outr.scalapass

import javax.crypto.spec.IvParameterSpec
import fabric.rw._

case class CryptoSecrets(password: String, initializationVector: Bytes) {
  private[scalapass] lazy val passwordChars: Array[Char] = password.toCharArray
  private[scalapass] lazy val iv: IvParameterSpec = new IvParameterSpec(initializationVector.array)
}

object CryptoSecrets {
  implicit val rw: RW[CryptoSecrets] = ccRW

  def generate(password: String): CryptoSecrets = {
    val iv = Bytes.generate(16, Bytes.Algorithm.Strong)
    CryptoSecrets(password, iv)
  }
}