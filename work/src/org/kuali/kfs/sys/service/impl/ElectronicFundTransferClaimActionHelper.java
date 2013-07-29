/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;
import org.kuali.kfs.sys.web.struts.ElectronicPaymentClaimClaimedHelper;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * An Electronic Funds Transfer action which claims the electronic payment claims in a form and redirects
 * to the claiming document.
 */
public class ElectronicFundTransferClaimActionHelper implements ElectronicFundTransferActionHelper {
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private DataDictionaryService ddService;
    private BusinessObjectService boService;
    private DocumentService documentService;
    
    protected static final String ACTION_NAME = "claim";
    protected static final String CHOSEN_DOCUMENT_PROPERTY = "chosenElectronicPaymentClaimingDocumentCode";
    protected static final String CLAIM_PROPERTY = "claims";
    protected static final String HAS_DOCUMENTATION_PROPERTY = "hasDocumentation";
    protected static final String BASIC_FORWARD = "basic";
    protected static final String PORTAL_FORWARD = "portal";

    /**
     * Claims the ElectronicPaymentClaim records with a document and then redirects to that docment.
     * @see org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper#performAction(org.kuali.rice.kns.web.struts.form.KualiForm, org.apache.struts.action.ActionMapping)
     */
    @Override
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map paramMap, String basePath) {
        // can the user claim electronic payments at all?
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        if (!form.hasAvailableClaimingDocumentStrategies()) {
            throw new AuthorizationException(currentUser.getPrincipalName(), ElectronicFundTransferClaimActionHelper.ACTION_NAME, ddService.getDataDictionary().getBusinessObjectEntry(ElectronicPaymentClaim.class.getName()).getTitleAttribute());
        }
        
        // did the user say they have documentation?  If not, give an error...
        boolean continueClaiming = true;
        continueClaiming = handleDocumentationForClaim(form.getHasDocumentation());
        
        // process admin's pre-claimed records
        List<ElectronicPaymentClaim> claims = form.getClaims();
        
        boolean isAuthorized = form.isAllowElectronicFundsTransferAdministration();
        if (isAuthorized) {
            claims = handlePreClaimedRecords(claims, generatePreClaimedByCheckboxSet(form.getClaimedByCheckboxHelpers()), form.getAvailableClaimingDocumentStrategies());
            if (GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() > 0) {
                // if there were any errors, we'll need to redirect to the page again
                return mapping.findForward(ElectronicFundTransferClaimActionHelper.BASIC_FORWARD);
            }
            else if (claims.size() == 0) {
                // no more claims to process...so don't make a document, just redirect to the portal
                return mapping.findForward(PORTAL_FORWARD);
            }
        }
        
        // put any remaining claims into a claiming doc
        String chosenDoc = form.getChosenElectronicPaymentClaimingDocumentCode();
        continueClaiming &= checkChosenDocumentType(chosenDoc);
        if (continueClaiming && KFSConstants.FinancialDocumentTypeCodes.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE.equalsIgnoreCase(chosenDoc)) {
            continueClaiming &= checkYEDIclaims(claims);
        }

        // get the requested document claiming helper
        if (continueClaiming) {
            ElectronicPaymentClaimingDocumentGenerationStrategy documentCreationHelper = getRequestedClaimingHelper(form.getChosenElectronicPaymentClaimingDocumentCode(), form.getAvailableClaimingDocumentStrategies(), currentUser);
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
    protected boolean checkChosenDocumentType(String chosenDoc) {
        boolean result = true;
        if (StringUtils.isBlank(chosenDoc)) {
            GlobalVariables.getMessageMap().putError(ElectronicFundTransferClaimActionHelper.CHOSEN_DOCUMENT_PROPERTY, KFSKeyConstants.ElectronicPaymentClaim.ERROR_EFT_NO_CHOSEN_CLAIMING_DOCTYPE, new String[]{});
            result = false;
        }
        return result;
    }

    /** Verifies that if Year End Distribution of Income and Expense (YEDI) is
     * the chosenElectronicPaymentClaimingDocumentCode, all claims selected are
     * not posted in the the current fiscal year.
     * @param claims the list of selected claims
     * @return true if the validation resulted in no errors, false if otherwise
     */
    protected boolean checkYEDIclaims(List<ElectronicPaymentClaim> claims) {
        boolean result = true;
        final Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        int count = 0;

        for (ElectronicPaymentClaim claim: claims) {
            if (currentFiscalYear.equals(claim.getFinancialDocumentPostingYear())) {
                GlobalVariables.getMessageMap().putError(
                        ElectronicFundTransferClaimActionHelper.CLAIM_PROPERTY+"["+count+"]",
                        KFSKeyConstants.ElectronicPaymentClaim.ERROR_EFT_CHOSEN_CLAIMING_DOCTYPE,
                        new String[]{});
                result = false;
                break;
            }
            count += 1;
        }
        return result;
    }

    /**
     * Using user entered form values, determines which of the available ElectronicPaymentClaimingDocumentGenerationStrategy implementations to use. 
     * @param chosenDoc the document type code for the doc that the user selected
     * @param availableClaimingDocs a List of ElectronicPaymentClaimingDocumentGenerationStrategy implementations that can be used by the given user
     * @param currentUser the currently logged in user
     * @throws AuthorizationException thrown if the user entered an invalid or unusable ElectronicPaymentClaimingDocumentGenerationStrategy code
     * @return an ElectronicPaymentClaimingDocumentGenerationStrategy helper to use to create the document
     */
    protected ElectronicPaymentClaimingDocumentGenerationStrategy getRequestedClaimingHelper(String chosenDoc, List<ElectronicPaymentClaimingDocumentGenerationStrategy> availableClaimingDocs, Person currentUser) {
        ElectronicPaymentClaimingDocumentGenerationStrategy chosenDocHelper = null;
        int count = 0;
        while (count < availableClaimingDocs.size() && chosenDocHelper == null) {
            ElectronicPaymentClaimingDocumentGenerationStrategy claimingDoc = availableClaimingDocs.get(count);
            if (StringUtils.equals(claimingDoc.getClaimingDocumentWorkflowDocumentType(), chosenDoc)) {
                chosenDocHelper = claimingDoc;
            }
            count += 1;
        }
        if (chosenDocHelper == null || !chosenDocHelper.userMayUseToClaim(currentUser)) {
            throw new AuthorizationException(currentUser.getPrincipalName(), ElectronicFundTransferClaimActionHelper.ACTION_NAME, ddService.getDataDictionary().getBusinessObjectEntry(ElectronicPaymentClaim.class.getName()).getObjectLabel());
        }
        return chosenDocHelper;
    }
    
    /**
     * Administrative users can fill in a field that says that a given electronic payment claim has already been claimed by another document.  This method
     * traverses through the list of electronic payment claims, checks if it is pre-claimed, and saves it if it is pre-claimed
     * @param claims the list of electronic payment claims 
     * @return the list of electronic payment claims with all pre-claimed records removed
     */
    protected List<ElectronicPaymentClaim> handlePreClaimedRecords(List<ElectronicPaymentClaim> claims, Set<String> preClaimedByCheckbox, List<ElectronicPaymentClaimingDocumentGenerationStrategy> documentGenerationStrategies) {
        List<ElectronicPaymentClaim> stillToClaim = new ArrayList<ElectronicPaymentClaim>();
        int count = 0;
        for (ElectronicPaymentClaim claim: claims) {
            if (StringUtils.isBlank(claim.getReferenceFinancialDocumentNumber()) && !preClaimedByCheckbox.contains(claim.getElectronicPaymentClaimRepresentation())) {
                // not claimed by any mechanism, add to stillToClaim list
                stillToClaim.add(claim);
            } else {
                boolean savePreClaimed = true;
                if (StringUtils.isNotBlank(claim.getReferenceFinancialDocumentNumber())) {
                    // check that the document exists
                    boolean isValidDocRef = false;
                    int stratCount = 0;
                    while (!isValidDocRef && stratCount < documentGenerationStrategies.size()) {
                        isValidDocRef |= documentGenerationStrategies.get(stratCount).isDocumentReferenceValid(claim.getReferenceFinancialDocumentNumber());
                        stratCount += 1;
                    }
                    if (!isValidDocRef) {
                        GlobalVariables.getMessageMap().putError(ElectronicFundTransferClaimActionHelper.CLAIM_PROPERTY+"["+count+"]", KFSKeyConstants.ElectronicPaymentClaim.ERROR_PRE_CLAIMING_DOCUMENT_DOES_NOT_EXIST, new String[] { claim.getReferenceFinancialDocumentNumber() });
                        savePreClaimed = false;
                    }
                }
                if (savePreClaimed) {
                    claim.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
                    // save that record
                    boService.save(claim);
                }
            }
            count += 1;
        }
        return stillToClaim;
    }
    
    /**
     * Uses the list of checked pre-claimed checkbox helpers to create a Set of representations of electronic payment claim records that were marked as "pre-claimed"
     * @param checkboxHelpers the list of checked ElectronicPaymentClaimClaimedHelpers from the form
     * @return a Set of electronic payment claim representations for records that have been reclaimed
     */
    protected Set<String> generatePreClaimedByCheckboxSet(List<ElectronicPaymentClaimClaimedHelper> checkboxHelpers) {
        Set<String> claimedByCheckboxRepresentations = new HashSet<String>();
        for (ElectronicPaymentClaimClaimedHelper helper: checkboxHelpers) {
            claimedByCheckboxRepresentations.add(helper.getElectronicPaymentClaimRepresentation());
        }
        return claimedByCheckboxRepresentations;
    }
    
    /**
     * Checks that the user was able to answer the "has documentation?" question correctly
     * @param hasDocumentation the user's response to the "has documentation" question
     * @return true if the user was able to successfully answer this question, false otherwise
     */
    protected boolean handleDocumentationForClaim(String hasDocumentation) {
        boolean success = true;
        if (StringUtils.isBlank(hasDocumentation) || !hasDocumentation.equalsIgnoreCase("yep")) {
            GlobalVariables.getMessageMap().putError(ElectronicFundTransferClaimActionHelper.HAS_DOCUMENTATION_PROPERTY, KFSKeyConstants.ElectronicPaymentClaim.ERROR_NO_DOCUMENTATION, new String[] {});
            success = false;
        }
        
        return success;
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

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
}

