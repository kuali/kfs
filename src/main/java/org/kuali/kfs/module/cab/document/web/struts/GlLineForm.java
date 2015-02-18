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
package org.kuali.kfs.module.cab.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.KRADConstants;

public class GlLineForm extends KualiForm {
    private GeneralLedgerEntry generalLedgerEntry;
    private Long primaryGlAccountId;
    private CapitalAssetInformation capitalAssetInformation;
    private boolean selectAllGlEntries;
    private String currDocNumber;
    private Integer capitalAssetLineNumber;
    
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

    public GlLineForm() {
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
     * Sets the generalLedgerEntry attribute value.
     * 
     * @param generalLedgerEntry The generalLedgerEntry to set.
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
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * 
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * Gets the capitalAssetLineNumber attribute. 
     * @return Returns the capitalAssetLineNumber.
     */
    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }

    /**
     * Sets the capitalAssetLineNumber attribute value.
     * @param capitalAssetLineNumber The capitalAssetLineNumber to set.
     */
    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
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
