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
package org.kuali.workflow.module.purap.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * A document should stop in the Separation of Duties Route Node if the following two conditions are met:
 * <ol>
 * <li>The total amount of the document is greater or equal to the Separation of Duties application parameter amount
 * <li>The document was not routed for Approval or Completion to more than the user who routed the document
 * </ol>
 * In Workflow the rule above means any of the following scenarios do not require Separation of Duties routing (assuming that the
 * routing of the document still applies as an Action Taken of COMPLETE):<br><br>
 * <ol>
 * <li>The total number of unique people who have had an APPROVE or COMPLETE action on the document is more than 1 (keep in mind
 * that the 'Route' action on a document generates a COMPLETE action taken)
 * <li>The total amount of the document is less than or equal to the Separation of Duties application parameter amount
 * </ol>
 * EPIC RULE (these cases will route to separation of duties group):<br>
 *  - (totalCost > appSettingCost) && (0 approvals taken on the document)<br>
 *  - (totalCost > appSettingCost) && (one approval taken on the document) && (approval is by initiator user)
 */
public class KualiSeparationOfDutiesRoleAttribute extends UnqualifiedRoleAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiSeparationOfDutiesRoleAttribute.class);    
    
    private static final String SEPARATION_OF_DUTIES_ROLE_KEY = "SEPARATION_OF_DUTIES";
    private static final String SEPARATION_OF_DUTIES_ROLE_LABEL = "Separation of Duties Reviewer";

    private static final Role ROLE = new Role(KualiSeparationOfDutiesRoleAttribute.class, SEPARATION_OF_DUTIES_ROLE_KEY, SEPARATION_OF_DUTIES_ROLE_LABEL);
    private static final List<Role> ROLES;
    static {
        ArrayList<Role> roles = new ArrayList<Role>(1);
        roles.add(ROLE);
        ROLES = Collections.unmodifiableList(roles);
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute#getRoleNames()
     */
    @Override
    public List<Role> getRoleNames() {
        return ROLES;
    }

    /**
     * TODO delyea - documentation
     * 
     * @param routeContext the RouteContext
     * @param roleName the role name
     * @return a ResolvedQualifiedRole
     */
    @Override
    // TODO delyea - edit for REPORT functionality
    public ResolvedQualifiedRole resolveRole(RouteContext routeContext, String roleName) throws EdenUserNotFoundException {
        DocumentRouteHeaderValue document = routeContext.getDocument();
        Set documentReviewers = new HashSet();
        // get a list of all people who have approved or completed this document in it's routing lifespan
        // currently this will get the COMPLETE request that the router of the document
        for (Iterator iter = document.getActionsTaken().iterator(); iter.hasNext();) {
            ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
            if ( (actionTaken.getActionTaken().equals(EdenConstants.ACTION_TAKEN_APPROVED_CD)) || 
                 (actionTaken.getActionTaken().equals(EdenConstants.ACTION_TAKEN_COMPLETED_CD)) ) {
                documentReviewers.add(actionTaken.getWorkflowUser().getWorkflowUserId().getWorkflowId());
            }
        }
        // if document has been approved or completed by more than one person... no need for separation of duties
        if (documentReviewers.size() > 1) {
            return null;
        }
        KualiConfigurationService configService = SpringContext.getBean(KualiConfigurationService.class);
        KualiDecimal maxAllowedAmount = new KualiDecimal(configService.getParameterValue(KFSConstants.PURAP_NAMESPACE,PurapConstants.Components.REQUISITION, PurapParameterConstants.WorkflowParameters.RequisitionDocument.SEPARATION_OF_DUTIES_DOLLAR_AMOUNT));
        // if app param amount is greater than or equal to documentTotalAmount... no need for separation of duties
        KualiDecimal totalAmount = KualiWorkflowUtils.getFinancialDocumentTotalAmount(routeContext);
        if ( ObjectUtils.isNotNull(maxAllowedAmount) && ObjectUtils.isNotNull(totalAmount) && (maxAllowedAmount.compareTo(totalAmount) >= 0)) {
            return null;
        }
        String workgroupName = configService.getParameterValue(KFSConstants.PURAP_NAMESPACE,PurapConstants.Components.REQUISITION, PurapParameterConstants.WorkflowParameters.RequisitionDocument.SEPARATION_OF_DUTIES_WORKGROUP_NAME);
        return new ResolvedQualifiedRole(SEPARATION_OF_DUTIES_ROLE_LABEL, Arrays.asList(new Id[] {new GroupNameId(workgroupName)}));
    }
}
