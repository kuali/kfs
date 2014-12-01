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
package org.kuali.kfs.module.ar.document;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.GlobalVariables;

public class OrganizationOptionsMaintainableImpl extends FinancialSystemMaintainable {

    private OrganizationOptions newOptions;
    private OrganizationOptions oldOptions;

    /**
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();
        maintenanceLocks.addAll(CustomerInvoiceItemCodeMaintainableImplUtil.generateCustomerInvoiceItemCodeMaintenanceLocks(getBusinessObject(), getDocumentNumber()));
        return maintenanceLocks;
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    public void processAfterNew(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.processAfterNew(document, parameters);
        
        initializeAttributes(document);
        //document.getNewMaintainableObject().setGenerateDefaultValues(false);
		
        Person finSysUser = GlobalVariables.getUserSession().getPerson();
        
        String chartAccountsCode = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(finSysUser, ArConstants.AR_NAMESPACE_CODE).getChartOfAccountsCode();
        newOptions.setProcessingChartOfAccountCode(chartAccountsCode);
        
        String organizationCode = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(finSysUser, ArConstants.AR_NAMESPACE_CODE).getOrganizationCode();
        newOptions.setProcessingOrganizationCode(organizationCode);
        
        updateRemitToAddress(chartAccountsCode, organizationCode);
    }
    
    @Override
	public void setGenerateBlankRequiredValues(String docTypeName) {		
		
	}
    
    /**
     * This method gets old and new maintainable objects and creates convenience handles to them
     * 
     * @param document OrganizationOptions document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newOptions == null) {
            newOptions = (OrganizationOptions) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldOptions == null) {
            oldOptions = (OrganizationOptions) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        this.initializeAttributes(document);
        
        // Handle refreshing remit to address if processing chart and org have changed
        String chartCode = (String)fieldValues.get(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE);
        String orgCode = (String)fieldValues.get(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE);
        if(chartCode!=null && orgCode!=null) {
            updateRemitToAddress(chartCode, orgCode);
        }
    }

    /**
     * 
     * This method...
     * @param chartCode
     * @param orgCode
     */
    private void updateRemitToAddress(String chartCode, String orgCode) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        SystemInformation sysInfo = SpringContext.getBean(SystemInformationService.class).getByProcessingChartOrgAndFiscalYear(chartCode, orgCode, universityDateService.getCurrentFiscalYear());
        
        if(sysInfo != null) {
            newOptions.setOrganizationRemitToAddressName(sysInfo.getOrganizationRemitToAddressName());
            newOptions.setOrganizationRemitToLine1StreetAddress(sysInfo.getOrganizationRemitToLine1StreetAddress());
            newOptions.setOrganizationRemitToLine2StreetAddress(sysInfo.getOrganizationRemitToLine2StreetAddress());
            newOptions.setOrganizationRemitToCityName(sysInfo.getOrganizationRemitToCityName());
            newOptions.setOrganizationRemitToStateCode(sysInfo.getOrganizationRemitToStateCode());
            newOptions.setOrganizationRemitToZipCode(sysInfo.getOrganizationRemitToZipCode());
            // Add message here to notify user that remit to address has been updated
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.OrganizationOptionsErrors.SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG, new String[] {chartCode, orgCode});
        }
    }

}

