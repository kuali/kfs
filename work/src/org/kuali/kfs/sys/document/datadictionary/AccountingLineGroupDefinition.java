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
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.rice.kns.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;

/**
 * Data dictionary definition that includes metadata for an accounting document about one of its groups of accounting lines (typically source vs. target, but this should open things up).
 */
public class AccountingLineGroupDefinition extends DataDictionaryDefinitionBase {
    private String groupLabel;
    private Class<? extends AccountingLine> accountingLineClass;
    private AccountingLineViewDefinition accountingLineView;
    private String importedLinePropertyPrefix;
    private List<? extends TotalDefinition> totals;
    private Class<? extends AccountingLineAuthorizer> accountingLineAuthorizerClass;
    private int forceColumnCount = -1;
    private String errorKey;
    
    private AccountingLineAuthorizer accountingLineAuthorizer;

    /**
     * Validates that:
     * 1) this accounting line group has an accounting line class
     * 2) this accounting line group has an accounting line view 
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (accountingLineClass == null) {
            throw new AttributeValidationException("Please specify an accounting line class for AccountingLineGroup "+getId());
        }
        if (accountingLineView == null) {
            throw new AttributeValidationException("Please specify an accountingLineView for AccountingLineGroup "+getId());
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
     * Gets the errorKey attribute. 
     * @return Returns the errorKey.
     */
    public String getErrorKey() {
        return errorKey;
    }

    /**
     * Sets the errorKey attribute value.
     * @param errorKey The errorKey to set.
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
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
