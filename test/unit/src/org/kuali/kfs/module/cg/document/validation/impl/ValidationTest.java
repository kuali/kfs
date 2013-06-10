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
package org.kuali.kfs.module.cg.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.businessobject.MilestoneSchedule;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests some of the validation Rule classes
 */
@ConfigureContext(session = khuntley)
public class ValidationTest extends KualiTestBase {

    private BusinessObjectService boService;
    private Long proposalNumber;
    private MilestoneSchedule mSchedule;

    public void setUp() throws Exception {
        proposalNumber = new Long(39928);
        boService = SpringContext.getBean(BusinessObjectService.class);
        mSchedule = boService.findBySinglePrimaryKey(MilestoneSchedule.class, proposalNumber);
    }

    public void testCheckIfMilestonesExists() {
        assertFalse(MilestoneScheduleRuleUtil.checkIfMilestonesExists(mSchedule));
    }
}
