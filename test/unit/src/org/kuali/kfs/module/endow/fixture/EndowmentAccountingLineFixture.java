/*
 * Copyright 2010 The Kuali Foundation.
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
