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
package org.kuali.kfs.module.bc.util;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.kfs.module.bc.fixture.ImportRequestFilelLineFixture;
import org.kuali.kfs.sys.context.KualiTestBase;

public class ImportRequestFileParsingHelperparseLineTest extends KualiTestBase {

    public void testParseLine_incorrectlyFormattedAnnualLine_commaFieldSeparatorQuoteTextDelimiter() {

        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse(), ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(), true);
        BudgetConstructionRequestMove expected = null;

        assertNull(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be incorrectly formatted", actual);
    }

    public void testParseLine_correctlyFormattedAnnualLine_commaFieldSeparatorQuoteTextDelimiter() {

        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(), true);
        BudgetConstructionRequestMove expected = ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.createExpectedBudgetConstructionMoveObject(true, "BA", "6044900", "", "1464", "", "245000");

        assertNotNull(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be correctly formatted", actual);
        assertTrue("expected correctly formatted line", isEqual(expected, actual, true));
    }

    public void testParseLine_incorrectlyFormattedMonthlyLine_commaFieldSeparatorQuoteTextDelimiter() {

        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse(), ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getFieldSeparator(), ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getTextFieldDelimiter(), false);
        BudgetConstructionRequestMove expected = null;

        assertNull(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse() + " This line is expected to be incorrectly formatted", actual);
    }

    public void testParseLine_correctlyFormattedMonthlyLine_commaFieldSeparatorQuoteTextDelimiter() {

        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getFieldSeparator(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getTextFieldDelimiter(), false);
        BudgetConstructionRequestMove expected = ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.createExpectedBudgetConstructionMoveObject(false, "BA", "6044900", "", "2000", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0");

        assertNotNull(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse() + " This line is expected to be correctly formatted", actual);
        assertTrue("expected correctly formatted line", isEqual(expected, actual, false));
    }
    
    public void testParseLine_correctlyFormattedAnnualLine_periodFieldSeparatorNoTextDelimiter() {

        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(), true);
        BudgetConstructionRequestMove expected = ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.createExpectedBudgetConstructionMoveObject(true, "BA", "6044900", "", "1468", "", "550000");

        assertNotNull(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be correctly formatted", actual);
        assertTrue("expected correctly formatted line", isEqual(expected, actual, true));
    }
    
    private boolean isEqual(BudgetConstructionRequestMove expected, BudgetConstructionRequestMove actual, boolean isAnnual) {
        if (!actual.getChartOfAccountsCode().equals(expected.getChartOfAccountsCode()))
            return false;
        if (!actual.getAccountNumber().equals(expected.getAccountNumber()))
            return false;
        if (!actual.getSubAccountNumber().equals(expected.getSubAccountNumber()))
            return false;
        if (!actual.getFinancialObjectCode().equals(expected.getFinancialObjectCode()))
            return false;
        if (!actual.getFinancialSubObjectCode().equals(expected.getFinancialSubObjectCode()))
            return false;

        if (isAnnual) {
            if (!actual.getAccountLineAnnualBalanceAmount().equals(expected.getAccountLineAnnualBalanceAmount()))
                return false;
        }
        else {
            if (!actual.getFinancialDocumentMonth1LineAmount().equals(expected.getFinancialDocumentMonth1LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth2LineAmount().equals(expected.getFinancialDocumentMonth2LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth3LineAmount().equals(expected.getFinancialDocumentMonth3LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth4LineAmount().equals(expected.getFinancialDocumentMonth4LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth5LineAmount().equals(expected.getFinancialDocumentMonth5LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth6LineAmount().equals(expected.getFinancialDocumentMonth6LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth7LineAmount().equals(expected.getFinancialDocumentMonth7LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth8LineAmount().equals(expected.getFinancialDocumentMonth8LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth9LineAmount().equals(expected.getFinancialDocumentMonth9LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth10LineAmount().equals(expected.getFinancialDocumentMonth10LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth11LineAmount().equals(expected.getFinancialDocumentMonth11LineAmount()))
                return false;
            if (!actual.getFinancialDocumentMonth12LineAmount().equals(expected.getFinancialDocumentMonth12LineAmount()))
                return false;
        }
        return true;
    }
}
