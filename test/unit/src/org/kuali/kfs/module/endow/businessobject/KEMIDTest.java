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
package org.kuali.kfs.module.endow.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class KEMIDTest extends KualiTestBase {

    private KEMID kemid;
    private KEMID savedKemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        kemid = KemIdFixture.SAVE_KEMID_RECORD.createKemidRecord();
        savedKemid = KemIdFixture.SAVE_KEMID_RECORD.getSavedKEMID(kemid.getKemid());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testKIMEID() {
        assertEquals(kemid.getKemid(),savedKemid.getKemid());
    }
    
    public void testShortTitle() {
        assertEquals(kemid.getShortTitle(),savedKemid.getShortTitle());
    }
    
    public void testLongTitle() {
        assertEquals(kemid.getLongTitle(),savedKemid.getLongTitle());
    }

    public void testDateOpened() {
        assertEquals(kemid.getDateOpened(), savedKemid.getDateOpened());
    }

    public void testDateEstablished() {
        assertEquals(kemid.getDateEstablished(), savedKemid.getDateEstablished());
    }

    public void testTypeCode() {
        assertEquals(kemid.getTypeCode(),savedKemid.getTypeCode());
    }

    public void testPurposeCode() {
        assertEquals(kemid.getPurposeCode(),savedKemid.getPurposeCode());
    }

    public void testResponsibleAdminCode() {
        assertEquals(kemid.getResponsibleAdminCode(),savedKemid.getResponsibleAdminCode());
    }

    public void testTransactionRestrictionCode() {
        assertEquals(kemid.getTransactionRestrictionCode(),savedKemid.getTransactionRestrictionCode());
    }

    public void testCashSweepModelId() {
        assertEquals(kemid.getCashSweepModelId(),savedKemid.getCashSweepModelId());
    }

    public void testDormantIndicator() {
        assertEquals(kemid.isDormantIndicator(), savedKemid.isDormantIndicator());
    }

    public void testClose() {
        assertEquals(kemid.isClose(), savedKemid.isClose());
    }

    public void testClosedToKEMID() {
        assertEquals(kemid.getClosedToKEMID(),savedKemid.getClosedToKEMID());
    }

    public void testCloseCode() {
        assertEquals(kemid.getCloseCode(),savedKemid.getCloseCode());
    }

    public void testIncomeRestrictionCode() {
        assertEquals(kemid.getIncomeRestrictionCode(),savedKemid.getIncomeRestrictionCode());
    }

    public void testPrincipalRestrictionCode() {
        assertEquals(kemid.getPrincipalRestrictionCode(),savedKemid.getPrincipalRestrictionCode());
    }
      
}
