/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.authorization;

import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class...
 */
public class SubAccountDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * Constructs a SubAccountDocumentAuthorizer.java.
     */
    public SubAccountDocumentAuthorizer() {
    }

    /**
     * This method returns the set of authorization restrictions (if any) that apply to this SubAccount in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} with certain fields marked read-only if necessary
     */
    @Override
    public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document, Person user) {

        // if the user is the system supervisor, then do nothing, dont apply
        // any restrictions
        if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, org.kuali.rice.kns.service.KNSServiceLocator.getKualiConfigurationService().getParameterValue(org.kuali.rice.kns.util.KNSConstants.KNS_NAMESPACE, org.kuali.rice.kns.util.KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, org.kuali.rice.kns.util.KNSConstants.CoreApcParms.SUPERVISOR_WORKGROUP))) {
            auths.clearAllRestrictions();
            return;
        }

        String groupName = SpringContext.getBean(ParameterService.class).getParameterValue(SubAccount.class, KFSConstants.ChartApcParms.SUBACCOUNT_CG_WORKGROUP_PARM_NAME);

        // create a new KimGroup instance with that name
        GroupService groupService = SpringContext.getBean(GroupService.class);
        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, groupName);
        if (group == null) {
            throw new RuntimeException("The group by name '" + groupName + "' was not " + "found in the GroupService.  This is a configuration error, and " + "authorization/business-rules cannot be processed without this.");
        }

        // if the user is NOT a member of the special group, then mark all the
        // ICR & CS fields read-only.
        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), group.getGroupId())) {
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
    }

    /**
     * Adds in a can blanket approve flag for Sub Accounts if the workflow document state is not canceled
     * 
     * @see org.kuali.rice.kns.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemDocumentActionFlags documentActionFlags = super.getDocumentActionFlags(document, user);
        // KULRNE-44: even if some fields are readonly to the user, we allow him to blanket approve
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsCanceled()) {
            documentActionFlags.setCanBlanketApprove(workflowDocument.isBlanketApproveCapable());
        }
        return documentActionFlags;
    }
}

