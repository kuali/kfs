/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceAccountDetail
 */
public enum InvoiceAccountDetailFixture {

    INV_ACCT_DTL1("5030", new Long(80072), new Integer(2011), "IN", "9000000", "", new KualiDecimal(300), KualiDecimal.ZERO, KualiDecimal.ZERO, new KualiDecimal(300), KualiDecimal.ZERO), INV_ACCT_DTL2("5030", new Long(80072), new Integer(2011), "IN", "9000001", "", new KualiDecimal(120), KualiDecimal.ZERO, KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO), INV_ACCT_DTL3("5030", new Long(80072), new Integer(2011), "BL", "2336320", "0142900", new KualiDecimal(120), new KualiDecimal(1), KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO), INV_ACCT_DTL4("5030", new Long(80072), new Integer(2011), "UA", "6812950", "1031400", new KualiDecimal(120), new KualiDecimal(1), KualiDecimal.ZERO, new KualiDecimal(120), KualiDecimal.ZERO);

    private String documentNumber;
    private Long proposalNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String contractControlAccountNumber;
    private KualiDecimal budgetAmount = KualiDecimal.ZERO;
    private KualiDecimal expenditureAmount = KualiDecimal.ZERO;
    private KualiDecimal cumulativeAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceAmount = KualiDecimal.ZERO;
    private KualiDecimal billedAmount = KualiDecimal.ZERO;

    private InvoiceAccountDetailFixture(String documentNumber, Long proposalNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String contractControlAccountNumber, KualiDecimal budgetAmount, KualiDecimal expenditureAmount, KualiDecimal cumulativeAmount, KualiDecimal balanceAmount, KualiDecimal billedAmount) {

        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.contractControlAccountNumber = contractControlAccountNumber;
        this.budgetAmount = budgetAmount;
        this.expenditureAmount = expenditureAmount;
        this.cumulativeAmount = cumulativeAmount;
        this.balanceAmount = balanceAmount;
        this.billedAmount = billedAmount;


    }

    @SuppressWarnings("deprecation")
    public InvoiceAccountDetail createInvoiceAccountDetail() {
        InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
        invoiceAccountDetail.setDocumentNumber(this.documentNumber);
        invoiceAccountDetail.setProposalNumber(this.proposalNumber);
        invoiceAccountDetail.setUniversityFiscalYear(this.universityFiscalYear);
        invoiceAccountDetail.setChartOfAccountsCode(this.chartOfAccountsCode);
        invoiceAccountDetail.setAccountNumber(this.accountNumber);
        invoiceAccountDetail.setContractControlAccountNumber(this.contractControlAccountNumber);
        invoiceAccountDetail.setBudgetAmount(this.budgetAmount);
        invoiceAccountDetail.setExpenditureAmount(this.expenditureAmount);
        invoiceAccountDetail.setCumulativeAmount(this.cumulativeAmount);
        invoiceAccountDetail.setBalanceAmount(this.balanceAmount);
        invoiceAccountDetail.setBilledAmount(this.billedAmount);


        return invoiceAccountDetail;

    }


}
