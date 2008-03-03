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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.service.ElectronicPaymentClaimingDocument;
import org.kuali.kfs.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.web.struts.form.ElectronicFundTransferForm;

/**
 * An Electronic Funds Transfer action which claims the electronic payment claims in a form and redirects
 * to the claiming document.
 */
public class ElectronicFundTransferClaimActionHelper implements ElectronicFundTransferActionHelper {
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private DataDictionaryService ddService;
    private BusinessObjectService boService;
    
    private static final String ACTION_NAME = "claim";
    private static final String CHOSEN_DOCUMENT_PROPERTY = "chosenElectronicPaymentClaimingDocumentCode";
    private static final String BASIC_FORWARD = "basic";
    private static final String PORTAL_FORWARD = "portal";

    /**
     * Claims the ElectronicPaymentClaim records with a document and then redirects to that docment.
     * @see org.kuali.kfs.service.ElectronicFundTransferActionHelper#performAction(org.kuali.core.web.struts.form.KualiForm, org.apache.struts.action.ActionMapping)
     */
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map paramMap, String basePath) {
        // can the user claim electronic payments at all?
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        if (!electronicPaymentClaimingService.isUserMemberOfClaimingGroup(currentUser)) {
            throw new AuthorizationException(currentUser.getPersonUserIdentifier(), ElectronicFundTransferClaimActionHelper.ACTION_NAME, ddService.getDataDictionary().getBusinessObjectEntry(ElectronicPaymentClaim.class.getName()).getTitleAttribute());
        }
        String chosenDoc = form.getChosenElectronicPaymentClaimingDocumentCode();
        boolean continueClaiming = checkChosenDocumentType(chosenDoc);
        // get the requested document claiming helper
        if (continueClaiming) {
            List<ElectronicPaymentClaim> claims = form.getClaims();
            if (electronicPaymentClaimingService.isElectronicPaymentAdministrator(currentUser)) {
                claims = handlePreClaimedRecords(claims);
                if (claims.size() == 0) {
                    // no more claims to process...so don't make a document, just redirect to the portal
                    return mapping.findForward(PORTAL_FORWARD);
                }
            }
            ElectronicPaymentClaimingDocument documentCreationHelper = getRequestedClaimingHelper(form.getChosenElectronicPaymentClaimingDocumentCode(), form.getAvailableClaimingDocuments(), currentUser);
            // take the claims from the form, create a document, and redirect to the given URL...which is easy
            String redirectURL = electronicPaymentClaimingService.createPaymentClaimingDocument(form.getClaims(), documentCreationHelper, currentUser);
            return new ActionForward(redirectURL, true);
        } else {
            return mapping.findForward(ElectronicFundTransferClaimActionHelper.BASIC_FORWARD);
        }
    }
    
    /**
     * Verifies that the chosenElectronicPaymentClaimingDocumentCode has been filled in.
     * @param chosenDoc the value of chosenElectronicPaymentClaimingDocumentCode from the form
     * @return true if the validation resulted in no errors, false if otherwise
     */
    private boolean checkChosenDocumentType(String chosenDoc) {
        boolean result = true;
        if (StringUtils.isBlank(chosenDoc)) {
            GlobalVariables.getErrorMap().putError(ElectronicFundTransferClaimActionHelper.CHOSEN_DOCUMENT_PROPERTY, KFSKeyConstants.ElectronicPaymentClaim.ERROR_EFT_NO_CHOSEN_CLAIMING_DOCTYPE, new String[]{});
            result = false;
        }
        return result;
    }

    /**
     * Using user entered form values, determines which of the available ElectronicPaymentClaimingDocument implementations to use. 
     * @param chosenDoc the document type code for the doc that the user selected
     * @param availableClaimingDocs a List of ElectronicPaymentClaimingDocument implementations that can be used by the given user
     * @param currentUser the currently logged in user
     * @throws AuthorizationException thrown if the user entered an invalid or unusable ElectronicPaymentClaimingDocument code
     * @return an ElectronicPaymentClaimingDocument helper to use to create the document
     */
    private ElectronicPaymentClaimingDocument getRequestedClaimingHelper(String chosenDoc, List<ElectronicPaymentClaimingDocument> availableClaimingDocs, UniversalUser currentUser) {
        ElectronicPaymentClaimingDocument chosenDocHelper = null;
        int count = 0;
        while (count < availableClaimingDocs.size() && chosenDocHelper == null) {
            ElectronicPaymentClaimingDocument claimingDoc = availableClaimingDocs.get(count);
            if (claimingDoc.getDocumentCode().equals(chosenDoc)) {
                chosenDocHelper = claimingDoc;
            }
            count += 1;
        }
        if (chosenDocHelper == null || !chosenDocHelper.userMayUseToClaim(currentUser)) {
            throw new AuthorizationException(currentUser.getPersonUserIdentifier(), ElectronicFundTransferClaimActionHelper.ACTION_NAME, ddService.getDataDictionary().getBusinessObjectEntry(ElectronicPaymentClaim.class.getName()).getObjectLabel());
        }
        return chosenDocHelper;
    }
    
    /**
     * Administrative users can fill in a field that says that a given electronic payment claim has already been claimed by another document.  This method
     * traverses through the list of electronic payment claims, checks if it is pre-claimed, and saves it if it is pre-claimed
     * @param claims the list of electronic payment claims 
     * @return the list of electronic payment claims with all pre-claimed records removed
     */
    private List<ElectronicPaymentClaim> handlePreClaimedRecords(List<ElectronicPaymentClaim> claims) {
        List<ElectronicPaymentClaim> stillToClaim = new ArrayList<ElectronicPaymentClaim>();
        for (ElectronicPaymentClaim claim: claims) {
            if (StringUtils.isBlank(claim.getReferenceFinancialDocumentNumber())) {
                // not claimed, add to stillToClaim list
                stillToClaim.add(claim);
            } else {
                // save that record
                boService.save(claim);
            }
        }
        return stillToClaim;
    }

    /**
     * Sets the ddService attribute value.
     * @param ddService The ddService to set.
     */
    public void setDataDictonaryService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    /**
     * Sets the boService attribute value.
     * @param boService The boService to set.
     */
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }
    
}
