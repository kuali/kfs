/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

import org.joda.time.DateTime;
import org.kuali.kfs.fp.businessobject.TravelMileageRate;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.dataaccess.TravelMileageRateDao;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the DisbursementVoucherTravel service.
 */
@ConfigureContext
public class DisbursementVoucherTravelServiceTest extends KualiTestBase {
    protected DisbursementVoucherDocument dvDocument;
    protected TravelMileageRateDao rateDao;
    protected UnitTestSqlDao sqlDao;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        rateDao = SpringContext.getBean(TravelMileageRateDao.class);
        dvDocument = new DisbursementVoucherDocument();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        setupMileageRates();
    }

    /**
     * Test calculation of per diem.
     *
     * @throws Exception
     */
    public void testCalculatePerDiem() throws Exception {

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 12:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(12.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 10:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 05:00 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), KualiDecimal.ZERO);

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 12:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(22.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:01 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(25.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 11:59 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(22.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 12:01 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(30.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 12:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(42.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 01:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 12:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(17.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 06:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/23/2006 12:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(15.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 06:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(45.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("12/28/2005 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("01/01/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(50.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 12:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 11:59 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(7.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 03:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 06:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), KualiDecimal.ZERO);

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 05:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 05:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), KualiDecimal.ZERO);

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 04:59 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 05:00 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(5.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 01:00 AM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/21/2006 07:01 PM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(50), new KualiDecimal(37.50));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 11:59 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 06:00 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), KualiDecimal.ZERO);

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 06:01 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 05:59 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(5.00));

        dvDocument.getDvNonEmployeeTravel().setPerDiemStartDateTime("04/21/2006 03:00 PM");
        dvDocument.getDvNonEmployeeTravel().setPerDiemEndDateTime("04/22/2006 06:01 AM");
        runPerDiemTest(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), new KualiDecimal(10), new KualiDecimal(10));

    }

    protected void runPerDiemTest(Timestamp startTime, Timestamp endTime, KualiDecimal perDiemRate, KualiDecimal expectedPerDiemAmount) {
        assertEquals("Per diem amount not correct ", expectedPerDiemAmount, SpringContext.getBean(DisbursementVoucherTravelService.class).calculatePerDiemAmount(startTime, endTime, perDiemRate));
    }

    /**
     * Tests the calculation of travel mileage amount. This is testing against the mileage rates defined currently, we need to find
     * a way to fix this for when they change: 0-500 0.375 500-3000 0.18 3000 - 0
     *
     * @throws Exception
     */
    public void testCalculateMileageAmount() throws Exception {
        Timestamp effectiveDate = new Timestamp( DateTime.parse("2004-01-01").toDateMidnight().toDate().getTime() );

        runMileageAmountTest(new Integer(0), KualiDecimal.ZERO, effectiveDate);
        runMileageAmountTest(new Integer(1), new KualiDecimal(.38), effectiveDate);
        runMileageAmountTest(new Integer(10), new KualiDecimal(3.75), effectiveDate);
        runMileageAmountTest(new Integer(15), new KualiDecimal(5.63), effectiveDate);
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

    public void testCalculateMileageAmount_2012_100miles() throws Exception {
        Timestamp effectiveDate = new Timestamp( DateTime.parse("2012-12-01").toDateMidnight().toDate().getTime() );

        runMileageAmountTest(100, KualiDecimal.ZERO, effectiveDate);
        runMileageAmountTest(500, KualiDecimal.ZERO, effectiveDate);
        runMileageAmountTest(501, new KualiDecimal("0.50"), effectiveDate);
    }


    protected void runMileageAmountTest(Integer totalMiles, KualiDecimal expectedMileageAmount, Timestamp effectiveDate) {
        assertEquals("Mileage amount not correct miles=" + totalMiles + " / date = " + effectiveDate, expectedMileageAmount, SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(totalMiles, effectiveDate));
    }

    protected TravelMileageRate createRate( DateTime effectiveDate, int mileageLimit, BigDecimal rateAmount ) {
        TravelMileageRate rate = new TravelMileageRate();
        rate.setDisbursementVoucherMileageEffectiveDate(new Date( effectiveDate.toDateMidnight().toDate().getTime() ));
        rate.setMileageLimitAmount(mileageLimit);
        rate.setMileageRate(rateAmount);
        return rate;
    }

    protected void setupMileageRates() {
        sqlDao.sqlCommand("DELETE FROM FP_DV_MLG_T");
        businessObjectService.save( createRate( DateTime.parse("2012-10-30"), 100, new BigDecimal("5") ) );
        businessObjectService.save( createRate( DateTime.parse("2012-10-25"), 500, new BigDecimal("0.5") ) );
        businessObjectService.save( createRate( DateTime.parse("2012-11-01"), 500, new BigDecimal("0.5") ) );
        businessObjectService.save( createRate( DateTime.parse("2003-07-01"), 0, new BigDecimal("0.375") ) );
        businessObjectService.save( createRate( DateTime.parse("2003-07-01"), 500, new BigDecimal("0.18") ) );
        businessObjectService.save( createRate( DateTime.parse("2003-07-01"), 3000, new BigDecimal("0") ) );
        businessObjectService.save( createRate( DateTime.parse("2012-10-29"), 500, new BigDecimal("0.5") ) );
    }

    public void testRetrievedRatesAllHaveSameDate_1() {
        Date travelStartDate = new Date( DateTime.parse("2012-12-01").toDateMidnight().toDate().getTime() );
        Collection<TravelMileageRate> rates = rateDao.retrieveMostEffectiveMileageRates( travelStartDate );
        assertFalse( "rates should have been returned", rates.isEmpty() );
        System.err.println( rates );
        Date returnedDate = rates.iterator().next().getDisbursementVoucherMileageEffectiveDate();
        for ( TravelMileageRate rate : rates ) {
            assertEquals( "All returned rates should have the same date: " + rate, returnedDate, rate.getDisbursementVoucherMileageEffectiveDate() );
        }
    }

    public void testRetrievedRatesAllHaveSameDate_2() {
        DateTime date = new DateTime(2004, 1, 1, 0, 0);
        Date travelStartDate = new Date( date.toDateMidnight().toDate().getTime() );
        Collection<TravelMileageRate> rates = rateDao.retrieveMostEffectiveMileageRates( travelStartDate );
        assertFalse( "rates should have been returned", rates.isEmpty() );
        System.err.println( rates );
        Date returnedDate = rates.iterator().next().getDisbursementVoucherMileageEffectiveDate();
        for ( TravelMileageRate rate : rates ) {
            assertEquals( "All returned rates should have the same date: " + rate, returnedDate, rate.getDisbursementVoucherMileageEffectiveDate() );
        }
    }
}
