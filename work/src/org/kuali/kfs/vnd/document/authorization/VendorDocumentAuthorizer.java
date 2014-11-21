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
package org.kuali.kfs.vnd.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.vnd.VendorPropertyConstants;

/**
 * Authorizer class for Vendor maintenance document
 */
public class VendorDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        Set<String> readOnlySectionIds = super.getSecurePotentiallyReadOnlySectionIds();
        
        // vendor contracts and commodity codes are the potentially readonly sections
        readOnlySectionIds.add(VendorPropertyConstants.VENDOR_CONTRACT);
        readOnlySectionIds.add(VendorPropertyConstants.VENDOR_COMMODITIES_CODE);

        return readOnlySectionIds;
    }    
    
}

