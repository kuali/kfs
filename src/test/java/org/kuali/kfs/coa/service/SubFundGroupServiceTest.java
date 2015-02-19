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
