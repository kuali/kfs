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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests some of the validation Rule classes
 */
@ConfigureContext(session = khuntley)
public class MilestoneScheduleRuleUtilTest extends KualiTestBase {

    private BusinessObjectService boService;
    private Long proposalNumber;
    private MilestoneSchedule mSchedule;

    @Override
    public void setUp() throws Exception {
        proposalNumber = new Long(39928);
        boService = SpringContext.getBean(BusinessObjectService.class);
        mSchedule = boService.findBySinglePrimaryKey(MilestoneSchedule.class, proposalNumber);
    }

    public void testCheckIfMilestonesExists() {
        assertFalse(MilestoneScheduleRuleUtil.checkIfMilestonesExists(mSchedule));
    }
}
