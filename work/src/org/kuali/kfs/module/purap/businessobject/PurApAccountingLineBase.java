/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapPropertyConstants;

/**
 * Purap Accounting Line Base Business Object.
 */
public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine, Comparable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApAccountingLineBase.class);
    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;
    private KualiDecimal alternateAmountForGLEntryCreation; // not stored in DB; needed for disencumbrances and such
    
    private PurApItem purapItem;

    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#isEmpty()
     */
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || StringUtils.isNotEmpty(getChartOfAccountsCode()) || StringUtils.isNotEmpty(getFinancialObjectCode()) || StringUtils.isNotEmpty(getFinancialSubObjectCode()) || StringUtils.isNotEmpty(getOrganizationReferenceId()) || StringUtils.isNotEmpty(getProjectCode()) || StringUtils.isNotEmpty(getSubAccountNumber()) || ObjectUtils.isNotNull(getAccountLinePercent()));
    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#createBlankAmountsCopy()
     */
    public PurApAccountingLine createBlankAmountsCopy() {
        PurApAccountingLine newAccount = (PurApAccountingLine) ObjectUtils.deepCopy(this);
        newAccount.setAccountLinePercent(BigDecimal.ZERO);
        newAccount.setAmount(KualiDecimal.ZERO);
        return newAccount;
    }


    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#accountStringsAreEqual(org.kuali.kfs.bo.SourceAccountingLine)
     */
    public boolean accountStringsAreEqual(SourceAccountingLine accountingLine) {
        if (accountingLine == null) {
            return false;
        }
        return new EqualsBuilder().append(getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode()).append(getAccountNumber(), accountingLine.getAccountNumber()).append(getSubAccountNumber(), accountingLine.getSubAccountNumber()).append(getFinancialObjectCode(), accountingLine.getFinancialObjectCode()).append(getFinancialSubObjectCode(), accountingLine.getFinancialSubObjectCode()).append(getProjectCode(), accountingLine.getProjectCode()).append(getOrganizationReferenceId(), accountingLine.getOrganizationReferenceId())
                .isEquals();
    }

    public boolean accountStringsAreEqual(PurApAccountingLine accountingLine) {
        return accountStringsAreEqual((SourceAccountingLine) accountingLine);

    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#generateSourceAccountingLine()
     */
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
        return sourceLine;
    }

    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
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

    public int compareTo(Object arg0) {
        if (arg0 instanceof PurApAccountingLine) {
            PurApAccountingLine account = (PurApAccountingLine) arg0;
            return this.getString().compareTo(account.getString());
        }
        return -1;
    }

    public String getString() {
        return getChartOfAccountsCode() + "~" + getAccountNumber() + "~" + getSubAccountNumber() + "~" + getFinancialObjectCode() + "~" + getFinancialSubObjectCode() + "~" + getProjectCode() + "~" + getOrganizationReferenceId();
    }

    public KualiDecimal getAlternateAmountForGLEntryCreation() {
        return alternateAmountForGLEntryCreation;
    }

    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmount) {
        this.alternateAmountForGLEntryCreation = alternateAmount;
    }

    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#getSequenceNumber()
     */
    @Override
    public Integer getSequenceNumber() {
        return this.getAccountIdentifier();
    }
    
    protected void copyFrom(PurApAccountingLine other) {
        
        super.copyFrom(other);

        setAccountLinePercent(other.getAccountLinePercent());      
        setAlternateAmountForGLEntryCreation(other.getAlternateAmountForGLEntryCreation());            
 
    }
 
    @Override
    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        updateObjectAndSubObject();
        super.afterLookup(persistenceBroker);
    }

    protected void updateObjectAndSubObject() {
        //TODO: default to current if there is no purapitem.  This should only happen during creation
        Integer universityFiscalYear = null;
        if(ObjectUtils.isNotNull(this.getItemIdentifier())) {
            if(ObjectUtils.isNull(this.getPurApItem())) {
                this.refreshReferenceObject(PurapPropertyConstants.PURAP_ITEM);
                if(ObjectUtils.isNotNull(this.getPurApItem())) {
                    if(ObjectUtils.isNull(this.getPurApItem().getPurapDocument())) {
                        this.getPurApItem().refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
                    }
                }
            }
        }
        if(ObjectUtils.isNotNull(this.getPurApItem()) &&
                ObjectUtils.isNotNull(this.getPurApItem().getPurapDocument())) {
            universityFiscalYear = this.getPurApItem().getPurapDocument().getPostingYearNextOrCurrent();
            setObjectCode(SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, this.getChartOfAccountsCode(), this.getFinancialObjectCode()));
            setSubObjectCode(SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, this.getChartOfAccountsCode(), this.getAccountNumber(), this.getFinancialObjectCode(), this.getFinancialSubObjectCode()));
        }
        

    }
    
    @Override
    public void refreshReferenceObject(String referenceObjectName) {
        boolean skipSuper = false;
        //don't refresh object code since that is specially handled
        if(StringUtils.equals(referenceObjectName, KFSPropertyConstants.OBJECT_CODE) ||
           StringUtils.equals(referenceObjectName, KFSPropertyConstants.SUB_OBJECT_CODE)) {
            updateObjectAndSubObject();
            return;
        }
        if(!skipSuper){
            super.refreshReferenceObject(referenceObjectName);
        }
    }

    @Override
    public void refreshNonUpdateableReferences() {
        //hold onto item reference if there without itemId
        PurApItem item = null;
        if(ObjectUtils.isNotNull(this.getPurApItem()) &&
           ObjectUtils.isNull(this.getPurApItem().getItemIdentifier())) {
            item = this.getPurApItem();
        }
        super.refreshNonUpdateableReferences();
        if(ObjectUtils.isNotNull(item)) {
            this.setPurApItem(item);
        }
    }

    public <T extends PurApItem> T getPurApItem() {
        return (T) purapItem;
    }
    
    /**
     * Sets the requisitionItem attribute.
     * @deprecated
     * @param item
     */
    public void setPurApItem(PurApItem item) {
        purapItem = item;
    }

    @Override
    public ObjectCode getObjectCode() {
        updateObjectCode();
        return super.getObjectCode();
    }

    /**
     * This method...
     */
    protected void updateObjectCode() {
        if((ObjectUtils.isNull(super.getObjectCode()) &&
                ObjectUtils.isNotNull(this.getFinancialObjectCode())) ||
           (ObjectUtils.isNotNull(super.getObjectCode()) && 
                   !StringUtils.equals(this.getFinancialObjectCode(), 
                           super.getObjectCode().getFinancialObjectCode()))) {
            updateObjectAndSubObject();
        }
    }

    @Override
    public SubObjCd getSubObjectCode() { 
        updateSubObjectCode();
        return super.getSubObjectCode();
    }

    /**
     * This method...
     */
    protected void updateSubObjectCode() {
        if((ObjectUtils.isNull(super.getSubObjectCode()) &&
                ObjectUtils.isNotNull(this.getFinancialSubObjectCode())) ||
           (ObjectUtils.isNotNull(super.getSubObjectCode()) && 
                   !StringUtils.equals(this.getFinancialSubObjectCode(), 
                           super.getSubObjectCode().getFinancialSubObjectCode()))) {
            updateObjectAndSubObject();
        }
    }

    @Override
    public void setObjectCode(ObjectCode objectCode) {
        // TODO Auto-generated method stub
        super.setObjectCode(objectCode);
    }

    @Override
    public void setSubObjectCode(SubObjCd subObjectCode) {
        // TODO Auto-generated method stub
        super.setSubObjectCode(subObjectCode);
    }
}
