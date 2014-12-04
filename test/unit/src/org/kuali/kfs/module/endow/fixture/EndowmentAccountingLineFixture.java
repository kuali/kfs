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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineBase;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum EndowmentAccountingLineFixture {

    ENDOWMENT_ACCOUNTING_LINE_EGLT_BASIC(new Integer(1), // accountingLineNumber
            new KualiDecimal("100"), // amount
            null,// organizationReferenceId
            "T", // financialDocumentLineTypeCode
            "BL", // chartOfAccountsCode
            "0142900", // accountNumber
            "5000", //financialObjectCode
            null, // subAccountNumber
            null, // financialSubObjectCode
            null// projectCode
    );

    public final Integer accountingLineNumber;
    public final KualiDecimal amount;
    public final String organizationReferenceId;
    public final String financialDocumentLineTypeCode;

    public final String chartOfAccountsCode;
    public final String accountNumber;
    public final String financialObjectCode;
    public final String subAccountNumber;
    public final String financialSubObjectCode;
    public final String projectCode;

    private EndowmentAccountingLineFixture(Integer accountingLineNumber, KualiDecimal amount, String organizationReferenceId, String financialDocumentLineTypeCode, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String subAccountNumber, String financialSubObjectCode, String projectCode) {

        this.accountingLineNumber = accountingLineNumber;
        this.amount = amount;
        this.organizationReferenceId = organizationReferenceId;
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.financialObjectCode = financialObjectCode;
        this.subAccountNumber = subAccountNumber;
        this.financialSubObjectCode = financialSubObjectCode;
        this.projectCode = projectCode;
    }

    /**
     * This method creates a Endowment Accounting Line record
     * 
     * @return endowmentAccountingLine
     */
    public EndowmentAccountingLineBase createEndowmentAccountingLine(boolean isSource) {
        EndowmentAccountingLineBase endowmentAccountingLine = null;

        if (isSource) {
            endowmentAccountingLine = (EndowmentAccountingLineBase) new SourceEndowmentAccountingLine();
        }
        else {
            endowmentAccountingLine = (EndowmentAccountingLineBase) new TargetEndowmentAccountingLine();
        }

        endowmentAccountingLine.setAccountingLineNumber(this.accountingLineNumber);
        endowmentAccountingLine.setAmount(this.amount);
        endowmentAccountingLine.setOrganizationReferenceId(this.organizationReferenceId);
        endowmentAccountingLine.setFinancialDocumentLineTypeCode(this.financialDocumentLineTypeCode);
        endowmentAccountingLine.setChartOfAccountsCode(this.chartOfAccountsCode);
        endowmentAccountingLine.setAccountNumber(this.accountNumber);
        endowmentAccountingLine.setFinancialObjectCode(this.financialObjectCode);
        endowmentAccountingLine.setSubAccountNumber(this.subAccountNumber);
        endowmentAccountingLine.setFinancialSubObjectCode(this.financialSubObjectCode);
        endowmentAccountingLine.setProjectCode(this.projectCode);

        endowmentAccountingLine.refreshNonUpdateableReferences();

        return endowmentAccountingLine;
    }
}
