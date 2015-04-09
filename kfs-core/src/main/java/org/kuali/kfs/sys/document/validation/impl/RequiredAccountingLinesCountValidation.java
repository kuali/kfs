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
package org.kuali.kfs.sys.document.validation.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * GenericValidation to check if the required number of accounting lines in a given accounting line group has been met
 */
public class RequiredAccountingLinesCountValidation extends GenericValidation {
    private String accountingLineGroupName;
    private int minimumNumber;
    protected String accountingLineGroupPropertyName;
    protected String errorMessageName;
    private AccountingDocument accountingDocumentForValidation;
    
    private static final String ACCOUNTING_LINES_GROUP_PROPERTY_SUFFIX = "AccountingLines";

    /**
     * Checks that the number of accounting lines in the accounting line group (named by the accountingLineGroupPropertyName property)
     * is greater than the set minimum number of accounting lines.
     * <strong>This validation expects the document to be sent in as a property.</strong>
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        List accountingLineGroup = (List)ObjectUtils.getPropertyValue(accountingDocumentForValidation, accountingLineGroupPropertyName);
        if (accountingLineGroup.size() < minimumNumber) {
            GlobalVariables.getMessageMap().putError(accountingLineGroupPropertyName, errorMessageName, new String[] { discoverGroupTitle(accountingDocumentForValidation) });
            return false;
        }
        return true;
    }
    
    /**
     * Returns the title of the given accounting line group on the document
     * @return an accounting line group title
     */
    protected String discoverGroupTitle(AccountingDocument document) {
        String title = accountingLineGroupName;
        Method groupTitleMethod = discoverGroupTitleMethod(document);
        if (groupTitleMethod != null) {
            try {
                title = (String)groupTitleMethod.invoke(document, new Object[0]);
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            }
            catch (InvocationTargetException ite) {
                throw new RuntimeException(ite);
            }
        }
        return title;
    }
    
    /**
     * Looks up what should be the method on the AccountingDocument class that returns the group title
     * @return
     */
    protected Method discoverGroupTitleMethod(AccountingDocument document) {
        Method groupTitleMethod = null;
        try {
            String methodName = new StringBuilder().append("get").append(accountingLineGroupPropertyName.substring(0, 1).toUpperCase()).append(accountingLineGroupPropertyName.substring(1)).append("SectionTitle").toString();
            groupTitleMethod = document.getClass().getMethod(methodName, new Class[0]);
        }
        catch (SecurityException se) {
            throw new RuntimeException(se);
        }
        catch (NoSuchMethodException nsme) {
            throw new RuntimeException(nsme);
        }
        
        return groupTitleMethod;
    }
    
    /**
     * Gets the accountingLineGroupName attribute. 
     * @return Returns the accountingLineGroupName.
     */
    public String getAccountingLineGroupName() {
        return accountingLineGroupName;
    }

    /**
     * Sets the accountingLineGroupName attribute value.
     * @param accountingLineGroupName The accountingLineGroupName to set.
     */
    public void setAccountingLineGroupName(String accountingLineGroupName) {
        this.accountingLineGroupName = accountingLineGroupName;
        this.accountingLineGroupPropertyName = new StringBuilder().append(this.accountingLineGroupName).append(RequiredAccountingLinesCountValidation.ACCOUNTING_LINES_GROUP_PROPERTY_SUFFIX).toString();
        this.errorMessageName = new StringBuilder().append("error.document.").append(accountingLineGroupName).append("SectionNoAccountingLines").toString();
    }

    /**
     * Gets the minimumNumber attribute. 
     * @return Returns the minimumNumber.
     */
    public int getMinimumNumber() {
        return minimumNumber;
    }

    /**
     * Sets the minimumNumber attribute value.
     * @param minimumNumber The minimumNumber to set.
     */
    public void setMinimumNumber(int minimumNumber) {
        this.minimumNumber = minimumNumber;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
