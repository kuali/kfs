/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationOptions;

/**
 * This class is used to ensure that default values are set accordingly if blank
 */
public class OrganizationOptionsPreRules extends PreRulesContinuationBase { 

    @Override
    public boolean doRules(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        OrganizationOptions organizationOptions = (OrganizationOptions) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        String institutionName = SpringContext.getBean(ParameterService.class).getParameterValue(OrganizationOptions.class, ArConstants.INSTITUTION_NAME);

        // If university name is not provided, default to institution name
        if (StringUtils.isBlank(organizationOptions.getUniversityName())) {
            organizationOptions.setUniversityName(institutionName);
        }

        // If check payable name is not provided, default to institution name
        if (StringUtils.isBlank(organizationOptions.getOrganizationCheckPayableToName())) {
            organizationOptions.setOrganizationCheckPayableToName(institutionName);
        }

        return true;
    }
}
