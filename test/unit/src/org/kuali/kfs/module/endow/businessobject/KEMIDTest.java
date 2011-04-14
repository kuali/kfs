/*
 * Copyright 2011 The Kuali Foundation.
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