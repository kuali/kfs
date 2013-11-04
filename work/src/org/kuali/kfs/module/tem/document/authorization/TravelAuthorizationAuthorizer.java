/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthorizationAuthorizer extends TravelArrangeableAuthorizer {
    protected volatile RoleService roleService;

    /**
     *  check permission to close
     *
     * @param taDoc
     * @param user
     * @return
     */
    public boolean canClose(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.Permission.CLOSE_TA);
    }

    /**
     * Check permission for amend
     *
     * @param taDoc
     * @param user
     * @return
     */
    public boolean canAmend(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.Permission.AMEND_TA);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canHold(TravelAuthorizationDocument travelDocument, Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.HOLD_TA);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canRemoveHold(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.REMOVE_HOLD_TA);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canCancel(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.CANCEL_TA);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean hideButtons(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.HIDE_BUTTONS);
    }

    /**
     * If the current user is a fiscal officer on any accounting line on the document, add that qualification
     * @see org.kuali.kfs.module.tem.document.authorization.TravelArrangeableAuthorizer#addRoleQualification(java.lang.Object, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> qualification) {
        if (dataObject instanceof TravelAuthorizationDocument) {
            addAccountQualification((TravelAuthorizationDocument)dataObject, qualification);
            addTemProfileQualification((TravelAuthorizationDocument)dataObject, qualification);
            if (!qualification.containsKey(KFSPropertyConstants.ACCOUNT_NUMBER)) { // FO permissions have precedence over accounting review permissions, so only overwrite if qualifiers for FO are missing
                addAccountingReviewerQualification((TravelAuthorizationDocument)dataObject, qualification);
            }
        }

        super.addRoleQualification(dataObject, qualification);
    }

    protected void addTemProfileQualification(TravelAuthorizationDocument document, Map<String, String> attributes) {
        if (ObjectUtils.isNotNull(document.getTemProfileId())){
            attributes.put(KFSPropertyConstants.PRINCIPAL_ID, document.getTraveler().getPrincipalId());
        }
    }

    /**
     * Goes through the given List of accounting lines and fines one line where the current user is the fiscal officer; it uses that line to put chart of accounts
     * code and account number qualifications into the given Map of attributes for role qualification
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addAccountQualification(TravelAuthorizationDocument authorizationDoc, Map<String, String> attributes) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean foundQualification = false;
        int count = 0;
        while (!foundQualification && !ObjectUtils.isNull(authorizationDoc.getSourceAccountingLines()) && count < authorizationDoc.getSourceAccountingLines().size()) {
            final TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)authorizationDoc.getSourceAccountingLines().get(count);
            foundQualification = addAccountQualificationForLine(accountingLine, attributes, currentUser);
            count += 1;
        }
    }

    /**
     * If the given user is the fiscal officer of the account on the given accounting line, then add that account as a qualification
     * @param line the accounting line to check
     * @param attributes the role qualification to fill
     * @param currentUser the currently logged in user, whom we are doing permission checks for
     * @return true if qualifications were added based on the line, false otherwise
     */
    protected boolean addAccountQualificationForLine(TemSourceAccountingLine line, Map<String, String> attributes, Person currentUser) {
        if (ObjectUtils.isNull(line.getAccount())) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }
        if (!ObjectUtils.isNull(line.getAccount()) && currentUser.getPrincipalId().equalsIgnoreCase(line.getAccount().getAccountFiscalOfficerSystemIdentifier())) {
            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
            attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, line.getAccountNumber());
            return true;
        }
        return false;
    }

    /**
     * Goes through the given List of accounting lines and finds one line where the current user is the accounting reviewer; it uses that line to add
     * accounting review qualifications to the qualifier
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addAccountingReviewerQualification(TravelAuthorizationDocument authorizationDoc, Map<String, String> attributes) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        boolean foundQualification = false;
        int count = 0;
        while (!foundQualification && !ObjectUtils.isNull(authorizationDoc.getSourceAccountingLines()) && count < authorizationDoc.getSourceAccountingLines().size()) {
            final TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)authorizationDoc.getSourceAccountingLines().get(count);
            foundQualification = addAccountingReviewQualificationForLine(authorizationDoc, accountingLine, attributes, currentUser);
            count += 1;
        }
    }

    /**
     * If the given user is an accounting reviewer based on accounting line, then add that account as a qualification
     * @param line the accounting line to check
     * @param attributes the role qualification to fill
     * @param currentUser the currently logged in user, whom we are doing permission checks for
     * @return true if qualifications were added based on the line, false otherwise
     */
    protected boolean addAccountingReviewQualificationForLine(TravelAuthorizationDocument travelAuth, TemSourceAccountingLine line, Map<String, String> attributes, Person currentUser) {
        if (ObjectUtils.isNull(line.getAccount())) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }

        if (!ObjectUtils.isNull(line.getAccount())) {
            Map<String, String> testQualifier = new HashMap<String, String>();
            testQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
            testQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, line.getAccount().getOrganizationCode());
            testQualifier.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, travelAuth.getTotalDollarAmount().toString());
            testQualifier.put(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, line.getOverrideCode());
            testQualifier.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, travelAuth.getDocumentTypeName());

            final List<Map<String,String>> userAccountingReviewQualifiers = getRoleService().getRoleQualifersForPrincipalByNamespaceAndRolename(currentUser.getPrincipalId(), KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME, testQualifier);

            if (userAccountingReviewQualifiers != null && !userAccountingReviewQualifiers.isEmpty()) {
                // let's make sure that our given doc type actually matches - we've seen weird bugs with that
                for (Map<String, String> qualifier : userAccountingReviewQualifiers) {
                    if (qualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                        Set<String> possibleParentDocumentTypes = new HashSet<String>();
                        possibleParentDocumentTypes.add(qualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));

                        final String closestParent = KimCommonUtils.getClosestParentDocumentTypeName(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(testQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), possibleParentDocumentTypes);
                        if (closestParent != null) {
                            attributes.putAll(qualifier);
                            return true;
                        }
                    } else {
                        // no doc type.  That's weird, but whatever - it passes
                        attributes.putAll(qualifier);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Generic way of calling permissions
     *
     * @param travelDocument the authorization we're authorizing actions against
     * @param user the user to check permission for
     * @param permission the name of the KFS-TEM namespace permission to check
     * @return true if the permission is granted, false otherwise
     */
    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String permission){
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;

        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
        return isAuthorized(travelDocument, nameSpaceCode, permission, user.getPrincipalId());
    }

    /**
     * Overridden to handle travel authorization specific actions
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> actions = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        final TravelAuthorizationDocument travelAuth = (TravelAuthorizationDocument)document;
        if (actions.contains(TemConstants.TravelAuthorizationActions.CAN_AMEND) && !canAmend(travelAuth, user)) {
            actions.remove(TemConstants.TravelAuthorizationActions.CAN_AMEND);
        }
        if (actions.contains(TemConstants.TravelAuthorizationActions.CAN_HOLD) && !canHold(travelAuth, user)) {
            actions.remove(TemConstants.TravelAuthorizationActions.CAN_HOLD);
        }
        if (actions.contains(TemConstants.TravelAuthorizationActions.CAN_REMOVE_HOLD) && !canRemoveHold(travelAuth, user)) {
            actions.remove(TemConstants.TravelAuthorizationActions.CAN_REMOVE_HOLD);
        }
        if (actions.contains(TemConstants.TravelAuthorizationActions.CAN_CLOSE_TA) && !canClose(travelAuth, user)) {
            actions.remove(TemConstants.TravelAuthorizationActions.CAN_CLOSE_TA);
        }
        if (actions.contains(TemConstants.TravelAuthorizationActions.CAN_CANCEL_TA) && !canCancel(travelAuth, user)) {
            actions.remove(TemConstants.TravelAuthorizationActions.CAN_CANCEL_TA);
        }
        return actions;
    }

    @Override
    protected RoleService getRoleService() {
        if (roleService == null) {
            roleService = SpringContext.getBean(RoleService.class);
        }
        return roleService;
    }
}
