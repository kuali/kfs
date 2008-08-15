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
import org.kuali.rice.kim.v2.bo.entity.KimPrincipal;
import org.kuali.rice.kim.v2.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.v2.service.AuthenticationService;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;

import edu.yale.its.tp.cas.auth.provider.WatchfulPasswordHandler;

public class KualiPasswordHandler extends WatchfulPasswordHandler {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiPasswordHandler.class);

    /**
     * Authenticates the given username/password pair, returning true on success and false on failure.
     */
    public boolean authenticate(javax.servlet.ServletRequest request, String username, String password) {
        if (super.authenticate(request, username, password) != false) {
            try {
                if (username != null && !username.trim().equals("")) {
                    //UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(username.trim()));                  
                    // Once the IdentityService facade is in place, we should use it to get the principal and clean up this code.
                    KimPrincipal principal = null;
                    Map criteria = new HashMap();
                    criteria.put("principalName", username);
                    Collection principals = KNSServiceLocator.getBusinessObjectService().findMatching(KimPrincipalImpl.class, criteria);
                    if (principals.isEmpty() || principals.size() > 1) {
                        throw new UserNotFoundException("User " + username + " was not found in the KIM Principal table.");
                    } else {
                        principal = (KimPrincipal) principals.iterator().next();
                    }
                    
                    // check if the password needs to be checked (if in a production environment or password turned on explicitly)
                    if (SpringContext.getBean(KualiConfigurationService.class).isProductionEnvironment() || SpringContext.getBean(AuthenticationService.class).validatePassword()) {
                        // if so, hash the passed in password and compare to the hash retrieved from the database
                        //String hashedPassword = user.getFinancialSystemsEncryptedPasswordText();
                        String hashedPassword = principal.getPassword();
                        if (hashedPassword == null) {
                            hashedPassword = "";
                        }
                        hashedPassword = StringUtils.stripEnd(hashedPassword, EncryptionService.HASH_POST_PREFIX);
                        if (SpringContext.getBean(EncryptionService.class).hash(password.trim()).equals(hashedPassword)) {
                            return true; // password matched
                        }
                    }
                    else {
                        LOG.warn("WARNING: password checking is disabled - user " + username + " has been authenticated without a password.");
                        return true; // no need to check password - user's existence is enough
                    }
                }
            }
            catch (GeneralSecurityException ex) {
                LOG.error("Error validating password", ex);
                return false; // fail if the hash function fails
            }
            catch (UserNotFoundException ex) {
                LOG.info("User " + username + " was not found in the UniversalUser table.");
                return false; // fail if user does not exist
            }

        }
        LOG.warn("CAS base password handler failed authenication for " + username + " based on number of attempts.");
        return false; // fail if we get to this point
    }
}
