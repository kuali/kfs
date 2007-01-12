package edu.yale.its.tp.cas.auth;

/** Interface for server-based authentication handlers. */
public interface TrustHandler extends AuthHandler {

  /**
   * Allows arbitrary logic to compute an authenticated user from
   * a ServletRequest.
   */
  String getUsername(javax.servlet.ServletRequest request);

}
