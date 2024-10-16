import {Injectable, OnInit} from '@angular/core';
import {NotificationType} from "../enum/notification-type.enum";

@Injectable({
  providedIn: 'root'
})
export class CryptoService {

  private secretKey: CryptoKeyPair;

  constructor() {
    this.generateKeyPair().then((result) =>{
      this.secretKey = result;
    });
  }

  generateKeyPair(): Promise<CryptoKeyPair> {
    return new Promise(async (resolve, reject) => {

      try {
        const key = this.secretKey = await crypto.subtle.generateKey(
          {
            name: "RSA-OAEP",
            modulusLength: 2048,
            publicExponent: new Uint8Array([1, 0, 1]), // Equivalent to 65537
            hash: "SHA-256", // Hash algorithm to use for RSA
          },
          true,
          ["encrypt", "decrypt"]
        );
        resolve(key)
      } catch (error) {
        reject(error)
      }
    })
  }

  encryptData(data: string): Promise<string> {
    return new Promise((resolve, reject) => {
      try {
        const encoder = new TextEncoder();
        const dataAsUint8Array = encoder.encode(data);

        crypto.subtle.encrypt(
          {
            name: "RSA-OAEP",
          },
          this.secretKey.publicKey, // Use the public key here
          dataAsUint8Array
        )
          .then((encryptedBuffer) => {
            resolve(this.bufferToHex(encryptedBuffer)); // Convert to hex string
          })
          .catch((error) => {
            reject(error);
          });
      } catch (error) {
        reject(error);
      }
    });
  }

  decryptData(encryptedHex: string): Promise<string> {
    return new Promise((resolve, reject) => {
      try {
        const encryptedBuffer = this.hexToBuffer(encryptedHex);

        crypto.subtle.decrypt(
          {
            name: "RSA-OAEP",
          },
          this.secretKey.privateKey, // Use the private key here
          encryptedBuffer
        )
          .then((decryptedBuffer) => {
            const decoder = new TextDecoder();
            resolve(decoder.decode(decryptedBuffer)); // Return decrypted string
          })
          .catch((error) => {
            reject(error);
          });
      } catch (error) {
        reject(error);
      }
    });
  }

// Convert buffer to hex string
  bufferToHex(buffer: ArrayBuffer): string {
    return Array.from(new Uint8Array(buffer))
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
  }

// Convert hex string to buffer
  hexToBuffer(hex: string): Uint8Array {
    const bytes = new Uint8Array(Math.ceil(hex.length / 2));
    for (let i = 0; i < bytes.length; i++) {
      bytes[i] = parseInt(hex.substr(i * 2, 2), 16);
    }
    return bytes;
  }
}
