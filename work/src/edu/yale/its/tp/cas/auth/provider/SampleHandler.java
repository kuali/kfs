package edu.yale.its.tp.cas.auth.provider;

import edu.yale.its.tp.cas.auth.*;

/** A simple, dummy authenticator. */
public class SampleHandler extends WatchfulPasswordHandler {

    public boolean authenticate(javax.servlet.ServletRequest request,
                                String username,
                                String password) {

	/*
         * As a demonstration, accept any username/password combination
         * where the username matches the password.
         */
	if (username.equals(password))
	    return true;

	return false;
    }
}
