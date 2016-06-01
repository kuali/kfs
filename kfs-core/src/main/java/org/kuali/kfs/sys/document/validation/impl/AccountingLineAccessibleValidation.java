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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.*;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class AccountingLineAccessibleValidation extends GenericValidation {
    protected DataDictionaryService dataDictionaryService;
    protected AccountingDocument accountingDocumentForValidation;
    protected AccountingLine accountingLineForValidation;

    /**
     * Indicates what is being done to an accounting line. This allows the same method to be used for different actions.
     */
    public enum AccountingLineAction {
        ADD(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD),
        DELETE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE),
        UPDATE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE),
        REVIEW(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_REVIEW);

        public final String accessibilityErrorKey;

        AccountingLineAction(String accessabilityErrorKey) {
            this.accessibilityErrorKey = accessabilityErrorKey;
        }
    }

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        if (accountingDocumentForValidation instanceof Correctable) {
            final String errorDocumentNumber = ((FinancialSystemDocumentHeader)accountingDocumentForValidation.getDocumentHeader()).getFinancialDocumentInErrorNumber();
            if (StringUtils.isNotBlank(errorDocumentNumber)) {
                return true;
            }
        }

        final AccountingLineAuthorizer accountingLineAuthorizer = lookupAccountingLineAuthorizer();
        final boolean lineIsAccessible = accountingLineAuthorizer.hasEditPermissionOnAccountingLine(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), currentUser, true);
        final boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.ACCOUNT_NUMBER, lineIsAccessible, true, currentUser);

        if (!isAccessible) {
            // if only object code changed and the user has edit permissions on object code, that's ok
            if (event instanceof UpdateAccountingLineEvent) {
                final boolean isObjectCodeAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.FINANCIAL_OBJECT_CODE, lineIsAccessible, true, currentUser);
                final boolean onlyObjectCodeChanged = onlyObjectCodeChanged(((UpdateAccountingLineEvent) event).getAccountingLine(), ((UpdateAccountingLineEvent) event).getUpdatedAccountingLine());

                if (isObjectCodeAccessible && onlyObjectCodeChanged) {
                    return true;
                }
            }

            // report errors
            final String principalName = currentUser.getPrincipalName();

            final String[] accountErrorParams = new String[] { getDataDictionaryService().getAttributeLabel(accountingLineForValidation.getClass(), KFSPropertyConstants.ACCOUNT_NUMBER), accountingLineForValidation.getChartOfAccountsCode()+"-"+accountingLineForValidation.getAccountNumber(), principalName };
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, convertEventToMessage(event), accountErrorParams);
        }

        return isAccessible;
    }

    /**
     * Checks to see if the object code is the only difference between the original accounting line and the updated accounting line.
     *
     * @param accountingLine
     * @param updatedAccountingLine
     * @return true if only the object code has changed on the accounting line, false otherwise
     */
    private boolean onlyObjectCodeChanged(AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        // no changes, return false
        if (accountingLine.isLike(updatedAccountingLine)) {
            return false;
        }

        // copy the updatedAccountLine so we can set the object code on the copy of the updated accounting line
        // to be the original value for comparison purposes
        AccountingLine updatedLine = null;
        if (updatedAccountingLine.isSourceAccountingLine()) {
            updatedLine = new SourceAccountingLine();
        } else {
            updatedLine = new TargetAccountingLine();
        }

        updatedLine.copyFrom(updatedAccountingLine);
        updatedLine.setFinancialObjectCode(accountingLine.getFinancialObjectCode());

        // if they're the same, the only change was the object code
        return (accountingLine.isLike(updatedLine));
    }

    /**
     * Returns the name of the accounting line group which holds the proper authorizer to do the KIM check
     * @return the name of the accouting line group to get the authorizer from
     */
    protected String getGroupName() {
        return (accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.SOURCE_ACCOUNTING_LINES_GROUP_NAME : KFSConstants.TARGET_ACCOUNTING_LINES_GROUP_NAME);
    }

    /**
     * @return hopefully, the best accounting line authorizer implementation to do the KIM check for to see if lines are accessible
     */
    protected AccountingLineAuthorizer lookupAccountingLineAuthorizer() {
        final String groupName = getGroupName();
        final Map<String, AccountingLineGroupDefinition> groups = ((FinancialSystemTransactionalDocumentEntry)dataDictionaryService.getDataDictionary().getDictionaryObjectEntry(accountingDocumentForValidation.getClass().getName())).getAccountingLineGroups();

        if (groups.isEmpty())
         {
            return new AccountingLineAuthorizerBase(); // no groups? just use the default...
        }
        if (groups.containsKey(groupName))
         {
            return groups.get(groupName).getAccountingLineAuthorizer(); // we've got the group
        }

        final Set<String> groupNames = groups.keySet(); // we've got groups, just not the proper name; try our luck and get the first group iterator
        final Iterator<String> groupNameIterator = groupNames.iterator();
        final String firstGroupName = groupNameIterator.next();
        return groups.get(firstGroupName).getAccountingLineAuthorizer();
    }

    /**
     * Determines the property of the accounting line collection from the error prefixes
     * @return the accounting line collection property
     */
    protected String getAccountingLineCollectionProperty() {
        String propertyName = null;
        if (GlobalVariables.getMessageMap().getErrorPath().size() > 0) {
            propertyName = GlobalVariables.getMessageMap().getErrorPath().get(0).replaceFirst(".*?document\\.", "");
        } else {
            propertyName = accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        }
        if (propertyName.equals("newSourceLine")) {
            return KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value;
        }
        if (propertyName.equals("newTargetLine")) {
            return KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        }
        return propertyName;
    }

    /**
     * Determines what error message should be shown based on the event that required this validation
     * @param event the event to use to determine the error message
     * @return the key of the error message to display
     */
    protected String convertEventToMessage(KualiDocumentEvent event) {
        if (event instanceof AddAccountingLineEvent) {
            return AccountingLineAction.ADD.accessibilityErrorKey;
        } else if (event instanceof UpdateAccountingLineEvent) {
            return AccountingLineAction.UPDATE.accessibilityErrorKey;
        } else if (event instanceof DeleteAccountingLineEvent) {
            return AccountingLineAction.DELETE.accessibilityErrorKey;
        } else if (event instanceof ReviewAccountingLineEvent) {
            return AccountingLineAction.REVIEW.accessibilityErrorKey;
        } else {
            return "";
        }
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

    /**
     * Gets the accountingLineForValidation attribute.
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the dataDictionaryService attribute.
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}

