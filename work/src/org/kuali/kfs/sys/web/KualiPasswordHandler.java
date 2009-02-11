/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.web;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;

import edu.yale.its.tp.cas.auth.provider.WatchfulPasswordHandler;

public class KualiPasswordHandler extends WatchfulPasswordHandler {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiPasswordHandler.class);

    private static EncryptionService encryptionService;
    private static Boolean productionEnvironment = null;
    private static Boolean validatesPassword = null;
    private static IdentityManagementService identityManagementService;
    
    
    protected static IdentityManagementService getIdentityManagementService() {
        if ( identityManagementService == null ) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    protected static EncryptionService getEncryptionService() {
        if ( encryptionService == null ) {
            encryptionService = SpringContext.getBean(EncryptionService.class);
        }
        return encryptionService;
    }
    
    protected boolean isProductionEnvironment() {
        if ( productionEnvironment == null ) {
            productionEnvironment = SpringContext.getBean(KualiConfigurationService.class).isProductionEnvironment();
        }
        return productionEnvironment;
    }

    protected boolean doesValidatePassword() {
        if ( validatesPassword == null ) {
            validatesPassword = getIdentityManagementService().authenticationServiceValidatesPassword();
        }
        return validatesPassword;
    }
    
    /**
     * Authenticates the given username/password pair, returning true on success and false on failure.
     */
    public boolean authenticate(javax.servlet.ServletRequest request, String username, String password) {
        if (super.authenticate(request, username, password) != false) {
            try {
                if ( StringUtils.isNotBlank(username) ) {
                    //Person user = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(new AuthenticationUserId(username.trim()));                  
                    // Once the IdentityService facade is in place, we should use it to get the principal and clean up this code.
                    // check if the password needs to be checked (if in a production environment or password turned on explicitly)
                    if (isProductionEnvironment() || doesValidatePassword() ) {
                        // if so, hash the passed in password and compare to the hash retrieved from the database
                        //String hashedPassword = user.getFinancialSystemsEncryptedPasswordText();
                        if ( password == null ) {
                            password = "";
                        }
                        String hashedPassword = getEncryptionService().hash(password);
                        KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalNameAndPassword(username, password + EncryptionService.HASH_POST_PREFIX );
                        if (principal == null ) {
                            throw new Exception("User " + username + " was not found in the KIM Principal table or password does not match.");
                        }
                        return true;
                    } else {
                        KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(username);
                        if (principal == null ) {
                            throw new Exception("User " + username + " was not found in the KIM Principal table.");
                        }
                        
                        LOG.warn("WARNING: password checking is disabled - user " + username + " has been authenticated without a password.");
                        return true; // no need to check password - user's existence is enough
                    }
                }
            }
            catch (GeneralSecurityException ex) {
                LOG.error("Error validating password", ex);
                return false; // fail if the hash function fails
            }
            catch (Exception ex) {
                LOG.info("User " + username + " was not found in the Person table.");
                return false; // fail if user does not exist
            }

        }
        LOG.warn("CAS base password handler failed authenication for " + username + " based on number of attempts.");
        return false; // fail if we get to this point
    }
}

