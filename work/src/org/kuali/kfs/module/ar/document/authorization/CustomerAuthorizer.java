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
package org.kuali.module.ar.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerAuthorizer extends MaintenanceDocumentAuthorizerBase {

    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {

        super.canInitiate(documentTypeName, user);
        // to initiate, the user must have the organization options set up.
        ChartUser chartUser = ValueFinderUtil.getCurrentChartUser();
        String chartCode = chartUser.getChartOfAccountsCode();
        String orgCode = chartUser.getUserOrganizationCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartCode);
        criteria.put("organizationCode", orgCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        KualiConfigurationService configurationService = SpringContext.getBean(KualiConfigurationService.class);

        // if organization doesn't exist
        if (ObjectUtils.isNull(organizationOptions)) {
            throw new DocumentInitiationAuthorizationException(ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, new String[] {});

        }
    }

    /**
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editModes = super.getEditMode(document, user);

        MaintenanceDocument maintDocument = (MaintenanceDocument) document;
        String maintenanceAction = maintDocument.getNewMaintainableObject().getMaintenanceAction();
        if (maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_EDIT_ACTION) || maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION)) {
            if (!isUserInArSupervisorGroup(user)) {
                editModes.clear();
                editModes.put(AuthorizationConstants.EditMode.UNVIEWABLE, "TRUE");
            }
        }

        return editModes;
    }

    private boolean isUserInArSupervisorGroup(UniversalUser user) {
        boolean retVal = false;
        try {
            retVal = SpringContext.getBean(KualiGroupService.class).getByGroupName(ArConstants.AR_SUPERVISOR_GROUP_NAME).hasMember(user);
        }
        catch (GroupNotFoundException gnfe) {

        }

        return retVal;
    }

     /**
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);

        MaintenanceDocument maintDocument = (MaintenanceDocument) document;
        String maintenanceAction = maintDocument.getNewMaintainableObject().getMaintenanceAction();

        // if user is not AR SUPERVISOR he cannot approve the customer creation document
        if (maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_NEW_ACTION) && !isUserInArSupervisorGroup(user)) {

            actionFlags.setCanApprove(false);
            actionFlags.setCanBlanketApprove(false);

        }

//         if ((maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_EDIT_ACTION) || maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION)) && !isUserInArSupervisorGroup(user)) {
//
//            actionFlags.setCanRoute(false);
//            actionFlags.setCanSave(false);
//            actionFlags.setCanCancel(false);
//
//        }
        return actionFlags;
    }

}
