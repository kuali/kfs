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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Check for travel arranger access
 * Checking for ReturnToFiscalOfficerAuthorization
 *
 */
abstract public class TravelArrangeableAuthorizer extends AccountingDocumentAuthorizerBase implements ReturnToFiscalOfficerAuthorizer {

    private volatile RoleService roleService;
    private volatile BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject,java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> qualification) {
        super.addRoleQualification(dataObject, qualification);
        if (dataObject instanceof TravelDocument) {
            TravelDocument document = (TravelDocument) dataObject;
            // add the qualifications - profile and document type
            if (ObjectUtils.isNotNull(document.getProfileId())) {
                qualification.put(TemProfileProperties.PROFILE_ID, document.getProfileId().toString());
                qualification.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, document.getDocumentTypeName());

                final TemProfile profile = getBusinessObjectService().findBySinglePrimaryKey(TemProfile.class, document.getProfileId());
                if (profile != null) {
                	if (!qualification.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
                	    qualification.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getHomeDeptChartOfAccountsCode());
                	}
                	if (!qualification.containsKey(KFSPropertyConstants.ORGANIZATION_CODE)) {
                	    qualification.put(KFSPropertyConstants.ORGANIZATION_CODE, profile.getHomeDeptOrgCode());
                	}
                }
            }
            if (ObjectUtils.isNotNull(document.getTraveler())) {
                qualification.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, document.getTraveler().getPrincipalId());
            }
            if (!qualification.containsKey(KFSPropertyConstants.ACCOUNT_NUMBER)) { // FO permissions have precedence over accounting review permissions, so only overwrite if qualifiers for FO are missing
                addAccountingReviewerQualification((TravelDocument)dataObject, qualification);
            }
            addAwardReviewerQualification((TravelDocument)dataObject, qualification);
            addSubFundReviewerQualification((TravelDocument)dataObject, qualification);
        }
    }

    /**
     * Goes through the given List of accounting lines and finds one line where the current user is the accounting reviewer; it uses that line to add
     * accounting review qualifications to the qualifier
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addAccountingReviewerQualification(TravelDocument authorizationDoc, Map<String, String> attributes) {
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
    protected boolean addAccountingReviewQualificationForLine(TravelDocument travelAuth, TemSourceAccountingLine line, Map<String, String> attributes, Person currentUser) {
        if (ObjectUtils.isNull(line.getAccount())) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }

        if (!ObjectUtils.isNull(line.getAccount())) {
            Map<String, String> testQualifier = new HashMap<String, String>();
            testQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
            testQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, line.getAccount().getOrganizationCode());
            if (travelAuth instanceof AmountTotaling) {
                testQualifier.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, ((AmountTotaling)travelAuth).getTotalDollarAmount().toString());
            }
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
     * Goes through the given List of accounting lines and finds one line where the current user is responsible for the award; it uses that line to add
     * c&g responsibility qualifications to the qualifier
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addAwardReviewerQualification(TravelDocument authorizationDoc, Map<String, String> attributes) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        boolean foundQualification = false;
        int count = 0;
        while (!foundQualification && !ObjectUtils.isNull(authorizationDoc.getSourceAccountingLines()) && count < authorizationDoc.getSourceAccountingLines().size()) {
            final TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)authorizationDoc.getSourceAccountingLines().get(count);
            foundQualification = addAwardReviewQualificationForLine(authorizationDoc, accountingLine, attributes, currentUser);
            count += 1;
        }
    }

    /**
     * If the given user is an award reviewer based on accounting line, then add that account as a qualification
     * @param line the accounting line to check
     * @param attributes the role qualification to fill
     * @param currentUser the currently logged in user, whom we are doing permission checks for
     * @return true if qualifications were added based on the line, false otherwise
     */
    protected boolean addAwardReviewQualificationForLine(TravelDocument travelAuth, TemSourceAccountingLine line, Map<String, String> attributes, Person currentUser) {
        if (ObjectUtils.isNull(line.getAccount())) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }

        if (!ObjectUtils.isNull(line.getAccount()) && line.getAccount().getContractsAndGrantsAccountResponsibilityId() != null) {
            Map<String, String> testQualifier = new HashMap<String, String>();
            testQualifier.put(KfsKimAttributes.CONTRACTS_AND_GRANTS_ACCOUNT_RESPONSIBILITY_ID, line.getAccount().getContractsAndGrantsAccountResponsibilityId().toString());

            final List<Map<String,String>> roleQualifiers = getRoleService().getRoleQualifersForPrincipalByNamespaceAndRolename(currentUser.getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROCESSOR, testQualifier);

            if (roleQualifiers != null && !roleQualifiers.isEmpty()) {
                // we can chose any of these since they all work - so we'll just choose the first
                attributes.putAll(roleQualifiers.get(0));
                return true;
            }
        }
        return false;
    }

    /**
     * Goes through the given List of accounting lines and finds one line where the current user is responsible for the sub-fund; it uses that line to add
     * sub-fund qualifications to the qualifier
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addSubFundReviewerQualification(TravelDocument authorizationDoc, Map<String, String> attributes) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        boolean foundQualification = false;
        int count = 0;
        while (!foundQualification && !ObjectUtils.isNull(authorizationDoc.getSourceAccountingLines()) && count < authorizationDoc.getSourceAccountingLines().size()) {
            final TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)authorizationDoc.getSourceAccountingLines().get(count);
            foundQualification = addSubFundQualificationForLine(authorizationDoc, accountingLine, attributes, currentUser);
            count += 1;
        }
    }

    /**
     * If the given user is an award reviewer based on accounting line, then add that account as a qualification
     * @param line the accounting line to check
     * @param attributes the role qualification to fill
     * @param currentUser the currently logged in user, whom we are doing permission checks for
     * @return true if qualifications were added based on the line, false otherwise
     */
    protected boolean addSubFundQualificationForLine(TravelDocument travelAuth, TemSourceAccountingLine line, Map<String, String> attributes, Person currentUser) {
        if (ObjectUtils.isNull(line.getAccount())) {
            line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }

        if (!ObjectUtils.isNull(line.getAccount()) && !StringUtils.isBlank(line.getAccount().getSubFundGroupCode())) {
            Map<String, String> testQualifier = new HashMap<String, String>();
            testQualifier.put(KfsKimAttributes.SUB_FUND_GROUP_CODE, line.getAccount().getSubFundGroupCode());
            testQualifier.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, travelAuth.getDocumentTypeName());

            final List<Map<String,String>> roleQualifiers = getRoleService().getRoleQualifersForPrincipalByNamespaceAndRolename(currentUser.getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.SUB_FUND_REVIEWER, testQualifier);

            if (roleQualifiers != null && !roleQualifiers.isEmpty()) {
                // let's make sure that our given doc type actually matches - we've seen weird bugs with that
                for (Map<String, String> qualifier : roleQualifiers) {
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
     * Overridden to pass in profile principal id as the current user's principal id
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        qualificationDetails.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, user.getPrincipalId());
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), nameSpaceCode,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, qualificationDetails);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canEditDocumentOverview(org.kuali.rice.kns.document.Document,org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        return canEditDocument(document, user);
    }

    /**
     * Check edit permission on document
     *
     * @param document
     * @param user
     * @return
     */
    public boolean canEditDocument(Document document, Person user) {
     // override base implementation to only allow initiator to edit doc overview
        return isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId());
    }

    public boolean canTaxSelectable(final Person user){
        return getPermissionService().hasPermission(user.getPrincipalId(), TemConstants.PARAM_NAMESPACE, TemConstants.Permission.EDIT_TAXABLE_IND);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.ReturnToFiscalOfficerAuthorizer#canReturnToFisicalOfficer(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canReturnToFisicalOfficer(final TravelDocument travelDocument, final Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }

        WorkflowDocument workflowDocument = travelDocument.getDocumentHeader().getWorkflowDocument();

        // initiator cannot Hold their own document
        String initiator = workflowDocument.getInitiatorPrincipalId();
        if (initiator.equals(user.getPrincipalId())){
            return false;
        }

        //now check to see if they are a Fiscal Officer
        if (getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId())) {
            return false;
        }

        //check if the doc can be routed
        if(workflowDocument.isFinal() || workflowDocument.isProcessed() || !workflowDocument.isApprovalRequested()){
            return false;
        }

        String nameSpaceCode = org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
        Map<String,String> roleQualifications = new HashMap<String,String>();
        addRoleQualification(travelDocument, roleQualifications);

        return getPermissionService().isAuthorized(user.getPrincipalId(), nameSpaceCode, TemConstants.Permission.RETURN_TO_FO, roleQualifications);

    }

    /**
     * Determine if the Fiscal Officer Role has permission named by permission name
     *
     * @param name of the permission to check for Fiscal Officer authorization on. This is usually, "Amend TA", "Close TA", "Cancel TA", "Hold TA", or "Unhold TA"
     * @boolean true if fiscal officer has rights or false otherwise
     */
    protected boolean isFiscalOfficerAuthorizedTo(final String permission, String documentTypeName) {

        final String fiscalOfficerRoleId = getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME);

        //**TODO remove the checking because new permission service does not allow for permission detail - and each permission is tied specifically to the travel document type,
        //               No need to further qualified by document type name
        //final List<String> roles = getPermissionService().getRoleIdsForPermission(TemConstants.PARAM_NAMESPACE, action, permissionDetails);

        final List<String> roles = getPermissionService().getRoleIdsForPermission(TemConstants.PARAM_NAMESPACE, permission);
        return (roles != null && roles.size() > 0 && roles.contains(fiscalOfficerRoleId));
    }

    /**
     * calculate and save falls in the exact same logic
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canCalculate(TravelDocument travelDocument, Person user) {
        return canSave(travelDocument, user);
    }

    /**
     *
     * @return
     */
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    /**
     * Check for employee
     *
     * @param traveler
     * @return
     */
    protected boolean isEmployee(final TravelerDetail traveler) {
        return traveler == null? false : getTravelerService().isEmployee(traveler);
    }

    protected TravelerService getTravelerService() {
        return SpringContext.getBean(TravelerService.class);
    }

    public TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }

    protected TemRoleService getTemRoleService() {
        return SpringContext.getBean(TemRoleService.class);
    }

    protected RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = SpringContext.getBean(RoleService.class);
        }
        return roleService;
    }

    /**
     * @return the default implementation of the BusinessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

}
