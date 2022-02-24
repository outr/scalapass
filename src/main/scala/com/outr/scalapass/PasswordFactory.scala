package com.outr.scalapass

import java.util.Base64

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
  object base64 {
    def apply(value: String): Vector[Byte] = Base64.getDecoder.decode(value).toVector
    def apply(vector: Vector[Byte]): String = apply(vector.toArray)
    def apply(array: Array[Byte]): String = Base64.getEncoder.encodeToString(array)
  }
}