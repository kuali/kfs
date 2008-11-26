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
package org.kuali.kfs.fp.document.service.impl;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;

public class DisbursementVoucherWorkGroupServiceImpl implements DisbursementVoucherWorkGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherWorkGroupServiceImpl.class);
    
    public ParameterService parameterService;
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isDvGroupMemeber(java.lang.String, java.lang.String)
     */
    public boolean isDvGroupMemeber(String principalId, String groupTypeName) {
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, KFSConstants.KFS_GROUP_NAMESPACE, groupTypeName);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isUserInTaxGroup(org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInTaxGroup(Person financialSystemUser) {
        String taxGroupName = parameterService.getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP);
        
        return this.isDvGroupMemeber(financialSystemUser.getPrincipalId(), taxGroupName);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isUserInTravelGroup(org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInTravelGroup(Person financialSystemUser) {
        String travelGroupName = parameterService.getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TRAVEL_WORKGROUP);
        
        return this.isDvGroupMemeber(financialSystemUser.getPrincipalId(), travelGroupName);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isUserInFRNGroup(org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInFRNGroup(Person financialSystemUser) {
        String frnGroupName = parameterService.getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_FOREIGNDRAFT_WORKGROUP);

        return this.isDvGroupMemeber(financialSystemUser.getPrincipalId(), frnGroupName);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isUserInWireGroup(org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInWireGroup(Person financialSystemUser) {
        String wireTransferGroupName = parameterService.getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_WIRETRANSFER_WORKGROUP);

        return this.isDvGroupMemeber(financialSystemUser.getPrincipalId(), wireTransferGroupName);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService#isUserInDvAdminGroup(org.kuali.rice.kim.bo.Person)
     */
    public boolean isUserInDvAdminGroup(Person financialSystemUser) {
        String adminGroupName = parameterService.getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_ADMIN_WORKGROUP);
        
        return this.isDvGroupMemeber(financialSystemUser.getPrincipalId(), adminGroupName);
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
