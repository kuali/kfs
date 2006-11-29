/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/infrastructure/src/org/kuali/kfs/sys/fixture/AccountingLineFixture.java,v $
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
package org.kuali.test.fixtures;

import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.Constants.GL_CREDIT_CODE;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.document.TransactionalDocument;

public enum AccountingLineFixture {
    LINE( 2004, 1, "BL", "1031400", "AC", "ADV", "5000", "SSS", "KUL3", "Y", "IN", "ONE", "01", "1", "blah", GL_DEBIT_CODE, "2.50"),
    LINE1( 2004, null, "BA", "6044900", null, null, "1697", null, null, null, "IN", null, null, null, null, null, "1000.00"),
    LINE2( 2004, null, "BL", "1031400", null, "ADV", "5000", "SSS", "KUL3", null, "IN", "ONE", null, null, null, null, "1.10"),
    LINE3( 2004, null, "BA", "6044900", null, null, "4008", "POL", null, null, "IN", "TWO", null, null, null, null, "1.10"),
    LINE4( 2004, null, "UA", "1912201", null, null, "5033", null, null, null, "EX", null, null, "123", null, null, "1.10"),
    LINE5( 2004, null, "BL", "1031400", "AC", "ADV", "5000", "SSS", "KUL3", null, "AS", "ONE", null, null, null, GL_DEBIT_CODE, "1.10"),
    LINE6( 2004, null, "BL", "1031400", "PE", "ADV", "5000", "SSS", "KUL3", null, "IN", "ONE", null, "123", null, GL_DEBIT_CODE, "1.10"),
    LINE7( 2004, null, "BA", "6044900", null, null, "4061", null, null, null, "IN", null, null, null, null, null, "1.10"),

    LINE8( 2004, null, "UA", "1912201", "AC", null, "9980", null, "BOB", "F", "TI", null, "01", "2004", "blah", GL_DEBIT_CODE, "1.10"),
    LINE9( 2004, null, "UA", "1912201", "AC", null, "9980", null, "BOB", "F", "FB", null, "01", "2004", null, GL_DEBIT_CODE, "1.10"),
    LINE10( 2004, null, "UA", "1912201", "AC", null, "8160", null, "BOB", "F", "TI", null, "01", "2004", "blah", GL_DEBIT_CODE, "1.10"),
    LINE11( 2004, null, "UA", "1912201", "AC", null, "9980", null, "BOB", "F", "TI", null, null, null, null, GL_DEBIT_CODE, "1.10"),
    LINE12( 2004, null, "UA", "1912201", "AC", null, "9897", null, "BOB", "F", "TI", null, null, null, null, GL_DEBIT_CODE, "1.10"),
    LINE13( 2004, null, "UA", "1912201", "AC", null, "9899", null, "BOB", "F", "TI", null, null, null, null, GL_DEBIT_CODE, "1.10"),
    LINE14( 2004, null, "UA", "1912201", null, null, "5033", null, null, null, "EX", null, null, "123", null, GL_DEBIT_CODE, "1.10"),
    LINE15( 2004, null, "UA", "1912201", null, null, "5033", null, null, null, "EX", null, null, "123", null, GL_CREDIT_CODE, "1.10"),
    LINE16( 2004, null, "UA", "1912201", "AC", null, "1175", null, "BOB", "F", "IC", null, "01", "2004", null, GL_DEBIT_CODE, "1.10"),
    LINE17( 2004, null, "UA", "1912201", "AC", null, "7600", null, "BOB", "F", "AM", null, "01", "2004", null, GL_DEBIT_CODE, "1.10"),
    LINE18( 2004, null, "BL", "1031400", null, null, "5000", null, null, null, null, null, null, null, null, null, "1.10"),

    GEC_LINE1( 2004, null, "BL", "1031400", null, "ADV", "5000", "SSS", "KUL3", null, "EX", null, "01", "123", null, null, "1.10"),
    ICA_LINE( 2004, null, "BL", "5431400", null, null, "5500", null, null, null, null, null, null, null, null, null, "1.10"),
    EXPENSE_GEC_LINE( 2004, null, "BA", "6044900", "AC", null, "1940", null, "BOB", "F", null, null, "01", "123", null, null, "1.10"),

    DOCUMENT_SERVICE_TEST_LINE( null, null, "BL", "1031400", null, "ADV", "5000", "SSS", "KUL3", "Y", "AS", null, null, null, null, GL_DEBIT_CODE, "2.50"),
    PFIP_SUB_FUND_LINE( 2004, null, "BA", "9544900", "AC", null, "9900", null, null, null, null, null, null, "2004", null, null, "1000.00"),
    SOURCE_LINE( 2004, null, "UA", "1912201", "AC", null, "9980", null, "BOB", "F", "AS", null, "01", "2004", "blah", GL_DEBIT_CODE, "1000.00"),
    EXPENSE_LINE( 2004, null, "UA", "1912201", "AC", "BEER", "9900", null, "BOB", "F", "EX", null, "01", "2004", "blah", GL_DEBIT_CODE, "1000.00"),
    EXPENSE_LINE2( 2004, null, "BL", "1031400", "AC", "BLDG", "9900", null, "BOB", "F", "EX", null, null, null, null, GL_DEBIT_CODE, "1000.00"),
    EXTERNAL_ENCUMBRANCE_LINE( 2004, null, "BL", "1031400", "EX", "BLDG", "9900", null, "BOB", "F", "EX", null, "01", "2004", "PE", GL_DEBIT_CODE, "1000.00"),

