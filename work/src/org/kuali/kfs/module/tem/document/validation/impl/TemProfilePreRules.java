/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class TemProfilePreRules extends MaintenancePreRulesBase {



    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        boolean askQuestion = true;

        TEMProfile profile = (TEMProfile) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        if (profile.getArrangers().size() > 0){
            for (TEMProfileArranger arranger : profile.getArrangers()){
                if (arranger.getActive()){
                    askQuestion = false;
                }
            }
        }

        TEMRoleService temRoleService = SpringContext.getBean(TEMRoleService.class);
        Collection<RoleMembership> members = temRoleService.getTravelArrangers(profile.getHomeDeptChartOfAccountsCode(), profile.getHomeDeptOrgCode());

        if (!members.isEmpty()) {
            askQuestion = false;
        }

        if (StringUtils.isNotEmpty(profile.getTravelerTypeCode()) && !profile.getTravelerTypeCode().equalsIgnoreCase(TemConstants.NONEMP_TRAVELER_TYP_CD) && askQuestion){
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_PROFILE_ARRANGERS_QUESTION);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(TemKeyConstants.GENERATE_TEM_PROFILE_ID_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return super.doCustomPreRules(maintenanceDocument);
    }

}
