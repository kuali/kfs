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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceAccountDetail
 */
public enum InvoiceAccountDetailFixture {

    INV_ACCT_DTL1("5030", new Long(80072), "IN", "9000000", "", new KualiDecimal(300), KualiDecimal.ZERO, KualiDecimal.ZERO, new KualiDecimal(300), KualiDecimal.ZERO),
    INV_ACCT_DTL2("5030", new Long(80072), "IN", "9000001", "", new KualiDecimal(120), KualiDecimal.ZERO, KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO),
    INV_ACCT_DTL3("5030", new Long(80072), "BL", "2336320", "0142900", new KualiDecimal(120), new KualiDecimal(1), KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO),
    INV_ACCT_DTL4("5030", new Long(80072), "IN", "1292016", "2336320", new KualiDecimal(120), new KualiDecimal(1), KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO),
    INV_ACCT_DTL5("5030", new Long(80072), "IN", "9000000", "", new KualiDecimal(300), KualiDecimal.ZERO, new KualiDecimal(300), new KualiDecimal(300), KualiDecimal.ZERO);

    private String documentNumber;
    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String contractControlAccountNumber;
    private KualiDecimal budgetAmount = KualiDecimal.ZERO;
    private KualiDecimal expenditureAmount = KualiDecimal.ZERO;
    private KualiDecimal cumulativeAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceAmount = KualiDecimal.ZERO;
    private KualiDecimal billedAmount = KualiDecimal.ZERO;

    private InvoiceAccountDetailFixture(String documentNumber, Long proposalNumber, String chartOfAccountsCode, String accountNumber, String contractControlAccountNumber, KualiDecimal budgetAmount, KualiDecimal expenditureAmount, KualiDecimal cumulativeAmount, KualiDecimal balanceAmount, KualiDecimal billedAmount) {
        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.contractControlAccountNumber = contractControlAccountNumber;
        this.budgetAmount = budgetAmount;
        this.expenditureAmount = expenditureAmount;
        this.cumulativeAmount = cumulativeAmount;
        this.balanceAmount = balanceAmount;
        this.billedAmount = billedAmount;
    }

    public InvoiceAccountDetail createInvoiceAccountDetail() {
        InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
        invoiceAccountDetail.setDocumentNumber(this.documentNumber);
        invoiceAccountDetail.setProposalNumber(this.proposalNumber);
        invoiceAccountDetail.setChartOfAccountsCode(this.chartOfAccountsCode);
        invoiceAccountDetail.setAccountNumber(this.accountNumber);
        invoiceAccountDetail.setContractControlAccountNumber(this.contractControlAccountNumber);
        invoiceAccountDetail.setTotalBudget(this.budgetAmount);
        invoiceAccountDetail.setInvoiceAmount(this.expenditureAmount);
        invoiceAccountDetail.setCumulativeExpenditures(this.cumulativeAmount);
        invoiceAccountDetail.setTotalPreviouslyBilled(this.billedAmount);

        return invoiceAccountDetail;
    }

}
