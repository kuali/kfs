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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class AssetGlpeSourceDetail extends PersistableBusinessObjectBase implements GeneralLedgerPendingEntrySourceDetail {
    private String accountNumber;
    private KualiDecimal amount;
    private String balanceTypeCode;
    private String chartOfAccountsCode;
    private String documentNumber;
    private String financialDocumentLineDescription;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String organizationReferenceId;
    private Integer postingYear;
    private String projectCode;
    private String referenceNumber;
    private String referenceOriginCode;
    private String referenceTypeCode;
    private String subAccountNumber;
    private boolean source;
    private boolean expense;
    private boolean capitalization;
    private boolean accumulatedDepreciation;
    private boolean capitalizationOffset;
    private boolean payment;
    private boolean paymentOffset;
    private Account account;
    private ObjectCode objectCode;
    private int sequenceNumber;
    private String postingPeriodCode;


    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public KualiDecimal getAmount() {
        return amount;
    }


    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }


    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }


    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }


    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public String getDocumentNumber() {
        return documentNumber;
    }


    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }


    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }


    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    public ObjectCode getObjectCode() {
        return objectCode;
    }


    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }


    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }


    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }


    public Integer getPostingYear() {
        return postingYear;
    }


    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }


    public String getProjectCode() {
        return projectCode;
    }


    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }


    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }


    public void setReferenceOriginCode(String referenceOriginCode) {
        this.referenceOriginCode = referenceOriginCode;
    }


    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }


    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }


    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public boolean isSource() {
        return source;
    }


    public void setSource(boolean source) {
        this.source = source;
    }


    public boolean isExpense() {
        return expense;
    }


    public void setExpense(boolean expense) {
        this.expense = expense;
    }


    public boolean isCapitalization() {
        return capitalization;
    }


    public void setCapitalization(boolean capitalization) {
        this.capitalization = capitalization;
    }


    public boolean isAccumulatedDepreciation() {
        return accumulatedDepreciation;
    }


    public void setAccumulatedDepreciation(boolean accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }


    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("accountNumber", this.accountNumber);
        map.put("amount", this.chartOfAccountsCode);
        return map;
    }


    public void setCapitalizationOffset(boolean b) {
        this.capitalizationOffset = b;

    }


    public boolean isCapitalizationOffset() {
        return capitalizationOffset;
    }


    /**
     * Gets the payment attribute.
     * 
     * @return Returns the payment.
     */
    public boolean isPayment() {
        return payment;
    }


    /**
     * Sets the payment attribute value.
     * 
     * @param payment The payment to set.
     */
    public void setPayment(boolean payment) {
        this.payment = payment;
    }


    /**
     * Gets the paymentOffset attribute.
     * 
     * @return Returns the paymentOffset.
     */
    public boolean isPaymentOffset() {
        return paymentOffset;
    }


    /**
     * Sets the paymentOffset attribute value.
     * 
     * @param paymentOffset The paymentOffset to set.
     */
    public void setPaymentOffset(boolean paymentOffset) {
        this.paymentOffset = paymentOffset;
    }


    /**
     * We have to return from this method directly since this is not a real persistent class and if we call super, it will run into
     * "Class not found in OJB repository" exception.
     * 
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refresh()
     */
    @Override
    public void refresh() {
        return;
    }


    /**
     * We have to return from this method directly since this is not a real persistent class and if we call super, it will run into
     * "Class not found in OJB repository" exception.
     * 
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refreshNonUpdateableReferences()
     */
    @Override
    public void refreshNonUpdateableReferences() {
        return;
    }


    /**
     * We have to return from this method directly since this is not a real persistent class and if we call super, it will run into
     * "Class not found in OJB repository" exception.
     * 
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refreshReferenceObject(java.lang.String)
     */
    @Override
    public void refreshReferenceObject(String referenceObjectName) {
        return;
    }


    /**
     * Gets the sequenceNumber attribute.
     * 
     * @return Returns the sequenceNumber.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }


    /**
     * Sets the sequenceNumber attribute value.
     * 
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    /**
     * Gets the postingPeriodCode attribute.
     * 
     * @return Returns the postingPeriodCode.
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    /**
     * Sets the postingPeriodCode attribute value.
     * 
     * @param postingPeriodCode The postingPeriodCode to set.
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }


}