    FLEXIBLE_EXPENSE_LINE( 2004, null, "BL", "2231401", "AC", null, "9900", null, "BOB", "F", "EX", null, "01", "1", "blah", GL_DEBIT_CODE, "1000.00"),
    CASH_LINE( 2004, null, "BA", "6044900", null, null, "8000", null, "BOB", null, "ES", null, null, null, null, null, "1000.00"),
    LOSSS_ON_RETIRE_LINE( 2004, null, "BA", "6044900", null, null, "5137", "CF", "BOB", null, "ES", null, null, null, null, null, "1000.00"),
    ACCRUED_INCOME_LINE( 2004, null, "BA", "6044900", null, null, "8111", null, "BOB", null, "ES", null, "01", "2004", null, null, "1000.00"),
    ACCRUED_SICK_PAY_LINE( 2004, null, "UA", "1912201", null, null, "2998", null, "BOB", null, "ES", null, null, "01", "2004", null, "1000.00"),
    FUND_BALANCE_LINE( 2004, null, "BA", "6044900", null, null, "9899", null, "BOB", null, null, null, null, "01", "2004", GL_DEBIT_CODE, "1000.00"),
    LINE2_TOF( 2004, null, "BL", "1031400", null, null, "1697", null, null, null, "TI", null, null, null, null, null, "1.10"),
    ;

    public final String accountNumber;
    public final String balanceTypeCode;
    public final String chartOfAccountsCode;
    public final String debitCreditCode;
    public final String encumbranceUpdateCode;
    public final String financialObjectCode;
    public final String financialSubObjectCode;
    public final String objectTypeCode;
    public final String organizationReferenceId;
    public final String projectCode;
    public final String referenceOriginCode;
    public final String referenceNumber;
    public final String referenceTypeCode;
    public final String subAccountNumber;
    public final KualiDecimal amount;
    public final Integer postingYear;
    public final Integer sequenceNumber;


    AccountingLineFixture( Integer postingYear, Integer sequenceNumber, String chartOfAccountsCode, String accountNumber,
                          String balanceTypeCode, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String projectCode, String encumbranceUpdateCode,
                          String objectTypeCode, String organizationReferenceId, String referenceOriginCode, String referenceNumber, String referenceTypeCode, String debitCreditCode, String amount)
    {

        this.postingYear = postingYear;
        this.sequenceNumber = sequenceNumber;
        this.accountNumber = accountNumber;
        this.balanceTypeCode = balanceTypeCode;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.debitCreditCode = debitCreditCode;
        this.encumbranceUpdateCode = encumbranceUpdateCode;
        this.financialObjectCode = financialObjectCode;
        this.financialSubObjectCode = financialSubObjectCode;
        this.objectTypeCode = objectTypeCode;
        this.organizationReferenceId = organizationReferenceId;
        this.projectCode = projectCode;
        this.referenceOriginCode = referenceOriginCode;
        this.referenceNumber = referenceNumber;
        this.referenceTypeCode = referenceTypeCode;
        this.subAccountNumber = subAccountNumber;
        this.amount = new KualiDecimal(amount);
    }

    private <T extends AccountingLine> T createAccountingLine(Class<T> lineClass)
        throws InstantiationException, IllegalAccessException
    {
        return createAccountingLine(lineClass, null, this.postingYear, this.sequenceNumber);
    }

    public <T extends AccountingLine> T createAccountingLine(Class<T> lineClass, String debitCreditCode)
        throws InstantiationException, IllegalAccessException
    {
        T line = createAccountingLine(lineClass, null, this.postingYear, this.sequenceNumber);
        line.setDebitCreditCode(debitCreditCode);
        return line;
    }

    public <T extends AccountingLine> T createAccountingLine(Class<T> lineClass, String documentNumber, Integer postingYear, Integer sequenceNumber)
        throws InstantiationException, IllegalAccessException
    {
        T line = createLine(lineClass);

        line.setDocumentNumber(documentNumber);
        line.setPostingYear(postingYear);
        line.setSequenceNumber(sequenceNumber);

        line.refresh();
        return line;
    }

    private <T extends AccountingLine> T createLine(Class<T> lineClass)
        throws InstantiationException, IllegalAccessException
    {
        T line = (T) lineClass.newInstance();
        line.setAccountNumber(this.accountNumber);
        line.setAmount(this.amount);
        line.setBalanceTypeCode(this.balanceTypeCode);
        line.setChartOfAccountsCode(this.chartOfAccountsCode);
        line.setDebitCreditCode(this.debitCreditCode);
        line.setEncumbranceUpdateCode(this.encumbranceUpdateCode);
        line.setFinancialObjectCode(this.financialObjectCode);
        line.setFinancialSubObjectCode(this.financialSubObjectCode);
        line.setObjectTypeCode(this.objectTypeCode);
        line.setOrganizationReferenceId(this.organizationReferenceId);
        line.setProjectCode(this.projectCode);
        line.setReferenceOriginCode(this.referenceOriginCode);
        line.setReferenceNumber(this.referenceNumber);
        line.setReferenceTypeCode(this.referenceTypeCode);
        line.setSubAccountNumber(this.subAccountNumber);

        return line;
    }

    public SourceAccountingLine createSourceAccountingLine()
        throws InstantiationException, IllegalAccessException
    {
        return createAccountingLine(SourceAccountingLine.class);
    }

    public TargetAccountingLine createTargetAccountingLine()
        throws InstantiationException, IllegalAccessException
    {
        return createAccountingLine(TargetAccountingLine.class);
    }

    public void addAsSourceTo(TransactionalDocument document)
        throws IllegalAccessException, InstantiationException
    {
        document.addSourceAccountingLine(createAccountingLine(SourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber()));
    }
    
    public void addAsTargetTo(TransactionalDocument document)
        throws IllegalAccessException, InstantiationException
    {
        document.addTargetAccountingLine(createAccountingLine(TargetAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextTargetLineNumber()));
    }
}