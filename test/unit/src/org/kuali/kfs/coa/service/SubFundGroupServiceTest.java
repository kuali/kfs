/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the subFundGroup service.
 */
@ConfigureContext
public class SubFundGroupServiceTest extends KualiTestBase {


    public final void testGetByCode_knownCode() {
        // known-good code
        SubFundGroup subFundGroup = SpringContext.getBean(SubFundGroupService.class).getByPrimaryId("LOANFD");
        assertEquals("Known code does not produce expected name.", "LOAN FUNDS", subFundGroup.getSubFundGroupDescription());
    }

    public final void testGetByCode_knownCode2() {
        // known-good code
        SubFundGroup subFundGroup = SpringContext.getBean(SubFundGroupService.class).getByPrimaryId("CLEAR");
        assertEquals("Known code does not produce expected name.", "CLEARING AND ROTATING FUNDS", subFundGroup.getSubFundGroupDescription());
    }

    public final void testGetByCode_unknownCode() {
        // known-bad code
        SubFundGroup subFundGroup = SpringContext.getBean(SubFundGroupService.class).getByPrimaryId("SMELL");
        assertNull("Known-bad code does not produce expected null object.", subFundGroup);
    }

    public final void testGetByChartAndAccount() {
        String chartCode = "BL";
        String accountNumber = "1031420";
        SubFundGroup subFundGroup = SpringContext.getBean(SubFundGroupService.class).getByChartAndAccount(chartCode, accountNumber);
        assertNotNull(subFundGroup);
        assertEquals("Foo", "GENFND", subFundGroup.getSubFundGroupCode());
    }

    public final void testGetByName_knownName() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-good name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "LOAN FUNDS");
        // assertEquals("Known code does not produce expected name.", "LOANFD", subFundGroup.getCode());
    }

    public final void testGetByName_knownName2() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-good name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "CLEARING AND ROTATING FUNDS");
        // assertEquals("Known code does not produce expected name.", "CLEAR", subFundGroup.getCode());
        // assertEquals("Known code's active indicator conversion failed.", true, subFundGroup.isActive());
        // assertEquals("Known code's wage indicator conversion failed.", false, subFundGroup.isWageIndicator());
    }

    public final void testGetByName_unknownName() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-bad name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "Smelly Cat");
        // assertNull("Known-bad name does not produce expected null object.", subFundGroup);
    }
}
