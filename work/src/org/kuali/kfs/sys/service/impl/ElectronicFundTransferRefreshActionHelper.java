/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.web.struts.form.ElectronicFundTransferForm;
import org.kuali.rice.KNSServiceLocator;

/**
 * Represents a web action that occurs when a user returns a bunch of selected claims and redirects to the electronic funds transfer "claimng" page
 */
public class ElectronicFundTransferRefreshActionHelper implements ElectronicFundTransferActionHelper {
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private DataDictionaryService ddService;
    
    private static final String BASIC_FORWARD = "basic";
    private static final String ACTION_NAME = "claim";
    private static final String PORTAL_FORWARD = "portal";

    /**
     * @see org.kuali.kfs.service.ElectronicFundTransferActionHelper#performAction(org.kuali.core.web.struts.form.KualiForm, org.apache.struts.action.ActionMapping)
     */
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map params, String basePath) {
        // is the current user able to claim electronic funds?
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        if (!electronicPaymentClaimingService.isUserMemberOfClaimingGroup(currentUser)) {
            throw new AuthorizationException(currentUser.getPersonUserIdentifier(), ElectronicFundTransferRefreshActionHelper.ACTION_NAME, ddService.getDataDictionary().getBusinessObjectEntry(ElectronicPaymentClaim.class.getName()).getObjectLabel());
        }
        // does the current user have claimed funds waiting for them?
        String lookupResultsSequenceNumber = null;
        if (params.get("lookupResultsSequenceNumber") != null) {
            lookupResultsSequenceNumber = ((String[])params.get("lookupResultsSequenceNumber"))[0];
        }
        if (StringUtils.isBlank(lookupResultsSequenceNumber)) {
            return mapping.findForward(PORTAL_FORWARD);
        }
        List<ElectronicPaymentClaim> claims = getClaimedPayments(currentUser, lookupResultsSequenceNumber);
        if (claims.size() == 0) {
            return mapping.findForward(PORTAL_FORWARD);
        }
        // if so, load their currently claimed electronic funds to the form
        form.setClaims(claims);
        // and redirect
        return mapping.findForward(BASIC_FORWARD);
    }
    
    /**
     * Gets the selected electronic payment claim records from the LookupResults service
     * @param currentUser the claiming user
     * @param lookupResultsSequenceNumber the parameter for the lookup results sequence number
     * @return a list of claims
     */
    private List<ElectronicPaymentClaim> getClaimedPayments(UniversalUser currentUser, String lookupResultsSequenceNumber) {
        List<ElectronicPaymentClaim> claims = new ArrayList<ElectronicPaymentClaim>();
        try {
            Collection selectedClaims = KNSServiceLocator.getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, ElectronicPaymentClaim.class, currentUser.getPersonUniversalIdentifier());
            for (Object claimAsObj : selectedClaims) {
                ElectronicPaymentClaim claim = (ElectronicPaymentClaim) claimAsObj;
                if (!claim.getPaymentClaimStatusCode().equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED) && StringUtils.isBlank(claim.getReferenceFinancialDocumentNumber())) {
                    claims.add(claim);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }        
        return claims;
    }
    
    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }
    
    /**
     * Sets the ddService attribute value.
     * @param ddService The ddService to set.
     */
    public void setDataDictonaryService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }
}
