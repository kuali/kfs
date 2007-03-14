/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import static org.kuali.kfs.util.SpringServiceLocator.getDisbursementVoucherTravelService;
import static org.kuali.rice.KNSServiceLocator.getDateTimeService;

import java.sql.Timestamp;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the DisbursementVoucherTravel service.
 * 
 * 
 */
@WithTestSpringContext
public class DisbursementVoucherTravelServiceTest extends KualiTestBase {
    private DisbursementVoucherDocument dvDocument;

    /**
     * Test calculation of per diem.
     * 
     * @throws Exception
     */
    public void testCalculatePerDiem() throws Exception {
        dvDocument = new DisbursementVoucherDocument();
        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 0:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(12.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 10:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 5:00 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(0));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 0:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(22.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:01 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(25.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 11:59 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(22.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 12:01 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(30.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 0:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(42.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 1:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 0:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(17.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 6:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 0:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(15.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 6:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(45.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(50.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 0:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(7.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 3:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 6:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(0));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 5:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 5:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(0.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 4:59 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 5:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(5.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 1:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 7:01 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(50), new KualiDecimal(37.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 11:59 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 6:00 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(0));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 6:01 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 5:59 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(5.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 3:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 6:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(10));

    }

    private void runPerDiemTest(Timestamp startTime, Timestamp endTime, KualiDecimal perDiemRate, KualiDecimal expectedPerDiemAmount) {
        assertEquals("Per diem amount not correct ", expectedPerDiemAmount, getDisbursementVoucherTravelService().calculatePerDiemAmount(startTime, endTime, perDiemRate));
    }

    /**
     * Tests the calculation of travel mileage amount. This is testing against the mileage rates defined currently, we need to find
     * a way to fix this for when they change:
     * 
     * 0-500 0.375 500-3000 0.18 3000 - 0
     * 
     * @throws Exception
     */
    public void testCalculateMileageAmount() throws Exception {
        Timestamp effectiveDate = getDateTimeService().getCurrentTimestamp();

        runMileageAmountTest(new Integer(0), new KualiDecimal(0), effectiveDate);
        runMileageAmountTest(new Integer(1), new KualiDecimal(.38), effectiveDate);
        runMileageAmountTest(new Integer(10), new KualiDecimal(3.75), effectiveDate);
        runMileageAmountTest(new Integer(15), new KualiDecimal(5.62), effectiveDate);
        runMileageAmountTest(new Integer(100), new KualiDecimal(37.5), effectiveDate);
        runMileageAmountTest(new Integer(200), new KualiDecimal(75.00), effectiveDate);
        runMileageAmountTest(new Integer(380), new KualiDecimal(142.5), effectiveDate);
        runMileageAmountTest(new Integer(500), new KualiDecimal(187.5), effectiveDate);

        runMileageAmountTest(new Integer(501), new KualiDecimal(187.68), effectiveDate);
        runMileageAmountTest(new Integer(600), new KualiDecimal(205.5), effectiveDate);
        runMileageAmountTest(new Integer(2500), new KualiDecimal(547.5), effectiveDate);
        runMileageAmountTest(new Integer(3000), new KualiDecimal(637.5), effectiveDate);

        runMileageAmountTest(new Integer(3001), new KualiDecimal(637.5), effectiveDate);
        runMileageAmountTest(new Integer(8000), new KualiDecimal(637.5), effectiveDate);
    }

    private void runMileageAmountTest(Integer totalMiles, KualiDecimal expectedMileageAmount, Timestamp effectiveDate) {
        assertEquals("Mileage amount not correct ", expectedMileageAmount, getDisbursementVoucherTravelService().calculateMileageAmount(totalMiles, effectiveDate));
    }


}
