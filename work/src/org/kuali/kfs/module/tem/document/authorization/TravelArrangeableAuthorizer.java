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
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
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
                qualification.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, document.getTraveler().getPrincipalId());
            }
        }
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
        qualificationDetails.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, user.getPrincipalId());
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

        if (!workflowDocument.isEnroute()) {
            return false;
        }

        //now check to see if they are a Fiscal Officer
        final Set<String> appDocStatuses = getNonReturnToFiscalOfficerDocumentStatuses();
        if (appDocStatuses.contains(travelDocument.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())) {
            return false;
        }

        String nameSpaceCode = org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
        Map<String,String> roleQualifications = new HashMap<String,String>();
        addRoleQualification(travelDocument, roleQualifications);

        return getPermissionService().isAuthorized(user.getPrincipalId(), nameSpaceCode, TemConstants.Permission.RETURN_TO_FO, roleQualifications);
    }

    /**
     * @return the generic Set of names of application document statuses where returning to Fiscal Officer is impossible (either because we're at fiscal officer or because we're before it)
     */
    protected Set<String> getNonReturnToFiscalOfficerDocumentStatuses() {
        Set<String> appDocStatuses = new HashSet<String>();
        appDocStatuses.add(TemConstants.TravelStatusCodeKeys.IN_PROCESS);
        appDocStatuses.add(TemConstants.TravelStatusCodeKeys.AWAIT_TRVLR);
        appDocStatuses.add(TemConstants.TravelStatusCodeKeys.AWAIT_FISCAL);
        return appDocStatuses;
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
