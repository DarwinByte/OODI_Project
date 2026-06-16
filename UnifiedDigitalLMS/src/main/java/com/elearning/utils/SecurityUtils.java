/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// ==============================================================================
// CLASS: SecurityUtils
// RUBRIC FOCUS: Tool Usage & Cryptography
//
// Q&A DEFENSE:
// "A utility class for hashing. It uses SHA-256 via Java's MessageDigest. 
// Passwords are never compared in plain text, ensuring database security."
// ==============================================================================
package com.elearning.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecurityUtils {

    private SecurityUtils() {}

    public static String hashPassword(String plainTextPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainTextPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm unavailable.", e);
        }
    }

    public static boolean verifyPassword(String plainText, String storedHash) {
        return hashPassword(plainText).equals(storedHash);
    }
}