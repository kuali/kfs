/*
 * Copyright 2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.arizona.rice.kim.impl.identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.jws.WebParam;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.impl.identity.IdentityArchiveService;
import org.kuali.rice.kim.service.LdapIdentityService;


public class IdentityCurrentAndArchivedServiceImpl extends org.kuali.rice.kim.impl.identity.IdentityCurrentAndArchivedServiceImpl {
    private LdapIdentityService ldapIdentityService;
    
    public IdentityCurrentAndArchivedServiceImpl(IdentityService identityService, IdentityArchiveService identityArchiveService) {
        super(identityService, identityArchiveService);
        ldapIdentityService = (LdapIdentityService)identityService;
    }

    @Override
    public List<Principal> getPrincipals(@WebParam(name = "principalIds") List<String> principalIds) {
        // UAF-6 - Performance improvements to improve user experience for AWS deployment 
        // try to pull ids in single query - if we miss any then
        // try individually
        List <Principal> retval = ldapIdentityService.getPrincipals(principalIds);
        
        if (retval == null) {
            retval =  new ArrayList<Principal>();
        }  
        
        Set <String> idset = new HashSet(retval);
        
        List <String> notfound = new ArrayList<String>();
        
        for(String id: principalIds) {
            if (!idset.contains(id)) {
                notfound.add(id);
            }
        }
        
        if (!notfound.isEmpty()) {
            List <EntityDefault> entityDefaults = ldapIdentityService.getEntityDefaultsByPrincipalIds(notfound);
            for (EntityDefault ed : entityDefaults) {
                if ((ed.getPrincipals() != null) && !ed.getPrincipals().isEmpty()) {
                    Principal p = ed.getPrincipals().get(0);
                    
                    if (!idset.contains(p.getPrincipalId())) {
                        idset.add(p.getPrincipalId());
                        retval.add(p);
                    }
                }
            }
        }
        
        if (!retval.isEmpty()) {
            return retval;
        } else {
            return null;
        }
    }
}
