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

import org.kuali.kfs.module.bc.fixture.ImportRequestFilelLineFixture;
import org.kuali.kfs.sys.context.KualiTestBase;

public class ImportRequestFileParsingHelperIsLineCorrectlyFormattedTest extends KualiTestBase {
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=comma, textDelimiter=comma
     * The test passes when a correctly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_correctlyFormmatedAnnualLine_commaFieldSeparatorQuoteTextDelimiter() {
        assertTrue(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be correctly formatted",
                    ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                            ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                            ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                            true));
    }
   
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=comma, textDelimiter=comma
     * The test passes when a incorrectly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_incorrectlyFormmatedAnnualLine_commaFieldSeparatorQuoteTextDelimiter() {
        assertFalse(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be incorrectly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                        true));
    }
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=comma, textDelimiter=nothing
     * The test passes when a correctly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_correctlyFormmatedAnnualLine_commaFieldSeparatorNoTextDelimiter() {
        assertTrue(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be correctly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                        ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                        true));
    }
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=comma, textDelimiter=nothing
     * The test passes when a incorrectly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_incorrectlyFormmatedAnnualLine_commaFieldSeparatorNoTextDelimiter() {
        assertFalse(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be incorrectly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                        true));
    }
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=TAB, textDelimiter=nothing
     * The test passes when a correctly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_correctlyFormmatedAnnualLine_tabFieldSeparatorNoTextDelimiter() {
        assertTrue(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be correctly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                        ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                        true));
    }
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = annual, fieldSeparator=comma, textDelimiter=nothing
     * The test passes when a incorrectly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_incorrectlyFormmatedAnnualLine_tabFieldSeparatorNoTextDelimiter() {
        assertFalse(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be incorrectly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                        true));
    }
    
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = monthly, fieldSeparator=comma, textDelimiter=comma
     * The test passes when a correctly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_correctlyFormmatedMonthlyLine_commaFieldSeparatorQuoteTextDelimiter() {
        assertTrue(" line = " + ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse() + " This line is expected to be correctly formatted",
                    ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse(), 
                            ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getFieldSeparator(), 
                            ImportRequestFilelLineFixture.CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getTextFieldDelimiter(),
                            false));
    }
   
    /**
     * Tests if a line is correctly formatted. Line parameters: file type = monthly, fieldSeparator=comma, textDelimiter=comma
     * The test passes when a incorrectly formatted line is used
     * 
     */
    public void testIsLineCorrectlyFormatted_incorrectlyFormmatedMonthlyLine_commaFieldSeparatorQuoteTextDelimiter() {
        assertFalse(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse() + " This line is expected to be incorrectly formatted",
                ImportRequestFileParsingHelper.isLineCorrectlyFormatted(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getLineToParse(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getFieldSeparator(), 
                        ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY.getTextFieldDelimiter(),
                        false));
    }
}
