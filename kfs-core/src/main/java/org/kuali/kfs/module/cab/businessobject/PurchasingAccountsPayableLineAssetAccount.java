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
package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableLineAssetAccount extends PersistableBusinessObjectBase {
    private static final Logger LOG = Logger.getLogger(PurchasingAccountsPayableLineAssetAccount.class);

    private String documentNumber;
    private Integer accountsPayableLineItemIdentifier;
    private Integer capitalAssetBuilderLineNumber;
    private Long generalLedgerAccountIdentifier;
    private KualiDecimal itemAccountTotalAmount;
    private String activityStatusCode;

    // non-persistent field
    private boolean active;

    // References
    private GeneralLedgerEntry generalLedgerEntry;
    private PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset;

    public PurchasingAccountsPayableLineAssetAccount() {

    }

    public PurchasingAccountsPayableLineAssetAccount(PurchasingAccountsPayableItemAsset itemAsset, Long generalLedgerAccountIdentifier) {
        this.documentNumber = itemAsset.getDocumentNumber();
        this.accountsPayableLineItemIdentifier = itemAsset.getAccountsPayableLineItemIdentifier();
        this.capitalAssetBuilderLineNumber = itemAsset.getCapitalAssetBuilderLineNumber();
        this.generalLedgerAccountIdentifier = generalLedgerAccountIdentifier;
        this.purchasingAccountsPayableItemAsset = itemAsset;
        this.setActivityStatusCode(StringUtils.isBlank(itemAsset.getActivityStatusCode()) ? CabConstants.ActivityStatusCode.MODIFIED : itemAsset.getActivityStatusCode());
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getAccountsPayableLineItemIdentifier() {
        return accountsPayableLineItemIdentifier;
    }

    public void setAccountsPayableLineItemIdentifier(Integer accountsPayableLineItemIdentifier) {
        this.accountsPayableLineItemIdentifier = accountsPayableLineItemIdentifier;
    }

    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
    }

    public Long getGeneralLedgerAccountIdentifier() {
        return generalLedgerAccountIdentifier;
    }

    public void setGeneralLedgerAccountIdentifier(Long generalLedgerAccountIdentifier) {
        this.generalLedgerAccountIdentifier = generalLedgerAccountIdentifier;
    }

    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }

    public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }

    public GeneralLedgerEntry getGeneralLedgerEntry() {
        return generalLedgerEntry;
    }

    public void setGeneralLedgerEntry(GeneralLedgerEntry generalLedgerEntry) {
        this.generalLedgerEntry = generalLedgerEntry;
    }

    public boolean isActive() {
        return CabConstants.ActivityStatusCode.NEW.equalsIgnoreCase(this.getActivityStatusCode()) || CabConstants.ActivityStatusCode.MODIFIED.equalsIgnoreCase(this.getActivityStatusCode());
    }


    /**
     * Gets the activityStatusCode attribute.
     * 
     * @return Returns the activityStatusCode.
     */
    public String getActivityStatusCode() {
        return activityStatusCode;
    }

    /**
     * Sets the activityStatusCode attribute value.
     * 
     * @param activityStatusCode The activityStatusCode to set.
     */
    public void setActivityStatusCode(String activityStatusCode) {
        this.activityStatusCode = activityStatusCode;
    }

    public PurchasingAccountsPayableItemAsset getPurchasingAccountsPayableItemAsset() {
        return purchasingAccountsPayableItemAsset;
    }

    public void setPurchasingAccountsPayableItemAsset(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset) {
        this.purchasingAccountsPayableItemAsset = purchasingAccountsPayableItemAsset;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("accountsPayableLineItemIdentifier", this.accountsPayableLineItemIdentifier);
        m.put("capitalAssetBuilderLineNumber", this.capitalAssetBuilderLineNumber);
        m.put("generalLedgerAccountIdentifier", this.generalLedgerAccountIdentifier);
        return m;
    }
}
