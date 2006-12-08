/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/document/SubAccountDocumentAuthorizer.java,v $
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
package org.kuali.module.chart.document;

import org.kuali.Constants;
import org.kuali.core.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.SpringServiceLocator;

/**
 * This class...
 * 
 * 
 */
public class SubAccountDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * Constructs a SubAccountDocumentAuthorizer.java.
     */
    public SubAccountDocumentAuthorizer() {
    }

    /**
     * 
     * This method returns the set of authorization restrictions (if any) that apply to this SubAccount in this context.
     * 
     * @param document
     * @param user
     * @return
     * 
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {

        // if the user is the system supervisor, then do nothing, dont apply
        // any restrictions
        if (user.isSupervisorUser()) {
            return new MaintenanceDocumentAuthorizations();
        }

        // get the group name that we need here - CGSACCT
        KualiConfigurationService configService;
        configService = SpringServiceLocator.getKualiConfigurationService();
        String groupName = configService.getApplicationParameterValue(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, Constants.ChartApcParms.SUBACCOUNT_CG_WORKGROUP_PARM_NAME);

        // create a new KualiGroup instance with that name
        KualiGroupService groupService = SpringServiceLocator.getKualiGroupService();
        KualiGroup group = null;
        try {
            group = groupService.getByGroupName(groupName);
        }
        catch (GroupNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("The group by name '" + groupName + "' was not " + "found in the KualiGroupService.  This is a configuration error, and " + "authorization/business-rules cannot be processed without this.", e);
        }

        // if the user is NOT a member of the special group, then mark all the
        // ICR & CS fields read-only.
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        if (!user.isMember(group)) {
            auths.addReadonlyAuthField("a21SubAccount.subAccountTypeCode");
            auths.addReadonlyAuthField("a21SubAccount.costShareChartOfAccountCode");
            auths.addReadonlyAuthField("a21SubAccount.costShareSourceAccountNumber");
            auths.addReadonlyAuthField("a21SubAccount.costShareSourceSubAccountNumber");
            auths.addReadonlyAuthField("a21SubAccount.financialIcrSeriesIdentifier");
            auths.addReadonlyAuthField("a21SubAccount.indirectCostRecoveryChartOfAccountsCode");
            auths.addReadonlyAuthField("a21SubAccount.indirectCostRecoveryAccountNumber");
            auths.addReadonlyAuthField("a21SubAccount.indirectCostRecoveryTypeCode");
            auths.addReadonlyAuthField("a21SubAccount.offCampusCode");
        }

        return auths;
    }
}
