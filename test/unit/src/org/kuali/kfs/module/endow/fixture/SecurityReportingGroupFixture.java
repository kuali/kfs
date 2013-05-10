/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum SecurityReportingGroupFixture {

    REPORTING_GROUP("TST12",// code
            "Test reporting group",// desc
            1,// securityReportingGrpOrder
            true // active
    ),        
    REPORTING_GROUP_COMMITTED("12TST",// code
                    "Test reporting group",// desc
                    1,// securityReportingGrpOrder
                    true // active            
    );

    public final String code;
    public final String desc;
    public final Integer securityReportingGrpOrder;
    public final boolean active;

    private SecurityReportingGroupFixture(String code, String desc, Integer securityReportingGrpOrder, boolean active) {
        this.code = code;
        this.desc = desc;
        this.securityReportingGrpOrder = securityReportingGrpOrder;
        this.active = active;
    }

    /**
     * This method creates a SecurityReportingGroup record and saves it to the DB table.
     * 
     * @return SecurityReportingGroup
     */
    public SecurityReportingGroup createSecurityReportingGroup() {
        SecurityReportingGroup reportingGroup = new SecurityReportingGroup();

        reportingGroup.setCode(this.code);
        reportingGroup.setName(this.desc);
        reportingGroup.setSecurityReportingGrpOrder(this.securityReportingGrpOrder);
        reportingGroup.setActive(this.active);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(reportingGroup);

        return reportingGroup;
    }
}
