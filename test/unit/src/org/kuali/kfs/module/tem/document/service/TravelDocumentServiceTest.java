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
package org.kuali.kfs.module.tem.document.service;

import static org.kuali.kfs.module.tem.TemConstants.LODGING_TOTAL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.MEALS_AND_INC_TOTAL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.MILEAGE_TOTAL_ATTRIBUTE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 *
 * This class tests the travelDocumentService class
 */
@ConfigureContext
public class TravelDocumentServiceTest extends KualiTestBase {

    private static final Logger LOG = Logger.getLogger(TravelDocumentServiceTest.class);

    protected final static String AIRFARE_EXPENSE_TYPE = "A";
    protected final static String MILEAGE_EXPENSE_TYPE = "MP";

    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private ExpenseTypeObjectCode mileageType = new ExpenseTypeObjectCode();
    private ExpenseTypeObjectCode airfareType = new ExpenseTypeObjectCode();

    private static final int ONE_DAY = 86400;
    private static final int EXPENSE_AMOUNT = 100;
    private static final int MILEAGE = 2;
    private static final int MILEAGE_RATE = 10;
    private static final String AIRLINE_EXPENSE_TYPE_CODE = "A";
    private static final String HOSTED_BREAKFAST = "HB";

    /**
     *
     * @see junit.framework.TestCase#setUp()
     * Sets up test parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mileageType.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
        airfareType.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE);

        final TravelDocumentService travelDocumentServiceTemp = SpringContext.getBean(TravelDocumentService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        travelDocumentService = (TravelDocumentService)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {TravelDocumentService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(method.getName().equals("newperDiemExpense")) {
                    PerDiemExpense perDiemExpense = new PerDiemExpense() {
                        @Override
                        public void refreshReferenceObject(String refObject) {
                            if(refObject.equals("perDiem")) {
                                PerDiem perDiem = new PerDiem();
                                perDiem.setBreakfast(new KualiDecimal(12));
                                perDiem.setLunch(new KualiDecimal(13));
                                perDiem.setDinner(new KualiDecimal(25));
                                perDiem.setIncidentals(new KualiDecimal(10));
                                this.setPerDiem(perDiem);
                            }
                        }
                        @Override
                        public MileageRate getMileageRate() {
                            MileageRate rate = new MileageRate();
                            rate.setRate(new BigDecimal(0.45));
                            rate.setExpenseTypeCode("MP");
                            return rate;
                        }
                    };
                    return perDiemExpense;
                }
                return method.invoke(travelDocumentServiceTemp, args);
            }
        });
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     *
     * This method tests calculateDailyTotal using a single per diem expense.
     */
    public final void testCalculateDailyTotal_oneDay() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };

        perDiemExpense.setProrated(false);
        perDiemExpense.setPersonal(false);

        perDiemExpense.setMiles(0);
        perDiemExpense.setBreakfast(false);
        perDiemExpense.setLunch(false);
        perDiemExpense.setDinner(false);
        Map<String, KualiDecimal> dailyTotal = travelDocumentService.calculateDailyTotal(perDiemExpense);

        assertEquals(KualiDecimal.ZERO, dailyTotal.get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotal.get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotal.get(MEALS_AND_INC_TOTAL_ATTRIBUTE));


        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(575.00));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        dailyTotal = travelDocumentService.calculateDailyTotal(perDiemExpense);
        assertEquals(new KualiDecimal(9), dailyTotal.get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(575.00), dailyTotal.get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60), dailyTotal.get(MEALS_AND_INC_TOTAL_ATTRIBUTE));

    }

    /**
     *
     * This method tests calculateDailyTotal using a single prorated per diem expense.
     */
    public final void testCalculateDailyTotal_oneDayProrate() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(575.00));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        perDiemExpense.setProrated(true);
        perDiemExpense.setPersonal(false);

        Map<String, KualiDecimal> dailyTotal = travelDocumentService.calculateDailyTotal(perDiemExpense);
        assertEquals(new KualiDecimal(9), dailyTotal.get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(575.00), dailyTotal.get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60 * .75), dailyTotal.get(MEALS_AND_INC_TOTAL_ATTRIBUTE));

    }

    /**
     *
     * This method tests calculateDailyTotals using multiple (3) per diem expenses.
     */
    public final void testCalculateDailyTotals_threeDays() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };


        perDiemExpense.setMiles(0);
        perDiemExpense.setBreakfast(false);
        perDiemExpense.setLunch(false);
        perDiemExpense.setDinner(false);

        Calendar cal = Calendar.getInstance();
        perDiemExpense.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
        perDiemExpenses.add(perDiemExpense);

        PerDiemExpense perDiemExpense2 = this.copyPerDiem(perDiemExpense);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));
        perDiemExpenses.add(perDiemExpense2);

        PerDiemExpense perDiemExpense3 = this.copyPerDiem(perDiemExpense2);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));
        perDiemExpenses.add(perDiemExpense3);

        List<Map<String, KualiDecimal>> dailyTotals = travelDocumentService.calculateDailyTotals(perDiemExpenses);

        assertEquals(KualiDecimal.ZERO, dailyTotals.get(0).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotals.get(0).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotals.get(0).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        perDiemExpense.setPersonal(false);

        perDiemExpense2.setMiles(30);
        perDiemExpense2.setLodging(new KualiDecimal(75.00));

        perDiemExpense2.setBreakfast(true);
        perDiemExpense2.setLunch(true);
        perDiemExpense2.setDinner(true);
        perDiemExpense2.setPersonal(false);

        perDiemExpense3.setMiles(40);
        perDiemExpense3.setLodging(new KualiDecimal(55.00));

        perDiemExpense3.setBreakfast(true);
        perDiemExpense3.setLunch(true);
        perDiemExpense3.setDinner(true);
        perDiemExpense3.setPersonal(false);

        List<PerDiemExpense> perDiemExpenses2 = new ArrayList<PerDiemExpense>();
        perDiemExpenses2.add(perDiemExpense);
        perDiemExpenses2.add(perDiemExpense2);
        perDiemExpenses2.add(perDiemExpense3);


        List<Map<String, KualiDecimal>> dailyTotals2 = travelDocumentService.calculateDailyTotals(perDiemExpenses2);

        assertEquals(new KualiDecimal(20 * 0.45), dailyTotals2.get(0).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(75), dailyTotals2.get(0).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60 * .75), dailyTotals2.get(0).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));


        assertEquals(new KualiDecimal(30 * 0.45), dailyTotals2.get(1).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(75), dailyTotals2.get(1).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60), dailyTotals2.get(1).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));


        assertEquals(new KualiDecimal(40 * 0.45), dailyTotals2.get(2).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(55), dailyTotals2.get(2).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60 * .75), dailyTotals2.get(2).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));
    }

    /**
     *
     * This method tests updatePerDiemItemsFor by adding a per diem to the list.
     */
    public final void testUpdatePerDiemExpenses_addDay() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };


        Calendar cal = Calendar.getInstance();
        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        perDiemExpense.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);

        PerDiemExpense perDiemExpense2 = this.copyPerDiem(perDiemExpense);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense2.setMiles(30);
        perDiemExpense2.setLodging(new KualiDecimal(75.00));

        perDiemExpense2.setBreakfast(true);
        perDiemExpense2.setLunch(true);
        perDiemExpense2.setDinner(true);

        PerDiemExpense perDiemExpense3 = this.copyPerDiem(perDiemExpense2);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense3.setMiles(40);
        perDiemExpense3.setLodging(new KualiDecimal(55.00));

        perDiemExpense3.setBreakfast(true);
        perDiemExpense3.setLunch(true);
        perDiemExpense3.setDinner(true);

        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
        perDiemExpenses.add(perDiemExpense);
        perDiemExpenses.add(perDiemExpense2);
        perDiemExpenses.add(perDiemExpense3);

        Timestamp startDate = perDiemExpense.getMileageDate();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Timestamp endDate = new Timestamp(cal.getTimeInMillis());

        TravelDocument td = new TravelAuthorizationDocument();
        td.setDocumentNumber("1");
        td.setPrimaryDestinationId(23242);
        travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, new Integer(1), startDate, endDate);

        assertEquals(4, perDiemExpenses.size());


    }

    /**
     *
     * This method tests updatePerDiemItemsFor for inserting a per diem into an existing list.
     */
    public final void testUpdatePerDiemExpenses_shiftAndAddADay() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };


        Date today = dateTimeService.getCurrentSqlDateMidnight();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        perDiemExpense.setMileageDate(new Timestamp(today.getTime()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);

        PerDiemExpense perDiemExpense2 = this.copyPerDiem(perDiemExpense);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense2.setMiles(30);
        perDiemExpense2.setLodging(new KualiDecimal(75.00));

        perDiemExpense2.setBreakfast(true);
        perDiemExpense2.setLunch(true);
        perDiemExpense2.setDinner(true);

        PerDiemExpense perDiemExpense3 = this.copyPerDiem(perDiemExpense2);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense3.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense3.setMiles(40);
        perDiemExpense3.setLodging(new KualiDecimal(55.00));

        perDiemExpense3.setBreakfast(true);
        perDiemExpense3.setLunch(true);
        perDiemExpense3.setDinner(true);

        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
        perDiemExpenses.add(perDiemExpense);
        perDiemExpenses.add(perDiemExpense2);
        perDiemExpenses.add(perDiemExpense3);

        Calendar moveBackOneDay = Calendar.getInstance();
        moveBackOneDay.setTime(perDiemExpense.getMileageDate());
        moveBackOneDay.add(Calendar.DAY_OF_MONTH, -1);
        Timestamp startDate = new Timestamp(moveBackOneDay.getTimeInMillis());
        Timestamp endDate = perDiemExpense2.getMileageDate();

        TravelDocument td = new TravelAuthorizationDocument();
        td.setDocumentNumber("1");
        td.setPrimaryDestinationId(23242);
        travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, 1, startDate, endDate);

        assertEquals(3, perDiemExpenses.size());
        assertEquals(startDate, perDiemExpenses.get(0).getMileageDate());
        assertEquals(endDate, perDiemExpenses.get(2).getMileageDate());
    }

    /**
     *
     * This method tests updatePerDiemItemsFor for inserting multiple per diem expenses (5).
     */
    public final void testUpdatePerDiemExpenses_emptyList() {
        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();

        Date today = dateTimeService.getCurrentSqlDateMidnight();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        cal.add(Calendar.DATE, 5);

        Timestamp endDate = new Timestamp(cal.getTimeInMillis());
        TravelDocument td = new TravelAuthorizationDocument();
        td.setDocumentNumber("1");
        td.setPrimaryDestinationId(23242);
        travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, 1, new Timestamp(today.getTime()), endDate);

        assertEquals(6, perDiemExpenses.size());
        assertEquals(today, perDiemExpenses.get(0).getMileageDate());
        assertEquals(endDate, perDiemExpenses.get(5).getMileageDate());
    }

    /**
     *
     * This method tests updatePerDiemItemsFor for removing a per diem expense from an existing list.
     */
    public final void testUpdatePerDiemExpenses_removeDay() {
        List<PerDiemExpense> perDiemExpenses = this.createAListOfPerDiems();
        Timestamp startDate = perDiemExpenses.get(0).getMileageDate();
        Timestamp endDate = perDiemExpenses.get(1).getMileageDate();

        TravelDocument td = new TravelAuthorizationDocument();
        td.setDocumentNumber("1");
        td.setPrimaryDestinationId(23242);
        travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, 1, startDate, endDate);

        assertEquals(2, perDiemExpenses.size());
        assertEquals(startDate, perDiemExpenses.get(0).getMileageDate());
        assertEquals(endDate, perDiemExpenses.get(1).getMileageDate());
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the top of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_topDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        Timestamp testDate1 = mileages.get(1).getMileageDate();
        Timestamp testDate2 = mileages.get(2).getMileageDate();

        mileages.get(0).setLodging(new KualiDecimal(25));

        travelDocumentService.copyDownPerDiemExpense(0, mileages);

        assertEquals(3, mileages.size());
        assertEquals(new KualiDecimal(25), mileages.get(1).getLodging());
        assertEquals(testDate1, mileages.get(1).getMileageDate());
        assertEquals(new KualiDecimal(25), mileages.get(2).getLodging());
        assertEquals(testDate2, mileages.get(2).getMileageDate());
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the middle of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_middleDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        Timestamp testDate1 = mileages.get(1).getMileageDate();
        Timestamp testDate2 = mileages.get(2).getMileageDate();

        mileages.get(0).setLodging(new KualiDecimal(25));

        mileages.get(1).setLodging(new KualiDecimal(50));
        mileages.get(1).setMiles(20);
        travelDocumentService.copyDownPerDiemExpense(1, mileages);

        assertEquals(3, mileages.size());
        assertEquals(new KualiDecimal(50), mileages.get(1).getLodging());
        assertEquals(testDate1, mileages.get(1).getMileageDate());
        assertEquals(new KualiDecimal(50), mileages.get(2).getLodging());
        assertEquals(new Integer(20), mileages.get(2).getMiles());
        assertEquals(testDate2, mileages.get(2).getMileageDate());
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the bottom of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_lastOneDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        Timestamp testDate1 = mileages.get(1).getMileageDate();
        Timestamp testDate2 = mileages.get(2).getMileageDate();

        mileages.get(0).setLodging(new KualiDecimal(25));

        mileages.get(1).setLodging(new KualiDecimal(50));
        mileages.get(1).setMiles(20);

        mileages.get(2).setLodging(new KualiDecimal(30));
        mileages.get(2).setMiles(10);
        travelDocumentService.copyDownPerDiemExpense(2, mileages);

        assertEquals(3, mileages.size());
        assertEquals(new KualiDecimal(50), mileages.get(1).getLodging());
        assertEquals(testDate1, mileages.get(1).getMileageDate());
        assertEquals(new KualiDecimal(30), mileages.get(2).getLodging());
        assertEquals(new Integer(10), mileages.get(2).getMiles());
        assertEquals(testDate2, mileages.get(2).getMileageDate());
    }

    /**
     *
     * This method copies per diem.
     * @param mileage
     * @return
     */
    private PerDiemExpense copyPerDiem(PerDiemExpense perDiemExpense) {
        PerDiemExpense currentPerDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };
        currentPerDiemExpense.setMiles(perDiemExpense.getMiles());
        currentPerDiemExpense.setLodging(perDiemExpense.getLodging());
        currentPerDiemExpense.setBreakfast(perDiemExpense.getBreakfast());
        currentPerDiemExpense.setLunch(perDiemExpense.getLunch());
        currentPerDiemExpense.setDinner(perDiemExpense.getDinner());
        currentPerDiemExpense.setMileageDate(perDiemExpense.getMileageDate());

        return currentPerDiemExpense;

    }

    /**
     *
     * This method creates a list of per diems.
     * @return
     */
    private List<PerDiemExpense> createAListOfPerDiems() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };


        Date today = dateTimeService.getCurrentSqlDateMidnight();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        perDiemExpense.setMileageDate(new Timestamp(today.getTime()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);

        PerDiemExpense perDiemExpense2 = this.copyPerDiem(perDiemExpense);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense2.setMiles(30);
        perDiemExpense2.setLodging(new KualiDecimal(75.00));

        perDiemExpense2.setBreakfast(true);
        perDiemExpense2.setLunch(true);
        perDiemExpense2.setDinner(true);

        PerDiemExpense perDiemExpense3 = this.copyPerDiem(perDiemExpense2);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        perDiemExpense3.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense3.setMiles(40);
        perDiemExpense3.setLodging(new KualiDecimal(55.00));

        perDiemExpense3.setBreakfast(true);
        perDiemExpense3.setLunch(true);
        perDiemExpense3.setDinner(true);

        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
        perDiemExpenses.add(perDiemExpense);
        perDiemExpenses.add(perDiemExpense2);
        perDiemExpenses.add(perDiemExpense3);

        return perDiemExpenses;
    }

    /**
     *
     * This method tests calculateMileage.
     */
    public final void testCalculateMileage_mileageRatePresent() {
        ActualExpense actualExpense = new ActualExpense() {
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }

        };

        actualExpense.setTravelExpenseTypeCode(mileageType);
        actualExpense.setMiles(50);
        actualExpense.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        ActualExpense actualExpense2 = new ActualExpense() {
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }
        };
        actualExpense2.setTravelExpenseTypeCode(mileageType);
        actualExpense2.setMiles(50);
        actualExpense2.setMileageOtherRate(new BigDecimal(0.50));
        actualExpense2.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        ActualExpense actualExpense3 = new ActualExpense() {
            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType airfareExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, AIRFARE_EXPENSE_TYPE);
                return airfareExpenseType;
            }
        };
        actualExpense3.setTravelExpenseTypeCode(airfareType);
        actualExpense3.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE);

        assertEquals(new KualiDecimal(actualExpense.getMiles() * 0.45), travelDocumentService.calculateMileage(actualExpense));
        assertEquals(new KualiDecimal(actualExpense2.getMiles() * 0.50), travelDocumentService.calculateMileage(actualExpense2));
        assertEquals(KualiDecimal.ZERO, travelDocumentService.calculateMileage(actualExpense3));
    }

    /**
     *
     * This method tests calculateExpenseAmountTotalForMileage.
     */
    public final void testCalculateExpenseAmountTotalForMileage() {
        ActualExpense actualExpense = new ActualExpense() {
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }
        };

        actualExpense.setTravelExpenseTypeCode(mileageType);
        actualExpense.setMiles(50);
        actualExpense.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        ActualExpense actualExpense2 = new ActualExpense() {
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }
        };
        actualExpense2.setTravelExpenseTypeCode(mileageType);
        actualExpense2.setMiles(50);
        actualExpense2.setMileageOtherRate(new BigDecimal(0.50));
        actualExpense2.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        ActualExpense actualExpense3 = new ActualExpense() {
            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType airfareExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, AIRFARE_EXPENSE_TYPE);
                return airfareExpenseType;
            }
        };
        actualExpense3.setTravelExpenseTypeCode(airfareType);
        actualExpense3.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE);

        List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();
        actualExpenses.add(actualExpense);
        actualExpenses.add(actualExpense2);
        actualExpenses.add(actualExpense3);

        travelDocumentService.calculateExpenseAmountTotalForMileage(actualExpenses);

        assertEquals(new KualiDecimal(actualExpense.getMiles() * 0.45), actualExpenses.get(0).getExpenseAmount());
        assertEquals(new KualiDecimal(actualExpense2.getMiles() * 0.50), actualExpenses.get(1).getExpenseAmount());
        assertEquals(KualiDecimal.ZERO, actualExpenses.get(2).getExpenseAmount());
    }

    /**
     *
     * This method tests calculateExpenseAmountTotalForMileage for parent record where expense amount = mileage.
     */
    public final void testCalculateExpenseAmountTotalForMileage2() {
        List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();

        ActualExpense actualExpense_1 = new ActualExpense() {
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }
        };
        ActualExpense actualExpense_2 = new ActualExpense(){
            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType airfareExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, AIRFARE_EXPENSE_TYPE);
                return airfareExpenseType;
            }
        };
        ActualExpense actualExpense_3 = new ActualExpense(){
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setId(1);
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);
                return rate;
            }

            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType mileageExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, MILEAGE_EXPENSE_TYPE);
                return mileageExpenseType;
            }
        };
        ActualExpense actualExpense_4 = new ActualExpense(){
            @Override
            public ExpenseType getExpenseType() {
                final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                final ExpenseType airfareExpenseType = boService.findBySinglePrimaryKey(ExpenseType.class, AIRFARE_EXPENSE_TYPE);
                return airfareExpenseType;
            }
        };
        ActualExpense actualExpense_5 = new ActualExpense();

        actualExpense_1.setId(new Long(1));
        actualExpense_1.setTravelExpenseTypeCode(mileageType);
        actualExpense_1.setExpenseAmount(new KualiDecimal(500.00));
        actualExpense_1.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        actualExpense_2.setId(new Long(2));
        actualExpense_2.setTravelExpenseTypeCode(airfareType);
        actualExpense_2.setExpenseAmount(new KualiDecimal(200.00));

        actualExpense_3.setId(new Long(3));
        actualExpense_3.setTravelExpenseTypeCode(mileageType);
        actualExpense_3.setExpenseAmount(new KualiDecimal(100.00));
        actualExpense_3.setExpenseParentId(new Long(1));
        actualExpense_3.setMileageOtherRate(new BigDecimal(5.0));
        actualExpense_3.setMiles(50);
        actualExpense_3.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE);

        actualExpense_4.setId(new Long(4));
        actualExpense_4.setTravelExpenseTypeCode(airfareType);
        actualExpense_4.setExpenseAmount(new KualiDecimal(10.00));
        actualExpense_4.setExpenseParentId(new Long(1));
        actualExpense_4.setMiles(100);
        actualExpense_4.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE);

        actualExpense_5.setId(new Long(5));
        actualExpense_5.setTravelExpenseTypeCode(airfareType);
        actualExpense_5.setExpenseAmount(new KualiDecimal(150.00));
        actualExpense_5.setExpenseParentId(new Long(2));

        actualExpenses.add(actualExpense_1);
        actualExpenses.add(actualExpense_2);
        actualExpenses.add(actualExpense_3);
        actualExpenses.add(actualExpense_4);
        actualExpenses.add(actualExpense_5);

        travelDocumentService.calculateExpenseAmountTotalForMileage(actualExpenses);

        assertEquals(new KualiDecimal(260.00), actualExpenses.get(0).getExpenseAmount());
        assertEquals(new KualiDecimal(200.00), actualExpenses.get(1).getExpenseAmount());
        assertEquals(new KualiDecimal(250.00), actualExpenses.get(2).getExpenseAmount());
        assertEquals(new KualiDecimal(10.00), actualExpenses.get(3).getExpenseAmount());
        assertEquals(new KualiDecimal(150.00), actualExpenses.get(4).getExpenseAmount());
    }

    /**
     *
     * This method tests isHostedMeal
     */
    @Test
    public void testIsHostedMeal() {
        boolean isHostedMeal = false;
        // test with null actualExpense
        try{
            isHostedMeal = travelDocumentService.isHostedMeal(null);
        }catch(NullPointerException e){
            LOG.warn("NPE.", e);
        }
        assertFalse(isHostedMeal);

        ActualExpense ote = new ActualExpense();
        ExpenseTypeObjectCode travelExpenseTypeCode = new ExpenseTypeObjectCode();

        // test with HOSTED_BREAKFAST travelExpenseTypeCode
        travelExpenseTypeCode.setExpenseTypeCode(HOSTED_BREAKFAST);
        ote.setTravelExpenseTypeCode(travelExpenseTypeCode);
        isHostedMeal = travelDocumentService.isHostedMeal(ote);
        assertTrue(isHostedMeal);

        // test with AIRLINE_EXPENSE_TYPE_CODE travelExpenseTypeCode
        travelExpenseTypeCode.setExpenseTypeCode(AIRLINE_EXPENSE_TYPE_CODE);
        isHostedMeal = travelDocumentService.isHostedMeal(ote);
        assertFalse(isHostedMeal);
    }

    /**
     *
     * This method tests calculateProratePercentage
     */
    @Test
    public void testCalculateProratePercentage() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        Calendar cal = Calendar.getInstance();
        perDiemExpense.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        perDiemExpense.setProrated(true);

        Integer proratePercentage = travelDocumentService.calculateProratePercentage(perDiemExpense, TemConstants.PERCENTAGE, perDiemExpense.getMileageDate());
        assertFalse(proratePercentage.equals(100));
    }

    /**
     *
     * This method tests calculatePerDiemPercentageFromTimestamp
     */
    @Test
    public void testCalculatePerDiemPercentageFromTimestamp() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public void refreshReferenceObject(String refObject) {
                if(refObject.equals("perDiem")) {
                    PerDiem perDiem = new PerDiem();
                    perDiem.setBreakfast(new KualiDecimal(12));
                    perDiem.setLunch(new KualiDecimal(13));
                    perDiem.setDinner(new KualiDecimal(25));
                    perDiem.setIncidentals(new KualiDecimal(10));
                    this.setPerDiem(perDiem);
                }
            }
            @Override
            public MileageRate getMileageRate() {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        Calendar cal = Calendar.getInstance();
        perDiemExpense.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);

        Integer perDiemPercentage = travelDocumentService.calculatePerDiemPercentageFromTimestamp(perDiemExpense, perDiemExpense.getMileageDate());
        assertFalse(perDiemPercentage.equals(100));
    }


    /**
     * This method tests {@link TravelDocumentService#checkNonEmployeeTravelerTypeCode(String)}
     */
    @Test
    public void testCheckNonEmployeeTravelerTypeCode() {
        assertFalse(travelDocumentService.checkNonEmployeeTravelerTypeCode(null));
        assertFalse(travelDocumentService.checkNonEmployeeTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD));
        assertTrue(travelDocumentService.checkNonEmployeeTravelerTypeCode(TemConstants.NONEMP_TRAVELER_TYP_CD));
    }

    /**
     * This method tests {@link TravelDocumentService#getAdvancesTotalFor(Integer)}
     */
    @Test
    public void testGetAdvancesTotalFor() {
        // test getAdvancesTotalFor for null value
        KualiDecimal advancesTotal = travelDocumentService.getAdvancesTotalFor(null);
        assertNotNull(advancesTotal);
        assertTrue(advancesTotal.isZero());

        // test getAdvancesTotalFor for non existent travelDocumentIdentifier
        advancesTotal = travelDocumentService.getAdvancesTotalFor(new TravelAuthorizationDocument());
        assertNotNull(advancesTotal);
        assertTrue(advancesTotal.isZero());
    }

   }
