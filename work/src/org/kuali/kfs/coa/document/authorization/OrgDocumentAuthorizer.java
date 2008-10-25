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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;

/**
 * Org/Organization specific authorization rules.
 */
public class OrgDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    private static final Logger LOG = Logger.getLogger(OrgDocumentAuthorizer.class);

    /**
     * Constructs a OrgDocumentAuthorizer.java.
     */
    public OrgDocumentAuthorizer() {
        super();
    }

    /**
     * This method returns the set of authorization restrictions (if any) that apply to this Org in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields read-only if necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, Person user) {

        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();

        // if the user is the system supervisor, then do nothing, dont apply
        // any restrictions
        if (org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().isMemberOfGroup(user, "KFS", org.kuali.rice.kns.service.KNSServiceLocator.getKualiConfigurationService().getParameterValue(org.kuali.rice.kns.util.KNSConstants.KNS_NAMESPACE, org.kuali.rice.kns.util.KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, org.kuali.rice.kns.util.KNSConstants.CoreApcParms.SUPERVISOR_WORKGROUP))) {
            return auths;
        }

        String groupName = SpringContext.getBean(ParameterService.class).getParameterValue(Org.class, KFSConstants.ChartApcParms.ORG_PLANT_WORKGROUP_PARM_NAME);

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

