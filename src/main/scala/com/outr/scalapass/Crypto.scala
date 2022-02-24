package com.outr.scalapass

case class Crypto(secrets: CryptoSecrets,
                  iterationCount: Int = 65536,
                  keyLength: Int = 256) {
  def instance(salt: Bytes = Bytes.generate(8, Bytes.Algorithm.Strong)): CryptoInstance =
    CryptoInstance(this, salt)
}