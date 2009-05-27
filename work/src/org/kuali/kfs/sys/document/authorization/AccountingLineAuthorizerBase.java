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
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * The default implementation of AccountingLineAuthorizer
 */
public class AccountingLineAuthorizerBase implements AccountingLineAuthorizer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineAuthorizerBase.class);

    private KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    private static String riceImagePath;
    private static String kfsImagePath;

    /**
     * Returns the basic actions - add for new lines, delete and balance inquiry for existing lines
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getActions(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer, org.kuali.rice.kim.bo.Person,
     *      java.lang.String)
     */
    public final List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, Person currentUser, String groupTitle) {
        List<AccountingLineViewAction> actions = new ArrayList<AccountingLineViewAction>();

        if (accountingLineRenderingContext.isEditableLine() || isErrorMapContainingErrorsOnLine(accountingLinePropertyName)) {
            Map<String, AccountingLineViewAction> actionMap = this.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);
            actions.addAll(actionMap.values());
        }

        return actions;
    }
    
    /**
     * Determines if the error map contains any errors which exist on the currently rendered accounting line
     * @param accountingLinePropertyName the property name of the accounting line
     * @return true if there are errors on the line, false otherwise
     */
    protected boolean isErrorMapContainingErrorsOnLine(String accountingLinePropertyName) {
        for (Object errorKeyAsObject : GlobalVariables.getErrorMap().getPropertiesWithErrors()) {
            if (((String)errorKeyAsObject).startsWith(accountingLinePropertyName)) return true;
        }
        return false;
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
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        return (accountingDocument.getDocumentHeader().getWorkflowDocument().stateIsInitiated() || accountingDocument.getDocumentHeader().getWorkflowDocument().stateIsSaved());
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isGroupEditable(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return workflowDocument.userIsInitiator(currentUser);
        }
        
        for (AccountingLineRenderingContext renderingContext : accountingLineRenderingContexts) {
            if (renderingContext.isEditableLine()) return true;
        }

        return false;
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
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> actionMap = new HashMap<String, AccountingLineViewAction>();

        if (accountingLineIndex == null || accountingLineIndex < 0) {
            AccountingLineViewAction addAction = this.getAddAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, groupTitle);
            actionMap.put(KFSConstants.INSERT_METHOD, addAction);
        }
        else {
            if (accountingLineRenderingContext.allowDelete()) {
                AccountingLineViewAction deleteAction = this.getDeleteAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
                actionMap.put(KNSConstants.DELETE_METHOD, deleteAction);
            }

            AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
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
     * @param editableLine whether the parent line of this field is editable
     * @param editablePage whether the parent page of this field is editable
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given field in the given accounting line; otherwsie, false
     */
    public final boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editableLine, boolean editablePage, Person currentUser) {
        if (!determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage)) {
            return false;
        }
        
        // the fields in a new line should be always editable
        if (accountingLine.getSequenceNumber() == null) {
            return true;
        }

        // examine whether the given field can be editable
        boolean hasEditPermissionOnField = editableLine || this.determineEditPermissionByFieldName(accountingDocument, accountingLine, getKimHappyPropertyNameForField(accountingLineCollectionProperty+"."+fieldName), currentUser);

        return hasEditPermissionOnField;
    }
 
    /**
     * Allows the overriding of whether a field on an accounting line is editable or not
     * @param accountingDocument the accounting document the line to test is on
     * @param accountingLine the accounting line to test
     * @param accountingLineCollectionProperty the property that the accounting line lives in
     * @param fieldName the name of the field we are testing
     * @param editableLine whether the parent line of this field is editable
     * @param editablePage whether the parent page of this field is editable
     * @return true if the field can be edited (subject to subsequence KIM check); false otherwise
     */
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        if (!editablePage) return false; // no edits by default on non editable pages
        
        final FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader) accountingDocument.getDocumentHeader();
        final KualiWorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        // if a document is cancelled or in error, all of its fields cannot be editable
        if (workflowDocument.stateIsCanceled() || ObjectUtils.isNotNull(documentHeader.getFinancialDocumentInErrorNumber())) {
            return false;
        }

        return true;
    }

    /**
     * determine whether the current user has permission to edit the given accounting line as a whole
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given accounting line; otherwsie, false
     */
    public final boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, Person currentUser) {        
        if (determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty)) {
            
            // examine whether the whole line can be editable via KIM check
            final String lineFieldName = getKimHappyPropertyNameForField(accountingLineCollectionProperty);
            return this.determineEditPermissionByFieldName(accountingDocument, accountingLine, lineFieldName, currentUser);
        }
        return false;
    }
 
    /**
     * A hook to decide, pre-KIM check, if there's an edit permission on the given accounting line
     * @param accountingDocument the accounting document the line is or wants to be associated with
     * @param accountingLine the accounting line itself
     * @param accountingLineCollectionProperty the collection the accounting line is or would be part of
     * @return true if the line as a whole can be edited, false otherwise
     */
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty) {
        // the fields in a new line should be always editable
        if (accountingLine.getSequenceNumber() == null) {
            return true;
        }
        
        // check the initiation permission on the document if it is in the state of preroute
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            boolean hasEditLinePermission = workflowDocument.userIsInitiator(GlobalVariables.getUserSession().getPerson());
            return hasEditLinePermission;
        }
        
        if (accountingDocument instanceof Correctable) {
            String errorDocumentNumber = ((FinancialSystemDocumentHeader)accountingDocument.getDocumentHeader()).getFinancialDocumentInErrorNumber();
            if (StringUtils.isNotBlank(errorDocumentNumber))
                return false;
        }
        
        return true;
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
        final AttributeSet roleQualifiers = this.getRoleQualifiers(accountingDocument, accountingLine);
        final String documentTypeName = accountingDocument.getDocumentHeader().getWorkflowDocument().getDocumentType();
        final AttributeSet permissionDetail = this.getPermissionDetails(documentTypeName, fieldName);

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
        DocumentAuthorizer accountingDocumentAuthorizer = this.getDocumentAuthorizer(accountingDocument);
        
        return accountingDocumentAuthorizer.isAuthorizedByTemplate(accountingDocument, KFSConstants.ParameterNamespaces.KFS, KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name, pricipalId, permissionDetails, roleQualifiers);
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

        String actionImageName = getKFSImagePath() + "tinybutton-balinquiry.gif";

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

        String actionImageName = getRiceImagePath() + "tinybutton-delete1.gif";

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

        String actionImageName = getRiceImagePath() + "tinybutton-add1.gif";

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
    private DocumentAuthorizer getDocumentAuthorizer(AccountingDocument accountingDocument) {
        return SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(accountingDocument);
    }
    
    /**
     * @return the path to rice images
     */
    protected String getRiceImagePath() {
        if (riceImagePath == null) {
            riceImagePath = kualiConfigurationService.getPropertyString(KNSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        }
        return riceImagePath;
    }
    
    /**
     * @return the path to KFS images
     */
    protected String getKFSImagePath() {
        if (kfsImagePath == null) {
            kfsImagePath = kualiConfigurationService.getPropertyString(KNSConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY);
        }
        return kfsImagePath;
    }
}
