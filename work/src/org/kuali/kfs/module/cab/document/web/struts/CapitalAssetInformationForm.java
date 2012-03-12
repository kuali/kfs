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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.KRADConstants;

public class CapitalAssetInformationForm extends KualiForm {
    private GeneralLedgerEntry generalLedgerEntry;
    private Long primaryGlAccountId;
    private List<CapitalAssetInformation> capitalAssetInformation;
    private boolean selectAllGlEntries;
    private String currDocNumber;
    protected Long generalLedgerAccountIdentifier;

    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (StringUtils.equals(methodToCallParameterName, KRADConstants.DISPATCH_REQUEST_PARAMETER) && (StringUtils.equals(methodToCallParameterValue, CabConstants.Actions.PROCESS) || StringUtils.equals(methodToCallParameterValue, CabConstants.Actions.VIEW_DOC))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
    }

    public CapitalAssetInformationForm() {
        this.generalLedgerEntry = new GeneralLedgerEntry();
    }

    /**
     * Gets the generalLedgerEntry attribute.
     * 
     * @return Returns the generalLedgerEntry.
     */
    public GeneralLedgerEntry getGeneralLedgerEntry() {
        return generalLedgerEntry;
    }

    /**
     * Sets the relatedGlEntries attribute value.
     * 
     * @param relatedGlEntries The generalLedgerEntry to set.
     */
    public void setGeneralLedgerEntry(GeneralLedgerEntry generalLedgerEntry) {
        this.generalLedgerEntry = generalLedgerEntry;
    }


    /**
     * Gets the primaryGlAccountId attribute.
     * 
     * @return Returns the primaryGlAccountId.
     */
    public Long getPrimaryGlAccountId() {
        return primaryGlAccountId;
    }

    /**
     * Sets the primaryGlAccountId attribute value.
     * 
     * @param primaryGlAccountId The primaryGlAccountId to set.
     */
    public void setPrimaryGlAccountId(Long primaryGlAccountId) {
        this.primaryGlAccountId = primaryGlAccountId;
    }

    /**
     * Gets the capitalAssetInformation attribute.
     * 
     * @return Returns the capitalAssetInformation.
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * 
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * Initialize index for struts
     * 
     * @param index current
     * @return value
    public GeneralLedgerEntry getRelatedGlEntry(int index) {
        int size = getRelatedGlEntries().size();
        while (size <= index || getRelatedGlEntries().get(index) == null) {
            getRelatedGlEntries().add(size++, new GeneralLedgerEntry());
        }
        return (GeneralLedgerEntry) getRelatedGlEntries().get(index);
    }
    */
    
    /**
     * Gets the selectAllGlEntries attribute.
     * 
     * @return Returns the selectAllGlEntries.
     */
    public boolean isSelectAllGlEntries() {
        return selectAllGlEntries;
    }

    /**
     * Sets the selectAllGlEntries attribute value.
     * 
     * @param selectAllGlEntries The selectAllGlEntries to set.
     */
    public void setSelectAllGlEntries(boolean selectAllGlEntries) {
        this.selectAllGlEntries = selectAllGlEntries;
    }


    /**
     * Gets the currDocNumber attribute.
     * 
     * @return Returns the currDocNumber.
     */
    public String getCurrDocNumber() {
        return currDocNumber;
    }

    /**
     * Sets the currDocNumber attribute value.
     * 
     * @param currDocNumber The currDocNumber to set.
     */
    public void setCurrDocNumber(String currDocNumber) {
        this.currDocNumber = currDocNumber;
    }

    /**
     * Gets the generalLedgerAccountIdentifier attribute.
     * 
     * @return Returns the generalLedgerAccountIdentifier
     */
    
    public Long getGeneralLedgerAccountIdentifier() {
        return generalLedgerAccountIdentifier;
    }

    /** 
     * Sets the generalLedgerAccountIdentifier attribute.
     * 
     * @param generalLedgerAccountIdentifier The generalLedgerAccountIdentifier to set.
     */
    public void setGeneralLedgerAccountIdentifier(Long generalLedgerAccountIdentifier) {
        this.generalLedgerAccountIdentifier = generalLedgerAccountIdentifier;
    }
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.selectAllGlEntries = false;
        this.currDocNumber = null;
    }

    @Override
    public boolean getIsNewForm() {
        // TODO hack for now
        // Avoid this exception after first submit
        /*
         * java.lang.RuntimeException: Cannot verify that the methodToCall should be methodToCall.submitAssetGlobal.x
         * org.kuali.rice.kns.util.WebUtils.parseMethodToCall(WebUtils.java:112)
         */
        return true;
    }
}
