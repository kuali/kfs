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
package org.kuali.module.chart.document;

import org.apache.log4j.Logger;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * Org/Organization specific authorization rules.
 * 
 * 
 */
public class OrgDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    private static final Logger LOG = Logger.getLogger(OrgDocumentAuthorizer.class);

    /**
     * Constructs a OrgDocumentAuthorizer.java.
     */
    public OrgDocumentAuthorizer() {
        super();
    }

    /**
     * 
     * This method returns the set of authorization restrictions (if any) that apply to this Org in this context.
     * 
     * @param document
     * @param user
     * @return
     * 
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {

        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();

        // if the user is the system supervisor, then do nothing, dont apply
        // any restrictions
        if (user.isSupervisorUser()) {
            return auths;
        }

        // get the group name that we need here - ORG
        KualiConfigurationService configService;
        configService = SpringContext.getBean(KualiConfigurationService.class);
        String groupName = configService.getParameterValue(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ORGANIZATION,
                KFSConstants.ChartApcParms.ORG_PLANT_WORKGROUP_PARM_NAME);

        // if the user is NOT a member of the special group, then mark all the
        // ICR & CS fields read-only.
        if (!user.isMember(groupName)) {
            auths.addReadonlyAuthField("organizationPlantChartCode");
            auths.addReadonlyAuthField("organizationPlantAccountNumber");
            auths.addReadonlyAuthField("campusPlantChartCode");
            auths.addReadonlyAuthField("campusPlantAccountNumber");
        }
        return auths;
    }
}
