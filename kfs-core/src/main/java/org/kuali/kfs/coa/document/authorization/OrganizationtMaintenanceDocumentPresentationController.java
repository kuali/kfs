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

package org.kuali.kfs.coa.document.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class can be shared by all account-involved maintenance documents which have special nested reference accounts.
 */
public class OrganizationtMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     *
     * This methods adds the extra COA code fields that are PKs of nested reference accounts but don't exist in the BO as FKs
     * to the readOnlyPropertyNames set when accounts can't cross charts.
     * Since these fields aren't included in AccountPersistenceStructureService.listChartOfAccountsCodeNames as
     * in super.getConditionallyReadOnlyPropertyNames, they need to be added individually for such special cases.
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        AttributeSecurity chartReadOnlyAttributeSecurity = dataDictionaryService.getAttributeSecurity(Organization.class.getName(), KFSPropertyConstants.CAMPUS_PLANT_CHART_CODE);
        chartReadOnlyAttributeSecurity.setReadOnly(true);

        AttributeSecurity plantAccountReadOnlyAttributeSecurity = dataDictionaryService.getAttributeSecurity(Organization.class.getName(), KFSPropertyConstants.CAMPUS_PLANT_ACCOUNT_NUMBER);
        plantAccountReadOnlyAttributeSecurity.setReadOnly(true);

        AttributeSecurity OrgChartReadOnlyAttributeSecurity = dataDictionaryService.getAttributeSecurity(Organization.class.getName(), KFSPropertyConstants.ORGANIZATION_PLANT_CHART_CODE);
        OrgChartReadOnlyAttributeSecurity.setReadOnly(true);

        AttributeSecurity orgPlantAccountReadOnlyAttributeSecurity = dataDictionaryService.getAttributeSecurity(Organization.class.getName(), KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT_NUMBER);
        orgPlantAccountReadOnlyAttributeSecurity.setReadOnly(true);

        Organization organization = (Organization) document.getNewMaintainableObject().getDataObject();

        // get user
        Person user = GlobalVariables.getUserSession().getPerson();
        Map<String,String> roleQualifiers = new HashMap<String,String>();

        if (isCampusChartManagerAuthorized(user, KFSPropertyConstants.CAMPUS_PLANT_CHART_CODE, roleQualifiers)) {
            chartReadOnlyAttributeSecurity.setReadOnly(false);
        }
        if (isCampusChartManagerAuthorized(user, KFSPropertyConstants.CAMPUS_PLANT_ACCOUNT_NUMBER, roleQualifiers)) {
            plantAccountReadOnlyAttributeSecurity.setReadOnly(false);
        }

        if (isCampusChartManagerAuthorized(user, KFSPropertyConstants.ORGANIZATION_PLANT_CHART_CODE, roleQualifiers)) {
            OrgChartReadOnlyAttributeSecurity.setReadOnly(false);
        }

        if (isCampusChartManagerAuthorized(user, KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT_NUMBER, roleQualifiers)) {
            orgPlantAccountReadOnlyAttributeSecurity.setReadOnly(false);
        }

        return readOnlyPropertyNames;
    }

    /**
     * This method checks whether the specified user is part of the group who can approve
     * at the campus chart level when the plant fund attributes are null.
     *
     * @param user
     * @parm propertyName
     * @param roleQualifiers
     * @return true if belongs to campus chart group else return false.
     */
    protected boolean isCampusChartManagerAuthorized(Person user, String propertyName, Map<String,String> roleQualifiers) {
        String principalId = user.getPrincipalId();
        String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
        String permissionTemplateName = KimConstants.PermissionTemplateNames.MODIFY_FIELD;

        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, Organization.class.getSimpleName());
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, propertyName);

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
        if (!isAuthorized) {
            return false;
        }

        return true;
    }

}
