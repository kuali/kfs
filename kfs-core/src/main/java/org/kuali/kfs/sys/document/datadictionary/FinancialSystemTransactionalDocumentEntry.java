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

    protected boolean hasAppDocStatus = false;

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

    public boolean hasAppDocStatus() {
        return hasAppDocStatus;
    }

    public void setHasAppDocStatus(boolean hasAppDocStatus) {
        this.hasAppDocStatus = hasAppDocStatus;
    }

}
