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
package org.kuali.kfs.fp.service;

import java.util.ArrayList;

import org.kuali.kfs.fp.businessobject.CashDetailTypeCode;
import org.kuali.kfs.fp.service.impl.CashDetailTypeCodeServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the CashDetailTypeCode service.
 */
@ConfigureContext
public class CashDetailTypeCodeServiceTest extends KualiTestBase {
    private ArrayList validCashDetailTypeCodes;

    /**
     * This method performs all the setup steps necessary to run the tests within this test case.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this.validCashDetailTypeCodes = (ArrayList)
        // SpringContext.getBean(BusinessObjectService.class).findAll(CashDetailTypeCode.class);
        // TODO - uncomment the above line of code when the database table for cash detail type codes is put in place and populated;
        // remove the line below
        populateValidCashDetailTypeCodes();
    }

    /**
     * 
     * This method tests that the getter for a CashDetailTypeCode works properly and is capable of retrieving a 
     * valid CashDetailTypeCode object.
     */
    public void testGetCashReceiptTypeCode() {
        assertEquals(true, validCashDetailTypeCodes.contains(SpringContext.getBean(CashDetailTypeCodeService.class).getCashReceiptCheckTypeCode()));
    }

    /**
     * 
     * This method creates a dummy CashDetailTypeCode object instance using the value given as the code to assign to the dummy.
     * @param cashDetailTypeCodeCode The code to be assigned to the dummy instance.
     * @return A dummy instance of a CashDetailTypeCode object with the code set to the value provided.
     */
    private CashDetailTypeCode getDummyInstance(String cashDetailTypeCodeCode) {
        CashDetailTypeCode cashDetailTypeCode = new CashDetailTypeCode();
        cashDetailTypeCode.setCode(cashDetailTypeCodeCode);
        return cashDetailTypeCode;
    }

    /**
     * TODO Remove this once the database table is in place.
     */
    private void populateValidCashDetailTypeCodes() {
        this.validCashDetailTypeCodes = new ArrayList();
        this.validCashDetailTypeCodes.add(getDummyInstance(CashDetailTypeCodeServiceImpl.CASH_RECEIPT_CHECK));
    }
}
