package edu.yale.its.tp.cas.auth;

/** Interface for password-based authentication handlers. */
public interface PasswordHandler extends AuthHandler {

  /**
   * Authenticates the given username/password pair, returning true
   * on success and false on failure.
   */
  boolean authenticate(javax.servlet.ServletRequest reqyest,
                       String username,
                       String password);

}
