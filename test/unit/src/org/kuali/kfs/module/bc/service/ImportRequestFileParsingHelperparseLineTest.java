/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.util;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.fixture.ImportRequestFilelLineFixture;

public class ImportRequestFileParsingHelperparseLineTest extends KualiTestBase  {

    public void testParseLine_incorrectlyFormattedAnnualLine_commaFieldSeparatorQuoteTextDelimiter() {
        
        BudgetConstructionRequestMove actual = ImportRequestFileParsingHelper.parseLine(ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse(), 
                ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getFieldSeparator(), 
                ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getTextFieldDelimiter(),
                true);
        BudgetConstructionRequestMove expected = ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getBudgetConstructionRequestMove();
        
        assertNull(" line = " + ImportRequestFilelLineFixture.INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL.getLineToParse() + " This line is expected to be incorrectly formatted", actual);
        assertTrue("expected incorrectly formatted line", (actual == expected));
    }
    
    private boolean isEqual(BudgetConstructionRequestMove expected, BudgetConstructionRequestMove actual, boolean isAnnual) {
        if ( !actual.getChartOfAccountsCode().equals(expected.getChartOfAccountsCode()) ) return false;
        if ( !actual.getAccountNumber().equals(expected.getAccountNumber()) ) return false;
        if ( !actual.getSubAccountNumber().equals(expected.getSubAccountNumber()) ) return false;
        if ( !actual.getFinancialObjectCode().equals(expected.getFinancialObjectCode()) ) return false;
        if ( !actual.getFinancialSubObjectCode().equals(expected.getFinancialSubObjectCode()) ) return false;
        
        return true;
    }
}
