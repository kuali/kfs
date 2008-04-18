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
package org.kuali.module.effort.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.document.EffortCertificationDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Business object for the outstanding documents by organization report
 */
public class OutstandingCertificationsByOrganization extends EffortCertificationDocument {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OutstandingCertificationsByOrganization.class);

    /**
     * gets a string representation of the document's chart and organization.
     * 
     * @return comma separated String
     */
    public String getCertificationOrganizations() {
        String certificationOrganizations = "";
        List<String> certificationOrganizationList = new ArrayList();
        List<EffortCertificationDetail> details = getEffortCertificationDetailLines();

        for (EffortCertificationDetail detailLine : details) {
            String accountOrg = detailLine.getAccount().getChartOfAccountsCode() + "-" + detailLine.getAccount().getOrganizationCode();
            if (!certificationOrganizationList.contains(accountOrg)) {
                certificationOrganizationList.add(accountOrg);
                if (certificationOrganizations.equals(""))
                    certificationOrganizations = accountOrg;
                else
                    certificationOrganizations = certificationOrganizations + ", " + accountOrg;
            }
        }

        return certificationOrganizations;
    }

    /**
     * Queries workflow to get users who have an approval request for this effort certification.
     * 
     * @return String - names of users (seperated by comma) who have an approval request
     */
    public String getNextApprovers() {
        String nextApprovers = "";

        try {
            List<String> approverUUIDs = SpringContext.getBean(KualiWorkflowInfo.class).getApprovalRequestedUsers(Long.valueOf(getDocumentHeader().getDocumentNumber()));
            for (String approverUUID : approverUUIDs) {
                UniversalUser approveUser = null;
                try {
                    approveUser = SpringContext.getBean(UniversalUserService.class).getUniversalUser(approverUUID);
                }
                catch (UserNotFoundException e) {
                    LOG.error("User information not found for UUID: " + approverUUID, e);
                }

                if (StringUtils.isBlank(nextApprovers)) {
                    nextApprovers = approveUser.getPersonName();
                }
                else {
                    nextApprovers += ", " + approveUser.getPersonName();
                }
            }
        }
        catch (WorkflowException e) {
            LOG.error("Problem getting next approver", e);
        }

        return nextApprovers;
    }
}
