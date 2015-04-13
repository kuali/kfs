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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForEffortCertification;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

public class LedgerBalanceForEffortCertificationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private LookupableHelperService effortLedgerBalanceLookupableHelperService;

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH));

        List<LedgerBalanceForEffortCertification> searchResults = (List<LedgerBalanceForEffortCertification>) effortLedgerBalanceLookupableHelperService.getSearchResults(fieldValues);

        return searchResults;
    }

    /**
     * Sets the effortLedgerBalanceLookupableHelperService attribute value.
     * @param effortLedgerBalanceLookupableHelperService The effortLedgerBalanceLookupableHelperService to set.
     */
    public void setEffortLedgerBalanceLookupableHelperService(LookupableHelperService effortLedgerBalanceLookupableHelperService) {
        if(effortLedgerBalanceLookupableHelperService != null) {
            this.effortLedgerBalanceLookupableHelperService = effortLedgerBalanceLookupableHelperService;
        }
        else {
            this.effortLedgerBalanceLookupableHelperService = new KualiLookupableHelperServiceImpl();
        }
    }
}
