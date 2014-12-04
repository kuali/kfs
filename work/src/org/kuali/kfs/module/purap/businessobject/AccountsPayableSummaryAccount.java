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

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Payment Request Summary Account Business Object.
 */
public class AccountsPayableSummaryAccount extends PaymentRequestAccount {

    private Integer paymentRequestIdentifier;
    private Integer creditMemoIdentifier;
    private Timestamp updateTimestamp;
    
    public AccountsPayableSummaryAccount() {
    }

    public AccountsPayableSummaryAccount(SourceAccountingLine account, Integer purapDocumentIdentifier, String docType) {
        if (PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(docType)) {
            this.setPaymentRequestIdentifier(purapDocumentIdentifier);
        }
        else if (PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(docType)) {
            this.setCreditMemoIdentifier(purapDocumentIdentifier);
        }
        this.setChartOfAccountsCode(account.getChartOfAccountsCode());
        this.setAccountNumber(account.getAccountNumber());
        this.setSubAccountNumber(account.getSubAccountNumber());
        this.setFinancialObjectCode(account.getFinancialObjectCode());
        this.setFinancialSubObjectCode(account.getFinancialSubObjectCode());
        this.setProjectCode(account.getProjectCode());
        this.setOrganizationReferenceId(account.getOrganizationReferenceId());
        this.setAmount(account.getAmount());
        this.setSequenceNumber(account.getSequenceNumber());
        this.setUpdateTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
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

    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    public Integer getCreditMemoIdentifier() {
        return creditMemoIdentifier;
    }

    public void setCreditMemoIdentifier(Integer creditMemoIdentifier) {
        this.creditMemoIdentifier = creditMemoIdentifier;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    
}
