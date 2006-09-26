/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;

import java.util.ArrayList;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.CashDetailTypeCode;
import org.kuali.module.financial.service.impl.CashDetailTypeCodeServiceImpl;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the CashDetailTypeCode service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class CashDetailTypeCodeServiceTest extends KualiTestBaseWithFixtures {
    private ArrayList validCashDetailTypeCodes;

    private CashDetailTypeCodeService cashDetailTypeCodeService;

    protected void setUp() throws Exception {
        super.setUp();
        this.cashDetailTypeCodeService = SpringServiceLocator.getCashDetailTypeCodeService();
        // this.validCashDetailTypeCodes = (ArrayList)
        // SpringServiceLocator.getBusinessObjectService().findAll(CashDetailTypeCodeService.class);
        // TODO - uncomment the above line of code when the database table for cash detail type codes is put in place and populated;
        // remove the line below
        populateValidCashDetailTypeCodes();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetCashReceiptTypeCode() {
        assertEquals(true, validCashDetailTypeCodes.contains(cashDetailTypeCodeService.getCashReceiptCheckTypeCode()));
    }

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
