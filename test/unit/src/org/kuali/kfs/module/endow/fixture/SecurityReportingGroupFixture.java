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
