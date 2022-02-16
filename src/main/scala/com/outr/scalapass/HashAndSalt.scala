package com.outr.scalapass

import fabric.rw._

case class HashAndSalt(hash: String, salt: String)

object HashAndSalt {
  implicit val rw: RW[HashAndSalt] = ccRW
}