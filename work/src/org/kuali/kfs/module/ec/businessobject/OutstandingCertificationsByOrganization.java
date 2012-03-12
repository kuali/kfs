/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Business object for the outstanding documents by organization report
 */
@COMPONENT(component = "OutstandingCertificationsByOrganization")
public class OutstandingCertificationsByOrganization extends EffortCertificationDocument {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OutstandingCertificationsByOrganization.class);

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


        List<ActionItem> actionList = KewApiServiceLocator.getActionListService().getAllActionItems(getDocumentHeader().getDocumentNumber());
        for (ActionItem actionItem : actionList) {
            if (actionItem.getActionRequestCd().equals(KewApiConstants.ACTION_REQUEST_APPROVE_REQ)) {
                String principalId = actionItem.getPrincipalId();
                if (principalId != null) {
                    Person person = KimApiServiceLocator.getPersonService().getPerson(actionItem.getPrincipalId());
                    if (StringUtils.isBlank(nextApprovers)) {
                        nextApprovers = person.getName();
                    }
                    else {
                        nextApprovers += "; " + person.getName();
                    }

                }
            }
        }

        return nextApprovers;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof OutstandingCertificationsByOrganization) {
            List<String> keyFields = new ArrayList<String>();
            keyFields.add(KFSPropertyConstants.DOCUMENT_NUMBER);

            ObjectUtil.equals(this, arg0, keyFields);
        }

        return false;
    }
}
