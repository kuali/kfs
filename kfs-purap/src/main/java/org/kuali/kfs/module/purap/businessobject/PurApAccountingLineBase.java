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
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purap Accounting Line Base Business Object.
 */
public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine, Comparable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApAccountingLineBase.class);
    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;
    private String postingPeriodCode;  //stored in DB only for PREQ and CM Account History
    private KualiDecimal alternateAmountForGLEntryCreation; // not stored in DB; needed for disencumbrances and such
    public Integer purApSequenceNumber;

    private PurApItem purapItem;

    /**
     * Default constructor
     */
    public PurApAccountingLineBase() {
        super();
        this.setSequenceNumber(0);
        setAmount(null);
    }

    @Override
    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    @Override
    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    @Override
    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    @Override
    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    @Override
    public BigDecimal getAccountLinePercent() {
    	/* UAF-4250
    	 * 1. KFS3, this method return accountLinePercent without set scale, not sure why kfs6 changed ?
    	 * 2. accountLinePercent is private, so can't override this method in PREQAccount
    	 * 3. This is a base calss, if extend this class to override this method will require to change all child classes extend this class
    	 */
        if (accountLinePercent != null) {
        	if (this instanceof PaymentRequestAccount) {
                accountLinePercent = accountLinePercent.setScale(4, BigDecimal.ROUND_HALF_UP);
        	} else {
                accountLinePercent = accountLinePercent.setScale(2, BigDecimal.ROUND_HALF_UP);
        	}
            return accountLinePercent;
        }
        else {
            return BigDecimal.ZERO.setScale(2, 2);
        }
    }

    @Override
    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || StringUtils.isNotEmpty(getChartOfAccountsCode()) || StringUtils.isNotEmpty(getFinancialObjectCode()) || StringUtils.isNotEmpty(getFinancialSubObjectCode()) || StringUtils.isNotEmpty(getOrganizationReferenceId()) || StringUtils.isNotEmpty(getProjectCode()) || StringUtils.isNotEmpty(getSubAccountNumber()) || ObjectUtils.isNotNull(getAccountLinePercent()));
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#createBlankAmountsCopy()
     */
    @Override
    public PurApAccountingLine createBlankAmountsCopy() {
        PurApAccountingLine newAccount = (PurApAccountingLine) ObjectUtils.deepCopy(this);
     //   newAccount.setAccountLinePercent(BigDecimal.ZERO);
     //   newAccount.setAmount(KualiDecimal.ZERO);
        newAccount.setSequenceNumber(0);
        newAccount.setAccountLinePercent(null);
        newAccount.setAmount(null);

        return newAccount;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#accountStringsAreEqual(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public boolean accountStringsAreEqual(SourceAccountingLine accountingLine) {
        if (accountingLine == null) {
            return false;
        }
        return new EqualsBuilder().append(getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode()).append(getAccountNumber(), accountingLine.getAccountNumber()).append(getSubAccountNumber(), accountingLine.getSubAccountNumber()).append(getFinancialObjectCode(), accountingLine.getFinancialObjectCode()).append(getFinancialSubObjectCode(), accountingLine.getFinancialSubObjectCode()).append(getProjectCode(), accountingLine.getProjectCode()).append(getOrganizationReferenceId(), accountingLine.getOrganizationReferenceId())
                .isEquals();
    }

    @Override
    public boolean accountStringsAreEqual(PurApAccountingLine accountingLine) {
        return accountStringsAreEqual((SourceAccountingLine) accountingLine);

    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#generateSourceAccountingLine()
     */
    @Override
    public SourceAccountingLine generateSourceAccountingLine() {
        // the fields here should probably match method 'accountStringsAreEqual' above
        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode(getChartOfAccountsCode());
        sourceLine.setAccountNumber(getAccountNumber());
        sourceLine.setSubAccountNumber(getSubAccountNumber());
        sourceLine.setFinancialObjectCode(getFinancialObjectCode());
        sourceLine.setFinancialSubObjectCode(getFinancialSubObjectCode());
        sourceLine.setProjectCode(getProjectCode());
        sourceLine.setOrganizationReferenceId(getOrganizationReferenceId());
        sourceLine.setAmount(getAmount());
        sourceLine.setSequenceNumber(getSequenceNumber());
        return sourceLine;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }

    @Override
    public int compareTo(Object arg0) {
        if (arg0 instanceof PurApAccountingLine) {
            PurApAccountingLine account = (PurApAccountingLine) arg0;
            return this.getString().compareTo(account.getString());
        }
        return -1;
    }

    @Override
    public String getString() {
        return getChartOfAccountsCode() + "~" + getAccountNumber() + "~" + getSubAccountNumber() + "~" + getFinancialObjectCode() + "~" + getFinancialSubObjectCode() + "~" + getProjectCode() + "~" + getOrganizationReferenceId();
    }

    @Override
    public KualiDecimal getAlternateAmountForGLEntryCreation() {
        return alternateAmountForGLEntryCreation;
    }

    @Override
    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmount) {
        this.alternateAmountForGLEntryCreation = alternateAmount;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getSequenceNumber()
     */
    @Override
    public Integer getSequenceNumber() {
        return this.getAccountIdentifier();
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#copyFrom(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public void copyFrom(AccountingLine other) {
        super.copyFrom(other);

        if (other instanceof PurApAccountingLine) {
            PurApAccountingLine purapOther = (PurApAccountingLine)other;

            // Need to fix accountIdentifier and sequenceNumber since they are crossed in the getter in purap
            // i.e. getSequenceNumber() actually returns accountIdentifier, while getPurApSequenceNumber() returns the original sequenceNumber.
            // Without this fix, this.sequenceNumber will be set as other.AccountIdentifier, while this.accountIdentifier will remain unpopulated or it was;
            // this is not what we want; and if this method were used during comparison, such as being called by isLike(), then error will occur.
            setAccountIdentifier(purapOther.getAccountIdentifier());
            setSequenceNumber(purapOther.getPurApSequenceNumber());

            setAccountLinePercent(purapOther.getAccountLinePercent());
            setAmount(purapOther.getAmount());
            setAlternateAmountForGLEntryCreation(purapOther.getAlternateAmountForGLEntryCreation());
        }
    }

    @Override
    public void refreshNonUpdateableReferences() {
        //hold onto item reference if there without itemId
        PurApItem item = null;
        PurApItem tempItem = getPurapItem();
        if(tempItem != null &&
           tempItem.getItemIdentifier() != null) {
            item = tempItem;
        }
        super.refreshNonUpdateableReferences();
        if(ObjectUtils.isNotNull(item)) {
            this.setPurapItem(item);
        }
    }

    @Override
    public <T extends PurApItem> T getPurapItem() {
        return (T) purapItem;
    }

    /**
     * Sets the requisitionItem attribute.
     * @deprecated
     * @param item
     */
    @Override
    public void setPurapItem(PurApItem item) {
        purapItem = item;
    }

    @Override
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    @Override
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * Overridden to use purap doc identifier, rather than document number
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map valuesMap = super.getValuesMap();
        // remove document number
        valuesMap.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        return valuesMap;
    }

    /**
     * Gets the purApSequenceNumber attribute.
     *
     * @return Returns the purApSequenceNumber
     */
    @Override
    public Integer getPurApSequenceNumber() {
        return super.getSequenceNumber();
    }
}
