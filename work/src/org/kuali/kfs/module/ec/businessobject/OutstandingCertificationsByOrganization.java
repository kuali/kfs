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
package org.kuali.kfs.module.ec.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.ParameterConstants.COMPONENT;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

/**
 * Business object for the outstanding documents by organization report
 */
@COMPONENT(component="OutstandingCertificationsByOrganization")
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
                Person approveUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(approverUUID);
                if (approveUser == null) {
                    LOG.error("User information not found for UUID: " + approverUUID);
                }
                if (StringUtils.isBlank(nextApprovers)) {
                    nextApprovers = approveUser.getName();
                }
                else {
                    nextApprovers += "; " + approveUser.getName();
                }
            }
        }
        catch (WorkflowException e) {
            LOG.error("Problem getting next approver", e);
        }

        return nextApprovers;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof OutstandingCertificationsByOrganization) {
            List<String> keyFields = new ArrayList<String>();
            keyFields.add(KFSPropertyConstants.DOCUMENT_NUMBER);

            ObjectUtil.equals(this, arg0, keyFields );
        }
        
        return false;
    }
}

