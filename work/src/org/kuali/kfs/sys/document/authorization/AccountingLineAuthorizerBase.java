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
package org.kuali.kfs.sys.document.authorization;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * The default implementation of AccountingLineAuthorizer
 */
public class AccountingLineAuthorizerBase implements AccountingLineAuthorizer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineAuthorizerBase.class);

    private KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

    /**
     * Returns the basic actions - add for new lines, delete and balance inquiry for existing lines
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getActions(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer, org.kuali.rice.kim.bo.Person,
     *      java.lang.String)
     */
    public final List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, Person currentUser, String groupTitle) {
        List<AccountingLineViewAction> actions = new ArrayList<AccountingLineViewAction>();

        String accountingLineGroupFieldName = this.replaceCollectionElementsWithPlurals(accountingLinePropertyName);
        boolean hasEditPermission = this.hasEditPermissionOnAccountingLine(accountingDocument, accountingLine, currentUser);
        if (hasEditPermission) {
            Map<String, AccountingLineViewAction> actionMap = this.getActionMap(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
            actions.addAll(actionMap.values());
        }

        return actions;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isFieldModifyable(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField,
     *      org.kuali.rice.kim.bo.Person)
     */
    public final boolean isFieldEditable(AccountingDocument accountingDocument, AccountingLine accountingLine, AccountingLineViewField field, Person currentUser) {
        // determine whether the current user has permission to edit the given field
        String fieldName = getKimHappyPropertyNameForField(this.getFieldName(field));
        boolean kimsAnswer = this.hasEditPermissionOnField(accountingDocument, accountingLine, fieldName, currentUser);

        return kimsAnswer && determineFieldEditability(accountingDocument, accountingLine, field);
    }

    /**
     * Returns a new empty HashSet
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean)
     */
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        return new HashSet<String>();
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String)
     */
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty, Person currentUser) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isGroupEditable(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean isGroupEditable(AccountingDocument accountingDocument, String accountingLineCollectionProperty, Person currentUser) {
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return workflowDocument.userIsInitiator(currentUser);
        }

        String accountingLineGroupFieldName = this.stripDocumentPrefixFromName(accountingLineCollectionProperty);
        List<AccountingLine> accountingLinesInGroup = new ArrayList<AccountingLine>();
        if (accountingLineGroupFieldName.startsWith(KFSConstants.SOURCE.toLowerCase())) {
            accountingLinesInGroup.addAll(accountingDocument.getSourceAccountingLines());
        }
        else if (accountingLineGroupFieldName.startsWith(KFSConstants.TARGET.toLowerCase())) {
            accountingLinesInGroup.addAll(accountingDocument.getTargetAccountingLines());
        }

        // examine whether the whole line can be editable
        for (AccountingLine accountingLine : accountingLinesInGroup) {
            boolean hasEditPermission = this.hasEditPermissionOnAccountingLine(accountingDocument, accountingLine, currentUser);

            if (hasEditPermission) {
                return true;
            }
        }

        return false;
    }

    /**
     * determine whether the given accounting line is a new line or not. If it is not in the given document, it is a new line
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line
     * @return true if the given accounting line is a new line; otherwise, false
     */
    protected boolean isNewLine(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isSourceLine = accountingDocument.getSourceAccountingLines().contains(accountingLine);
        boolean isTargetLine = accountingDocument.getTargetAccountingLines().contains(accountingLine);

        return !isSourceLine && !isTargetLine;
    }

    /**
     * collection the actions that are allowed for the given accounting line
     * 
     * @param accountingLine the given accounting line
     * @param accountingLinePropertyName the property name of the given account line, typically, the form name
     * @param accountingLineIndex the index of the given accounting line in its accounting line group
     * @param groupTitle the title of the accounting line group
     * @return the actions that are allowed for the given accounting line
     */
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> actionMap = new HashMap<String, AccountingLineViewAction>();

        if (accountingLineIndex == null || accountingLineIndex < 0) {
            AccountingLineViewAction addAction = this.getAddAction(accountingLine, accountingLinePropertyName, groupTitle);
            actionMap.put(KFSConstants.INSERT_METHOD, addAction);
        }
        else {
            AccountingLineViewAction deleteAction = this.getDeleteAction(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KNSConstants.DELETE_METHOD, deleteAction);

            AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);
        }

        return actionMap;
    }

    /**
     * determine whether the current user has permission to edit the given field in the given accounting line
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param fieldName the name of a field in the given accounting line
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given field in the given accounting line; otherwsie, false
     */
    protected boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser) {
        // the fields in a new line should be always editable
        if (this.isNewLine(accountingDocument, accountingLine)) {
            return true;
        }

        // examine whether the whole line can be editable
        String lineFieldName = accountingLine.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        boolean hasEditPermissionOnField = this.determineEditPermissionByFieldName(accountingDocument, accountingLine, lineFieldName, currentUser);

        // examine whether the given field can be editable
        hasEditPermissionOnField |= this.determineEditPermissionByFieldName(accountingDocument, accountingLine, fieldName, currentUser);

        return hasEditPermissionOnField;
    }

    /**
     * determine whether the current user has permission to edit the given accounting line as a whole
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given accounting line; otherwsie, false
     */
    protected boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, Person currentUser) {        
        // the fields in a new line should be always editable
        if (this.isNewLine(accountingDocument, accountingLine)) {
            return true;
        }

        // check the initiation permission on the document if it is in the state of preroute
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            boolean hasEditLinePermission = workflowDocument.userIsInitiator(currentUser);
            return hasEditLinePermission;
        }

        // examine whether the whole line can be editable
        String lineFieldName = accountingLine.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        return this.determineEditPermissionByFieldName(accountingDocument, accountingLine, lineFieldName, currentUser);
    }

    /**
     * determine whether the current user has permission to edit the given field in the given accounting line
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param fieldName the name of a field in the given accounting line
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given field in the given accounting line; otherwsie, false
     */
    private boolean determineEditPermissionByFieldName(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser) {        
        AttributeSet roleQualifiers = this.getRoleQualifiers(accountingDocument, accountingLine);
        
        String documentTypeName = accountingDocument.getClass().getSimpleName();
        AttributeSet permissionDetail = this.getPermissionDetails(documentTypeName, fieldName);

        return this.hasEditPermission(accountingDocument, currentUser, permissionDetail, roleQualifiers);       
    }

    /**
     * determine whether the current user has modification permission on an accounting line with the given qualifications. The
     * permission template and namespace have been setup in the method.
     * 
     * @param currentUser the current user
     * @param permissionDetails the given permission details
     * @param roleQualifiers the given role qualifications
     * @return true if the user has edit permission on an accounting line with the given qualifications; otherwise, false
     */
    private boolean hasEditPermission(AccountingDocument accountingDocument, Person currentUser, AttributeSet permissionDetails, AttributeSet roleQualifiers) {
        String pricipalId = currentUser.getPrincipalId();
        AccountingDocumentAuthorizer accountingDocumentAuthorizer = this.getAccountingDocumentAuthorizer(accountingDocument);
        
        return accountingDocumentAuthorizer.isAuthorizedByTemplate(accountingDocument, KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.MODIFY_ACCOUNTING_LINES_PERMISSION_TEMPLATE_NAME, pricipalId, permissionDetails, roleQualifiers);
    }

    /**
     * A hook to let implementers add logic to determine whether an accounting line field is editable or not. Do you want to modify
     * isFieldeditable?? Here is your hook, young programmer!
     * 
     * @param accountingDocument the accounting document the accounting line is on or eventually will be on
     * @param accountingLine the accounting line the field is a member of
     * @param field the field we're testing the modifyability of
     * @return true if field can be modified, false otherwise
     */
    protected boolean determineFieldEditability(AccountingDocument accountingDocument, AccountingLine accountingLine, AccountingLineViewField field) {
        FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader) accountingDocument.getDocumentHeader();
        KualiWorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        // if a document is cancelled or in error, all of its fields cannot be editable
        if (workflowDocument.stateIsCanceled() || ObjectUtils.isNotNull(documentHeader.getFinancialDocumentInErrorNumber())) {
            return false;
        }

        return true;
    }

    /**
     * Gathers together all the information for a permission detail attribute set
     * 
     * @param documentTypeName the document
     * @param fieldName the given field name
     * @return all the information for a permission detail attribute set
     */
    private AttributeSet getPermissionDetails(String documentTypeName, String fieldName) {
        AttributeSet permissionDetails = new AttributeSet();

        if (StringUtils.isNotBlank(documentTypeName)) {
            permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, documentTypeName);
        }

        if (StringUtils.isNotBlank(fieldName)) {
            permissionDetails.put(KfsKimAttributes.PROPERTY_NAME, fieldName);
        }

        return permissionDetails;
    }

    /**
     * Gathers together the role qualifiers for the KIM perm call
     * 
     * @param accountingLine the accounting line to get role qualifiers from
     * @return the gathered AttributeSet of role qualifiers
     */
    private final AttributeSet getRoleQualifiers(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        AttributeSet roleQualifiers = new AttributeSet();

        if (accountingLine != null) {
            roleQualifiers.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
            roleQualifiers.put(KfsKimAttributes.ACCOUNT_NUMBER, accountingLine.getAccountNumber());
        }

        return roleQualifiers;
    }

    /**
     * @param field AccountingLineViewField to find KIM-happy property name for
     * @return a property name that KIM will like
     */
    protected String getKimHappyPropertyNameForField(String convertedName) {
        convertedName = stripDocumentPrefixFromName(convertedName);

        return replaceCollectionElementsWithPlurals(convertedName);
    }

    /**
     * get the full property name of the given field
     * 
     * @param field the field to get the name from
     * @return the full property name of the given field, typically, a combination of property prefix and simple property name
     */
    protected String getFieldName(AccountingLineViewField field) {
        String propertyPrefix = field.getField().getPropertyPrefix();
        String propertyName = field.getField().getPropertyName();

        return StringUtils.isNotBlank(propertyPrefix) ? (propertyPrefix + "." + propertyName) : propertyName;
    }

    /**
     * Strips "document." and everything before from the property name
     * 
     * @param name the property name to strip the document portion off of
     * @return the stripped name
     */
    protected String stripDocumentPrefixFromName(String name) {
        return name.replaceFirst("(.)*document\\.", StringUtils.EMPTY);
    }

    /**
     * Replaces references to collection elements to their respective plural names WARNING: this method is totally lame and I for
     * one wished it didn't have to exist
     * 
     * @param name the property name with perhaps collection elements in
     * @return the corrected name
     */
    protected String replaceCollectionElementsWithPlurals(String name) {
        return name.replaceAll("\\[\\d+\\]", "s");
    }

    /**
     * construct the balance inquiry action for the given accounting line
     * 
     * @param accountingLine the given accounting line
     * @param accountingLinePropertyName the property name of the given account line, typically, the form name
     * @param accountingLineIndex the index of the given accounting line in its accounting line group
     * @param groupTitle the title of the accounting line group
     * @return the balance inquiry action for the given accounting line
     */
    protected AccountingLineViewAction getBalanceInquiryAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getBalanceInquiryMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_BALANCE_INQUIRY_ACTION_LABEL, groupTitle, accountingLineIndex + 1);

        String imagesPath = kualiConfigurationService.getPropertyString(KNSConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY);
        String actionImageName = imagesPath + "tinybutton-balinquiry.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }

    /**
     * construct the delete action for the given accounting line
     * 
     * @param accountingLine the given accounting line
     * @param accountingLinePropertyName the property name of the given account line, typically, the form name
     * @param accountingLineIndex the index of the given accounting line in its accounting line group
     * @param groupTitle the title of the accounting line group
     * @return the delete action for the given accounting line
     */
    protected AccountingLineViewAction getDeleteAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getDeleteLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_DELETE_ACTION_LABEL, groupTitle, accountingLineIndex + 1);

        String imagesPath = kualiConfigurationService.getPropertyString(KNSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        String actionImageName = imagesPath + "tinybutton-delete1.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }

    /**
     * construct the add action for the given accounting line, typically, a new accounting line
     * 
     * @param accountingLine the given accounting line
     * @param accountingLinePropertyName the property name of the given account line, typically, the form name
     * @param accountingLineIndex the index of the given accounting line in its accounting line group
     * @param groupTitle the title of the accounting line group
     * @return the add action for the given accounting line
     */
    protected AccountingLineViewAction getAddAction(AccountingLine accountingLine, String accountingLinePropertyName, String groupTitle) {
        String actionMethod = this.getAddMethod(accountingLine, accountingLinePropertyName);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_ADD_ACTION_LABEL, groupTitle);

        String imagesPath = kualiConfigurationService.getPropertyString(KNSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        String actionImageName = imagesPath + "tinybutton-add1.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }

    /**
     * get a label for an action with the specified message key and values
     * 
     * @param messageKey the given message key that points to the label
     * @param values the given values that would be displayed in label
     * @return a label for an action with the specified message key and values
     */
    protected String getActionLabel(String messageKey, Object... values) {
        String messageBody = kualiConfigurationService.getPropertyString(messageKey);

        return MessageFormat.format(messageBody, values);
    }

    /**
     * Builds the action method name of the method that adds accounting lines for this group
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the action method name of the method that adds accounting lines for this group
     */
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        final String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        return KFSConstants.INSERT_METHOD + infix + "Line.anchoraccounting" + infix + "Anchor";
    }

    /**
     * Builds the action method name of the method that deletes accounting lines for this group
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that deletes accounting lines for this group
     */
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return KNSConstants.DELETE_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    /**
     * Builds the action method name of the method that performs a balance inquiry on accounting lines for this group
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that performs a balance inquiry on accounting lines for this group
     */
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }

    /**
     * Gets the "action infix" for the given accounting line, so that the action knows it is supposed to add to source vs. target
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the name of the action infix
     */
    protected String getActionInfixForNewAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (accountingLine.isSourceAccountingLine()) {
            return KFSConstants.SOURCE;
        }

        if (accountingLine.isTargetAccountingLine()) {
            return KFSConstants.TARGET;
        }

        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the "action infix" for the given accounting line which already exists on the document, so that the action knows it is
     * supposed to add to source vs. target
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @return the name of the action infix
     */
    protected String getActionInfixForExtantAccountingLine(AccountingLine accountingLine, String accountingLinePropertyName) {
        if (accountingLine.isSourceAccountingLine()) {
            return KFSConstants.SOURCE;
        }

        if (accountingLine.isTargetAccountingLine()) {
            return KFSConstants.TARGET;
        }

        return KFSConstants.EMPTY_STRING;
    }

    /**
     * get the document authorizer of the given accounting document
     * 
     * @param accountingDocument the given accounting document
     * @return the document authorizer of the given accounting document
     */
    private AccountingDocumentAuthorizer getAccountingDocumentAuthorizer(AccountingDocument accountingDocument) {
        return (AccountingDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(accountingDocument);
    }
}
