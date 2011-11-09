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
package org.kuali.kfs.module.bc.fixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.RequestImportFieldSeparator;
import org.kuali.kfs.module.bc.BCConstants.RequestImportTextFieldDelimiter;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * Test data for ImportRequestFileParsingHelper
 * Lines are formatted for annual file
 * 
 */
public enum ImportRequestFilelLineFixture {
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL("\"BA\",\"6044900\",\"\",\"1464\",\"\",245000", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.QUOTE.getDelimiter()),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_ANNUAL("\"BA\",\"6044900\",\"\",\"1464\",\"\",\"245000\"", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.QUOTE.getDelimiter()),
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA,6044900,,1468,,550000", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.NOTHING.getDelimiter()),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA,6044900,,,1468,,550000", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.NOTHING.getDelimiter()),
    
    CORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA\u00096044900\u0009\u00091464\u0009\u0009245000", 
            RequestImportFieldSeparator.TAB.getSeparator(), 
            RequestImportTextFieldDelimiter.NOTHING.getDelimiter()),
    INCORRECTLY_FORMATTED_LINE_TAB_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA\u00096044900\u0009\u00091464\u0009\u0009\u0009245000", 
            RequestImportFieldSeparator.TAB.getSeparator(), 
            RequestImportTextFieldDelimiter.NOTHING.getDelimiter()),
    
    CORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY("\"BA\",\"6044900\",\"\",\"2000\",\"\",0,0,0,0,0,0,0,0,0,0,0,0", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.QUOTE.getDelimiter()),
    INCORRECTLY_FORMATTED_LINE_COMMA_FIELD_SEPARATOR_QUOTE_TEXT_DELIMITER_MONTHLY("\"BA\",\"6044900\",\"\",\"2000\",\"\",\"0\",0,0,0,0,0,0,0,0,0,0,0", 
            RequestImportFieldSeparator.COMMA.getSeparator(), 
            RequestImportTextFieldDelimiter.QUOTE.getDelimiter()),
    
    CORRECTLY_FORMATTED_LINE_PERIOD_FIELD_SEPARATOR_NO_TEXT_DELIMITER_ANNUAL("BA.6044900..1468..550000", 
            ".", 
            RequestImportTextFieldDelimiter.NOTHING.getDelimiter());   

    private String lineToParse;
    private String fieldSeparator;
    private String textFieldDelimiter;
    
    private ImportRequestFilelLineFixture(String line, String fieldSeparator, String textFieldDelimiter) {
        this.lineToParse = line;
        this.fieldSeparator = fieldSeparator;
        this.textFieldDelimiter = textFieldDelimiter;
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
    
    public BudgetConstructionRequestMove createExpectedBudgetConstructionMoveObject(boolean isAnnual, String chartCode, String account, String subAccount, String objectCode, String subObjectCode, String... requestAmounts) {
        BudgetConstructionRequestMove request = new BudgetConstructionRequestMove();
        List<String> amountList = new ArrayList<String>();
        amountList.addAll(Arrays.asList(requestAmounts));
        
        request.setChartOfAccountsCode(chartCode);
        request.setAccountNumber(account);
        request.setSubAccountNumber(subAccount);
        request.setFinancialObjectCode(objectCode);
        request.setFinancialSubObjectCode(subObjectCode);
        
        if (isAnnual) request.setAccountLineAnnualBalanceAmount(new KualiInteger(Integer.parseInt(amountList.get(0))));
        else {
            request.setFinancialDocumentMonth1LineAmount(new KualiInteger(Integer.parseInt(amountList.get(0))));
            request.setFinancialDocumentMonth2LineAmount(new KualiInteger(Integer.parseInt(amountList.get(1))));
            request.setFinancialDocumentMonth3LineAmount(new KualiInteger(Integer.parseInt(amountList.get(2))));
            request.setFinancialDocumentMonth4LineAmount(new KualiInteger(Integer.parseInt(amountList.get(3))));
            request.setFinancialDocumentMonth5LineAmount(new KualiInteger(Integer.parseInt(amountList.get(4))));
            request.setFinancialDocumentMonth6LineAmount(new KualiInteger(Integer.parseInt(amountList.get(5))));
            request.setFinancialDocumentMonth7LineAmount(new KualiInteger(Integer.parseInt(amountList.get(6))));
            request.setFinancialDocumentMonth8LineAmount(new KualiInteger(Integer.parseInt(amountList.get(7))));
            request.setFinancialDocumentMonth9LineAmount(new KualiInteger(Integer.parseInt(amountList.get(8))));
            request.setFinancialDocumentMonth10LineAmount(new KualiInteger(Integer.parseInt(amountList.get(9))));
            request.setFinancialDocumentMonth11LineAmount(new KualiInteger(Integer.parseInt(amountList.get(10))));
            request.setFinancialDocumentMonth12LineAmount(new KualiInteger(Integer.parseInt(amountList.get(11))));
            
        }
        return request;
    }
    

}
