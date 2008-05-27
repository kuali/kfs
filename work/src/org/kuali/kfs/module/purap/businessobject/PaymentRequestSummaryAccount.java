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

import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.purap.document.PaymentRequestDocument;

/**
 * Payment Request Summary Account Business Object.
 */
public class PaymentRequestSummaryAccount extends PaymentRequestAccount {

    private Integer purapDocumentIdentifier;
    private String postingPeriodCode;
    
    private PaymentRequestDocument paymentRequest;
    private AccountingPeriod financialDocumentPostingPeriod;
    
    /**
     * Default constructor.
     */
    public PaymentRequestSummaryAccount() {

    }

    public PaymentRequestSummaryAccount(SourceAccountingLine account, Integer purapDocumentIdentifier) {
        this.setPurapDocumentIdentifier(purapDocumentIdentifier);
        this.setChartOfAccountsCode(account.getChartOfAccountsCode());
        this.setAccountNumber(account.getAccountNumber());
        this.setSubAccountNumber(account.getSubAccountNumber());
        this.setFinancialObjectCode(account.getFinancialObjectCode());
        this.setFinancialSubObjectCode(account.getFinancialSubObjectCode());
        this.setProjectCode(account.getProjectCode());
        this.setOrganizationReferenceId(account.getOrganizationReferenceId());
        this.setAmount(account.getAmount());
    }

    public PaymentRequestDocument getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    /**
     * ItemIdentifier is not a valid field in this table because it is the summary of accounts for the document, not per item
     * 
     * @deprecated
     */
    @Override
    public Integer getItemIdentifier() {
        return super.getItemIdentifier();
    }

    /**
     * ItemIdentifier is not a valid field in this table because it is the summary of accounts for the document, not per item
     * 
     * @deprecated
     */
    @Override
    public void setItemIdentifier(Integer itemIdentifier) {
        super.setItemIdentifier(itemIdentifier);
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
     * Gets the postingPeriodCode attribute. 
     * @return Returns the postingPeriodCode.
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    /**
     * Sets the postingPeriodCode attribute value.
     * @param postingPeriodCode The postingPeriodCode to set.
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * Gets the financialDocumentPostingPeriod attribute. 
     * @return Returns the financialDocumentPostingPeriod.
     */
    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }

    /**
     * Sets the financialDocumentPostingPeriod attribute value.
     * @param financialDocumentPostingPeriod The financialDocumentPostingPeriod to set.
     * @deprecated
     */
    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }
    
}
