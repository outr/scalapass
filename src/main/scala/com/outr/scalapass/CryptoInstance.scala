package com.outr.scalapass

import javax.crypto.spec.{PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKeyFactory}

case class CryptoInstance(crypto: Crypto, salt: Bytes) {
  private lazy val secret = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val spec = new PBEKeySpec(crypto.secrets.passwordChars, salt.array, crypto.iterationCount, crypto.keyLength)
    new SecretKeySpec(factory.generateSecret(spec).getEncoded, "AES")
  }

  private lazy val encryption = {
    val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
    c.init(Cipher.ENCRYPT_MODE, secret, crypto.secrets.iv)
    c
  }

  private lazy val decryption = {
    val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
    c.init(Cipher.DECRYPT_MODE, secret, crypto.secrets.iv)
    c
  }

  def encrypt(value: String): Encrypted = {
    val bytes = encryption.doFinal(value.getBytes("UTF-8"))
    Encrypted(Bytes(bytes), salt)
  }

  def decrypt(encrypted: Encrypted): String = {
    val bytes = encrypted.data
    new String(decryption.doFinal(bytes.array), "UTF-8")
  }
}