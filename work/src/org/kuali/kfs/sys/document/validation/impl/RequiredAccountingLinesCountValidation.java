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
package org.kuali.kfs.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

/**
 * GenericValidation to check if the required number of accounting lines in a given accounting line group has been met
 */
public class RequiredAccountingLinesCountValidation extends GenericValidation {
    private String accountingLineGroupName;
    private int minimumNumber;
    private String accountingLineGroupPropertyName;
    private String errorMessageName;
    private AccountingDocument accountingDocumentForValidation;
    
    private static final String ACCOUNTING_LINES_GROUP_PROPERTY_SUFFIX = "AccountingLines";

    /**
     * Checks that the number of accounting lines in the accounting line group (named by the accountingLineGroupPropertyName property)
     * is greater than the set minimum number of accounting lines.
     * <strong>This validation expects the document to be sent in as a property.</strong>
     * @see org.kuali.kfs.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        List accountingLineGroup = (List)ObjectUtils.getPropertyValue(accountingDocumentForValidation, accountingLineGroupPropertyName);
        if (accountingLineGroup.size() < minimumNumber) {
            GlobalVariables.getErrorMap().putError(accountingLineGroupPropertyName, errorMessageName, new String[] { discoverGroupTitle(accountingDocumentForValidation) });
            return false;
        }
        return true;
    }
    
    /**
     * Returns the title of the given accounting line group on the document
     * @return an accounting line group title
     */
    private String discoverGroupTitle(AccountingDocument document) {
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
    private Method discoverGroupTitleMethod(AccountingDocument document) {
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
