/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
