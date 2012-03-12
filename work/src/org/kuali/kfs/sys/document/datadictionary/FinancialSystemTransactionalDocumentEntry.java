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
package org.kuali.kfs.sys.document.datadictionary;

import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;

/**
 * An extension of the Rice TransactionalDocumentEntry that allows for KFS-centric properties, such as
 * Accounting Document validations 
 */
public class FinancialSystemTransactionalDocumentEntry extends TransactionalDocumentEntry {
    private Map<Class, String> validationMap;
    private Map<String, AccountingLineGroupDefinition> accountingLineGroups;
    private Class<? extends AccountingLineParser> importedLineParserClass;
    private Integer maxDictionaryValidationDepth;
    protected boolean allowsErrorCorrection = false;
    protected boolean potentiallySensitive = false;

    /**
     * Gets the validationMap attribute. 
     * @return Returns the validationMap.
     */
    public Map<Class, String> getValidationMap() {
        return validationMap;
    }

    /**
     * Sets the validationMap attribute value.
     * @param validationMap The validationMap to set.
     */
    public void setValidationMap(Map<Class, String> validationMap) {
        this.validationMap = validationMap;
    }

    /**
     * Gets the accountingLineGroups attribute. 
     * @return Returns the accountingLineGroups.
     */
    public Map<String, AccountingLineGroupDefinition> getAccountingLineGroups() {
        return accountingLineGroups;
    }

    /**
     * Sets the accountingLineGroups attribute value.
     * @param accountingLineGroups The accountingLineGroups to set.
     */
    public void setAccountingLineGroups(Map<String, AccountingLineGroupDefinition> accountingLineGroups) {
        this.accountingLineGroups = accountingLineGroups;
    }
    
    /**
     * Gets the importedLineParserClass attribute. 
     * @return Returns the importedLineParserClass.
     */
    public Class<? extends AccountingLineParser> getImportedLineParserClass() {
        return importedLineParserClass;
    }

    /**
     * Sets the importedLineParserClass attribute value.
     * @param importedLineParserClass The importedLineParserClass to set.
     */
    public void setImportedLineParserClass(Class<? extends AccountingLineParser> importedLineParser) {
        this.importedLineParserClass = importedLineParser;
    }

    /**
     * Gets the maxDictionaryValidationDepth attribute. 
     * @return Returns the maxDictionaryValidationDepth.
     */
    public Integer getMaxDictionaryValidationDepth() {
        return maxDictionaryValidationDepth;
    }

    /**
     * Sets the maxDictionaryValidationDepth attribute value.
     * @param maxDictionaryValidationDepth The maxDictionaryValidationDepth to set.
     */
    public void setMaxDictionaryValidationDepth(Integer maxDictionaryValidationDepth) {
        this.maxDictionaryValidationDepth = maxDictionaryValidationDepth;
    }
    
    /**
        This field contains a value of true or false.
        If true, then error correction is allowed for the document.
     */
    public void setAllowsErrorCorrection(boolean allowsErrorCorrection) {
        this.allowsErrorCorrection = allowsErrorCorrection;
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry#getAllowsErrorCorrection()
     */
    public boolean getAllowsErrorCorrection() {
        return allowsErrorCorrection;
    }

    /**
     * Gets the potentiallySensitive attribute. 
     * @return Returns the potentiallySensitive.
     */
    public boolean isPotentiallySensitive() {
        return potentiallySensitive;
    }

    /**
     * Sets the potentiallySensitive attribute value.
     * @param potentiallySensitive The potentiallySensitive to set.
     */
    public void setPotentiallySensitive(boolean potentiallySensitive) {
        this.potentiallySensitive = potentiallySensitive;
    }
    
    
}
