/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsLookupService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Service with methods related to Contracts & Grants Lookups.
 */
public class ContractsAndGrantsLookupServiceImpl implements ContractsAndGrantsLookupService {

    @Override
    public boolean setupSearchFields(Map<String, String> fieldValues, String userNameField, String universalUserIdField) {
        if (!StringUtils.isBlank(fieldValues.get(userNameField))) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(fieldValues.get(userNameField));

            if (principal == null) {
                return false;
            }

            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(universalUserIdField, principal.getPrincipalId());
            fieldValues.remove(userNameField);
        }

        return true;
    }

}
