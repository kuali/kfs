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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

public class OrganizationOptionsPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    protected static final String ACCOUNTS_RECEIVABLE_MANAGER_ROLE_NAME = "Accounts Receivable Manager";

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> readOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);
        setRemitToAddressSectionEditable(readOnlySectionIds);
        return readOnlySectionIds;
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        setRemitToNameEditable(readOnlyPropertyNames);
        setOrgPostalZipCodeEditable(readOnlyPropertyNames);
        setBillingOrgFieldsEditable(readOnlyPropertyNames, document);
        setProcessingOrgFieldsEditable(readOnlyPropertyNames, document);
        return readOnlyPropertyNames;
    }

    /**
     *
     * Billing Chart/Org are always read-only on an edit.  Always.
     *
     * They are editable on an Add, but only if KIM lets you in on an Add,
     * but thats handled elsewhere.
     *
     * @param readOnlyPropertyNames
     * @param document
     */
    protected void setBillingOrgFieldsEditable(Set<String> readOnlyPropertyNames, MaintenanceDocument document) {
        if (document.isEdit()) {
            readOnlyPropertyNames.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            readOnlyPropertyNames.add(KFSPropertyConstants.ORGANIZATION_CODE);
        }
    }

    /**
     * Sets the processing Chart/Org code editable
     *
     * @param readOnlyPropertyNames
     * @param document
     */
    protected void setProcessingOrgFieldsEditable(Set<String> readOnlyPropertyNames, MaintenanceDocument document) {

        if (document.isEdit()) {

            RoleService rms = KimApiServiceLocator.getRoleService();

            String principalId = GlobalVariables.getUserSession().getPrincipalId();

            List<String> roleIds = new ArrayList<String>();
            roleIds.add(rms.getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, ACCOUNTS_RECEIVABLE_MANAGER_ROLE_NAME));

            // editable only for the AR Manager role
            if (!rms.principalHasRole(principalId, roleIds, null)) {
                readOnlyPropertyNames.add(KFSPropertyConstants.PROCESSING_CHART_OF_ACCT_CD);
                readOnlyPropertyNames.add(KFSPropertyConstants.PROCESSING_ORGANIZATION_CODE);
            }
        }
    }

    /**
     *
     * Sets the Remit-To Name (ie, OrgCheckPayableToName) to read only if thats how the system parameters are
     * configured, otherwise leave it read/write.
     *
     * @param readOnlyPropertyNames
     */
    protected void setRemitToNameEditable(Set<String> readOnlyPropertyNames) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        if ( parameterService.getParameterValueAsBoolean(OrganizationOptions.class, ArConstants.REMIT_TO_NAME_EDITABLE_IND, Boolean.TRUE) ) { // defaulting to true to preserve prior behavior
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CHECK_PAYABLE_TO_NAME);
        }
    }

    /**
     *
     * Sets the OrgPostalZipCode to readonly if thats what the system parameters say, otherwise leave it
     * read/write.
     *
     * @param readOnlyPropertyNames
     */
    protected void setOrgPostalZipCodeEditable(Set<String> readOnlyPropertyNames) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        if (!service.getParameterValueAsBoolean(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND, Boolean.FALSE ) ){
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_POSTAL_ZIP_CODE);
        }
    }

    /**
     *
     * Sets the whole Remit-To Address section to read-only if thats what the system parameter says, otherwise leave
     * it read/wrtie.
     *
     * @param readOnlySectionIds
     */
    protected void setRemitToAddressSectionEditable(Set<String> readOnlySectionIds) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        String addressEditable = service.getParameterValueAsString(OrganizationOptions.class, ArConstants.REMIT_TO_ADDRESS_EDITABLE_IND);
        if ("N".equalsIgnoreCase(addressEditable)) {
            readOnlySectionIds.add(ArConstants.OrganizationOptionsSections.EDIT_ORGANIZATION_REMIT_TO_ADDRESS);
        }
    }

}
