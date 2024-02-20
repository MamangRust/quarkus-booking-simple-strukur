package org.sanedge.security;

public interface HashProvider {
    String hashPassword(String password);

    boolean checkPassword(String plaintext, String hashed);
}
