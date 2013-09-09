/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

/**
 * This class is used to ensure that default values are set accordingly if blank
 */
public class OrganizationOptionsPreRules extends PromptBeforeValidationBase {

    @Override
    public boolean doPrompts(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        OrganizationOptions organizationOptions = (OrganizationOptions) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        String institutionName = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME);

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
