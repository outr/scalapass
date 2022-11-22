package com.outr.scalapass

import fabric.rw._

case class HashAndSalt(hash: String, salt: Bytes)

object HashAndSalt {
  implicit val rw: RW[HashAndSalt] = RW.gen
}