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

import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceDetailAccountObjectCode
 */
public enum InvoiceDetailAccountObjectCodeFixture {


    DETAIL_ACC_OBJ_CD1("6320", new Long(80072), "9000000", "IN", "2008", "SAL", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO),
    DETAIL_ACC_OBJ_CD2("6320", new Long(80072), "9000001", "IN", "2408", "EMPB", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO),
    DETAIL_ACC_OBJ_CD3("6320", new Long(80072), "6044901", "BA", "2408", "EMPB", KualiDecimal.ZERO, KualiDecimal.ZERO, KualiDecimal.ZERO),
    DETAIL_ACC_OBJ_CD4("6320", new Long(80072), "2336320", "BL", "2008", "SAL", new KualiDecimal(5.0), KualiDecimal.ZERO, KualiDecimal.ZERO),
    DETAIL_ACC_OBJ_CD5("6320", new Long(80072), "1292016", "IN", "2408", "EMPB", new KualiDecimal(7.0), KualiDecimal.ZERO, KualiDecimal.ZERO);

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
