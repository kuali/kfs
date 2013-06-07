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

import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceDetailAccountObjectCode
 */
public enum InvoiceDetailAccountObjectCodeFixture {


    DETAIL_ACC_OBJ_CD1("6320", new Long(80072), "9000000", "IN", "2008", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO), DETAIL_ACC_OBJ_CD2("6320", new Long(80072), "9000000", "IN", "2408", "EMPB", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO);

    private String documentNumber;
    private Long proposalNumber;
    private String accountNumber;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private String categoryCode;
    private KualiDecimal currentExpenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulativeExpenditures = KualiDecimal.ZERO;
    private KualiDecimal totalBilled = KualiDecimal.ZERO;

    private InvoiceDetailAccountObjectCodeFixture(String documentNumber, Long proposalNumber, String accountNumber, String chartOfAccountsCode, String financialObjectCode, String categoryCode, KualiDecimal currentExpenditures, KualiDecimal cumulativeExpenditures, KualiDecimal totalBilled) {
        this.documentNumber = documentNumber;
        this.proposalNumber = proposalNumber;
        this.accountNumber = accountNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.financialObjectCode = financialObjectCode;
        this.categoryCode = categoryCode;
        this.currentExpenditures = currentExpenditures;
        this.cumulativeExpenditures = cumulativeExpenditures;
        this.totalBilled = totalBilled;
    }

    public InvoiceDetailAccountObjectCode createInvoiceDetailAccountObjectCode() {
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
        invoiceDetailAccountObjectCode.setDocumentNumber(this.documentNumber);
        invoiceDetailAccountObjectCode.setProposalNumber(this.proposalNumber);
        invoiceDetailAccountObjectCode.setAccountNumber(this.accountNumber);
        invoiceDetailAccountObjectCode.setChartOfAccountsCode(this.chartOfAccountsCode);
        invoiceDetailAccountObjectCode.setFinancialObjectCode(this.financialObjectCode);
        invoiceDetailAccountObjectCode.setCategoryCode(this.categoryCode);
        invoiceDetailAccountObjectCode.setCurrentExpenditures(this.currentExpenditures);
        invoiceDetailAccountObjectCode.setCumulativeExpenditures(this.cumulativeExpenditures);
        invoiceDetailAccountObjectCode.setTotalBilled(this.totalBilled);

        return invoiceDetailAccountObjectCode;
    }
}
