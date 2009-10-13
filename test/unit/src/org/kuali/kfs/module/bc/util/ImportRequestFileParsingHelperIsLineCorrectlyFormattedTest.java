/*
 * Copyright 2008 The Kuali Foundation
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
