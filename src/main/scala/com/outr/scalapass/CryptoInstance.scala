package com.outr.scalapass

import javax.crypto.spec.{GCMParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKeyFactory}
import java.security.SecureRandom

case class CryptoInstance(crypto: Crypto, salt: Bytes) {
  private lazy val secret = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val spec = new PBEKeySpec(crypto.secrets.passwordChars, salt.array, crypto.iterationCount, crypto.keyLength)
    new SecretKeySpec(factory.generateSecret(spec).getEncoded, "AES")
  }

  private val NonceLength = 12
  private val TagBits = 128

  def encrypt(value: String): Encrypted = {
    val nonce = new Array[Byte](NonceLength)
    new SecureRandom().nextBytes(nonce)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TagBits, nonce))
    val ciphertext = cipher.doFinal(value.getBytes("UTF-8"))
    Encrypted(Bytes(nonce ++ ciphertext), salt)
  }

  def decrypt(encrypted: Encrypted): String = {
    val all = encrypted.data.array
    val nonce = all.take(NonceLength)
    val ciphertext = all.drop(NonceLength)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TagBits, nonce))
    new String(cipher.doFinal(ciphertext), "UTF-8")
  }
}
