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
package org.kuali.kfs.sys.document.datadictionary;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;

/**
 * Data dictionary definition that includes metadata for an accounting document about one of its groups of accounting lines (typically source vs. target, but this should open things up).
 */
public class AccountingLineGroupDefinition extends DataDictionaryDefinitionBase {
    private String groupLabel;
    private Class<? extends AccountingLine> accountingLineClass;
    private AccountingLineViewDefinition accountingLineView;
    private Class<? extends AccountingLineParser> importedLineParser;
    private String importedLinePropertyPrefix;
    private List<? extends TotalDefinition> totals;
    private Class<? extends AccountingLineAuthorizer> accountingLineAuthorizerClass;
    private int forceColumnCount = -1;
    private int tabIndexPasses = 2;
    
    private AccountingLineAuthorizer accountingLineAuthorizer;

    /**
     * Validates that:
     * 1) this accounting line group has an accounting line class
     * 2) this accounting line group has an accounting line view 
     * 3) if importedLineParser is specified, then importedLinePropertyPrefix exists
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (accountingLineClass == null) {
            throw new AttributeValidationException("Please specify an accounting line class for AccountingLineGroup "+getId());
        }
        if (accountingLineView == null) {
            throw new AttributeValidationException("Please specify an accountingLineView for AccountingLineGroup "+getId());
        }
        if (importedLineParser != null && StringUtils.isBlank(importedLinePropertyPrefix)) {
            throw new AttributeValidationException("As an imported line parser class has been specified, please also specify an imported line property prefix");
        }
        if (tabIndexPasses < 1) {
            throw new AttributeValidationException("Please specify a positive number of tab index passes");
        }
    }

    /**
     * Gets the accountingLineClass attribute. 
     * @return Returns the accountingLineClass.
     */
    public Class<? extends AccountingLine> getAccountingLineClass() {
        return accountingLineClass;
    }

    /**
     * Sets the accountingLineClass attribute value.
     * @param accountingLineClass The accountingLineClass to set.
     */
    public void setAccountingLineClass(Class<? extends AccountingLine> accountingLineClass) {
        this.accountingLineClass = accountingLineClass;
    }

    /**
     * Gets the accountingLineView attribute. 
     * @return Returns the accountingLineView.
     */
    public AccountingLineViewDefinition getAccountingLineView() {
        return accountingLineView;
    }

    /**
     * Sets the accountingLineView attribute value.
     * @param accountingLineView The accountingLineView to set.
     */
    public void setAccountingLineView(AccountingLineViewDefinition accountingLineView) {
        this.accountingLineView = accountingLineView;
    }

    /**
     * Gets the groupLabel attribute. 
     * @return Returns the groupLabel.
     */
    public String getGroupLabel() {
        return (groupLabel == null) ? "" : groupLabel;
    }

    /**
     * Sets the groupLabel attribute value.
     * @param groupLabel The groupLabel to set.
     */
    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    /**
     * Gets the importedLineParser attribute. 
     * @return Returns the importedLineParser.
     */
    public Class<? extends AccountingLineParser> getImportedLineParser() {
        return importedLineParser;
    }

    /**
     * Sets the importedLineParser attribute value.
     * @param importedLineParser The importedLineParser to set.
     */
    public void setImportedLineParser(Class<? extends AccountingLineParser> importedLineParser) {
        this.importedLineParser = importedLineParser;
    }

    /**
     * Gets the totals attribute. 
     * @return Returns the totals.
     */
    public List<? extends TotalDefinition> getTotals() {
        return totals;
    }

    /**
     * Sets the totals attribute value.
     * @param totals The totals to set.
     */
    public void setTotals(List<? extends TotalDefinition> totals) {
        this.totals = totals;
    }

    /**
     * Gets the accountingLineAuthorizerClass attribute. 
     * @return Returns the accountingLineAuthorizerClass.
     */
    public Class<? extends AccountingLineAuthorizer> getAccountingLineAuthorizerClass() {
        return accountingLineAuthorizerClass;
    }

    /**
     * Sets the accountingLineAuthorizerClass attribute value.
     * @param accountingLineAuthorizerClass The accountingLineAuthorizerClass to set.
     */
    public void setAccountingLineAuthorizerClass(Class<? extends AccountingLineAuthorizer> accountingLineAuthorizerClass) {
        this.accountingLineAuthorizerClass = accountingLineAuthorizerClass;
    }
    
    /**
     * Gets the importedLinePropertyPrefix attribute. 
     * @return Returns the importedLinePropertyPrefix.
     */
    public String getImportedLinePropertyPrefix() {
        return importedLinePropertyPrefix;
    }

    /**
     * Sets the importedLinePropertyPrefix attribute value.
     * @param importedLinePropertyPrefix The importedLinePropertyPrefix to set.
     */
    public void setImportedLinePropertyPrefix(String importedLinePropertyPrefix) {
        this.importedLinePropertyPrefix = importedLinePropertyPrefix;
    }

    /**
     * Gets the forceColumnCount attribute. 
     * @return Returns the forceColumnCount.
     */
    public int getForceColumnCount() {
        return forceColumnCount;
    }

    /**
     * Sets the forceColumnCount attribute value.
     * @param forceColumnCount The forceColumnCount to set.
     */
    public void setForceColumnCount(int forceColumnCount) {
        this.forceColumnCount = forceColumnCount;
    }

    /**
     * Gets the tabIndexPasses attribute. 
     * @return Returns the tabIndexPasses.
     */
    public int getTabIndexPasses() {
        return tabIndexPasses;
    }

    /**
     * Sets the tabIndexPasses attribute value.
     * @param tabIndexPasses The tabIndexPasses to set.
     */
    public void setTabIndexPasses(int tabIndexPasses) {
        this.tabIndexPasses = tabIndexPasses;
    }

    /**
     * Returns an instance of the accounting line authorizer for this group
     * @return an instance of the accounting line authorizer
     */
    public AccountingLineAuthorizer getAccountingLineAuthorizer() {
        if (accountingLineAuthorizer == null) {
            accountingLineAuthorizer = createAccountingLineAuthorizer();
        }
        return accountingLineAuthorizer;
    }
    
    /**
     * Creates an instance of the accounting line authorizer
     * @return the accounting line authorizer for this group
     */
    protected AccountingLineAuthorizer createAccountingLineAuthorizer() {
        Class<? extends AccountingLineAuthorizer> authorizerClass = getAccountingLineAuthorizerClass();
        AccountingLineAuthorizer authorizer = null;
        try {
            authorizer = authorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new IllegalArgumentException("InstantiationException while attempting to instantiate AccountingLineAuthorization class", ie);
        }
        catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("IllegalAccessException while attempting to instantiate AccountingLineAuthorization class", iae);
        }
        return authorizer;
    }
}
