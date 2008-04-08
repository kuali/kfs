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
package org.kuali.module.budget.fixture;

import org.kuali.module.budget.BCConstants.RequestImportFieldSeparator;
import org.kuali.module.budget.BCConstants.RequestImportTextFieldDelimiter;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;

/**
 * Test data for ImportRequestFileParsingHelper
 * Lines are formatted for annual file
 * 
 */
public enum ImportRequestFilelLineFixture {
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL("\"BA\",\"6044900\",\"\",\"1464\",\"\",245000", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.QUOTE.getDelimiter(), null),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL("\"BA\",\"6044900\",\"\",\"1464\",\"\",\"245000\"", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.QUOTE.getDelimiter(), null),
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA,6044900,,1468,,550000", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.NOTHING.getDelimiter(), null),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA,6044900,,,1468,,550000", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.NOTHING.getDelimiter(), null),
    
    CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA\u00096044900\u0009\u00091464\u0009\u0009245000", RequestImportFieldSeparator.TAB.getSeparator(), RequestImportTextFieldDelimiter.NOTHING.getDelimiter(), null),
    INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA\u00096044900\u0009\u00091464\u0009\u0009\u0009245000", RequestImportFieldSeparator.TAB.getSeparator(), RequestImportTextFieldDelimiter.NOTHING.getDelimiter(), null),
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY("\"BA\",\"6044900\",\"\",\"2000\",\"\",0,0,0,0,0,0,0,0,0,0,0,0", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.QUOTE.getDelimiter(), null),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY("\"BA\",\"6044900\",\"\",\"2000\",\"\",\"0\",0,0,0,0,0,0,0,0,0,0,0", RequestImportFieldSeparator.COMMA.getSeparator(), RequestImportTextFieldDelimiter.QUOTE.getDelimiter(), null);

    private String lineToParse;
    private String fieldSeparator;
    private String textFieldDelimiter;
    private BudgetConstructionRequestMove budgetConstructionRequestMove;
    
    private ImportRequestFilelLineFixture(String line, String fieldSeparator, String textFieldDelimiter, BudgetConstructionRequestMove budgetConstructionRequestMove) {
        this.lineToParse = line;
        this.fieldSeparator = fieldSeparator;
        this.textFieldDelimiter = textFieldDelimiter;
        this.budgetConstructionRequestMove = budgetConstructionRequestMove;
    }
    
    /**
     * gets field separator
     * 
     * @return
     */
    public String getFieldSeparator() {
        return this.fieldSeparator;
    }
    
    /**
     * gets line to parse
     * 
     * @return
     */
    public String getLineToParse() {
        return this.lineToParse;
    }
    
    /**
     * gets text field delimiter
     * 
     * @return
     */
    public String getTextFieldDelimiter() {
        return this.textFieldDelimiter;
    }

    /**
     * Gets the budget construction request move object that should be created from this line
     * 
     * @return
     */
    public BudgetConstructionRequestMove getBudgetConstructionRequestMove() {
        return this.budgetConstructionRequestMove;
    }
    
    
    

}
