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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.document.web.struts.TravelReimbursementForm;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.MockWorkflowDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
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
        travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     *
     * This method tests calculateDailyTotal using a single per diem expense.
     */
    public final void testCalculateDailyTotal_oneDay() {
        TravelReimbursementDocument trDoc = new TravelReimbursementDocument();
        TravelFormBase form = new TravelReimbursementForm();
        form.setDocument(trDoc);
        KNSGlobalVariables.setKualiForm(form);

        PerDiemExpense perDiemExpense = new PerDiemExpense() {

            @Override
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
                MileageRate rate = new MileageRate();
                rate.setRate(new BigDecimal(0.45));
                rate.setExpenseTypeCode("MP");
                return rate;
            }
        };

        perDiemExpense.setProrated(false);
        //perDiemExpense.setPersonal(true);

        perDiemExpense.setMiles(0);
        perDiemExpense.setBreakfast(false);
        perDiemExpense.setLunch(false);
        perDiemExpense.setDinner(false);
        Map<String, KualiDecimal> dailyTotal = travelDocumentService.calculateDailyTotal(perDiemExpense);

        assertEquals(KualiDecimal.ZERO, dailyTotal.get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotal.get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(KualiDecimal.ZERO, dailyTotal.get(MEALS_AND_INC_TOTAL_ATTRIBUTE));

        perDiemExpense.setPersonal(false);
        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(575.00));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        perDiemExpense.setBreakfastValue(new KualiDecimal(12));
        perDiemExpense.setLunchValue(new KualiDecimal(13));
        perDiemExpense.setDinnerValue(new KualiDecimal(25));
        perDiemExpense.setIncidentalsValue(new KualiDecimal(10));

        dailyTotal = travelDocumentService.calculateDailyTotal(perDiemExpense);
        assertEquals(new KualiDecimal(9), dailyTotal.get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(575.00), dailyTotal.get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60), dailyTotal.get(MEALS_AND_INC_TOTAL_ATTRIBUTE));

    }



    /**
     *
     * This method tests calculateDailyTotals using multiple (3) per diem expenses.
     */
    public final void testCalculateDailyTotals_threeDays() {
        TravelReimbursementDocument trDoc = new TravelReimbursementDocument();
        TravelFormBase form = new TravelReimbursementForm();
        form.setDocument(trDoc);
        KNSGlobalVariables.setKualiForm(form);

        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
          public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
        //perDiemExpense.setMileageRateExpenseType(rate);

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

        perDiemExpense.setBreakfastValue(new KualiDecimal(12));
        perDiemExpense.setLunchValue(new KualiDecimal(13));
        perDiemExpense.setDinnerValue(new KualiDecimal(25));
        perDiemExpense.setIncidentalsValue(new KualiDecimal(10));

        perDiemExpense2.setMiles(30);
        perDiemExpense2.setLodging(new KualiDecimal(75.00));

        perDiemExpense2.setBreakfast(true);
        perDiemExpense2.setLunch(true);
        perDiemExpense2.setDinner(true);
        perDiemExpense2.setPersonal(false);
        perDiemExpense2.setBreakfastValue(new KualiDecimal(12));
        perDiemExpense2.setLunchValue(new KualiDecimal(13));
        perDiemExpense2.setDinnerValue(new KualiDecimal(25));
        perDiemExpense2.setIncidentalsValue(new KualiDecimal(10));

        perDiemExpense3.setMiles(40);
        perDiemExpense3.setLodging(new KualiDecimal(55.00));

        perDiemExpense3.setBreakfast(true);
        perDiemExpense3.setLunch(true);
        perDiemExpense3.setDinner(true);
        perDiemExpense3.setPersonal(false);
        perDiemExpense3.setBreakfastValue(new KualiDecimal(12));
        perDiemExpense3.setLunchValue(new KualiDecimal(13));
        perDiemExpense3.setDinnerValue(new KualiDecimal(25));
        perDiemExpense3.setIncidentalsValue(new KualiDecimal(10));

        List<PerDiemExpense> perDiemExpenses2 = new ArrayList<PerDiemExpense>();
        perDiemExpenses2.add(perDiemExpense);
        perDiemExpenses2.add(perDiemExpense2);
        perDiemExpenses2.add(perDiemExpense3);


        List<Map<String, KualiDecimal>> dailyTotals2 = travelDocumentService.calculateDailyTotals(perDiemExpenses2);

        assertEquals(new KualiDecimal(20 * 0.45), dailyTotals2.get(0).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(75), dailyTotals2.get(0).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60 ), dailyTotals2.get(0).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));


        assertEquals(new KualiDecimal(30 * 0.45), dailyTotals2.get(1).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(75), dailyTotals2.get(1).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60), dailyTotals2.get(1).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));


        assertEquals(new KualiDecimal(40 * 0.45), dailyTotals2.get(2).get(MILEAGE_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(55), dailyTotals2.get(2).get(LODGING_TOTAL_ATTRIBUTE));
        assertEquals(new KualiDecimal(60 ), dailyTotals2.get(2).get(MEALS_AND_INC_TOTAL_ATTRIBUTE));
    }

    /**
     *
     * This method tests updatePerDiemItemsFor by adding a per diem to the list.
     */
    public final void testUpdatePerDiemExpenses_addDay() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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

        setDocumentHeader(td);
        travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, new Integer(1), startDate, endDate);

        assertEquals(4, perDiemExpenses.size());
    }

    protected void setDocumentHeader(TravelDocument td) {
        WorkflowDocument workflowDocument = new MockWorkflowDocument() {
            public boolean isStandardSaveAllowed() {
                return false;
            }

            @Override
            public boolean isInitiated() {
                return false;
            }

            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean isEnroute() {
                return true;
            }

            public boolean userIsRoutedByUser(Person user) {
                return false;
            }

            public Set<Person> getAllPriorApprovers()  {
                return null;
            }

            @Override
            public DateTime getDateCreated() {
                DateTime today = new DateTime();
                return today;
            }

        };
        FinancialSystemDocumentHeader dh = new FinancialSystemDocumentHeader();
        dh.setDocumentNumber("1");
        dh.setWorkflowDocument(workflowDocument);
        td.setDocumentHeader(dh);
    }

    /**
     *
     * This method tests updatePerDiemItemsFor for inserting a per diem into an existing list.
     */
    public final void testUpdatePerDiemExpenses_shiftAndAddADay() {
        PerDiemExpense perDiemExpense = new PerDiemExpense() {
            @Override
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
        setDocumentHeader(td);
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
        setDocumentHeader(td);
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
        if (!perDiemExpenses.isEmpty()) {
            Timestamp startDate = perDiemExpenses.get(0).getMileageDate();
            Timestamp endDate = perDiemExpenses.get(1).getMileageDate();

            TravelDocument td = new TravelAuthorizationDocument();
            td.setDocumentNumber("1");
            td.setPrimaryDestinationId(23242);
            setDocumentHeader(td);
            travelDocumentService.updatePerDiemItemsFor(td, perDiemExpenses, 1, startDate, endDate);

            assertEquals(2, perDiemExpenses.size());
            assertEquals(startDate, perDiemExpenses.get(0).getMileageDate());
            assertEquals(endDate, perDiemExpenses.get(1).getMileageDate());
        } else {
            assertTrue( true ); // couldn't actually run test because there aren't primary destinations
        }
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the top of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_topDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        if (!mileages.isEmpty()) {
            Timestamp testDate1 = mileages.get(1).getMileageDate();
            Timestamp testDate2 = mileages.get(2).getMileageDate();

            mileages.get(0).setLodging(new KualiDecimal(25));

            TravelDocument ta = new TravelAuthorizationDocument();
            ta.setTripBegin(mileages.get(0).getMileageDate());
            ta.setTripEnd(mileages.get(mileages.size() - 1).getMileageDate());
            ta.setPrimaryDestinationId(mileages.get(0).getPrimaryDestinationId());

            travelDocumentService.copyDownPerDiemExpense(ta, 0, mileages);

            assertEquals(3, mileages.size());
            assertEquals(new KualiDecimal(25), mileages.get(1).getLodging());
            assertEquals(testDate1, mileages.get(1).getMileageDate());
            assertEquals(new KualiDecimal(0), mileages.get(2).getLodging()); // last day lodging is always 0
            assertEquals(testDate2, mileages.get(2).getMileageDate());
        } else {
            assertTrue( true ); // couldn't actually run test because there aren't primary destinations
        }
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the middle of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_middleDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        if (!mileages.isEmpty()) {
            Timestamp testDate1 = mileages.get(1).getMileageDate();
            Timestamp testDate2 = mileages.get(2).getMileageDate();

            mileages.get(0).setLodging(new KualiDecimal(25));

            mileages.get(1).setLodging(new KualiDecimal(50));
            mileages.get(1).setMiles(20);

            TravelDocument ta = new TravelAuthorizationDocument();
            ta.setTripBegin(mileages.get(0).getMileageDate());
            ta.setTripEnd(mileages.get(mileages.size() - 1).getMileageDate());
            ta.setPrimaryDestinationId(mileages.get(0).getPrimaryDestinationId());

            travelDocumentService.copyDownPerDiemExpense(ta, 1, mileages);

            assertEquals(3, mileages.size());
            assertEquals(new KualiDecimal(50), mileages.get(1).getLodging());
            assertEquals(testDate1, mileages.get(1).getMileageDate());
            assertEquals(KualiDecimal.ZERO, mileages.get(2).getLodging());
            assertEquals(new Integer(20), mileages.get(2).getMiles());
            assertEquals(testDate2, mileages.get(2).getMileageDate());
        } else {
            assertTrue( true ); // couldn't actually run test because there aren't primary destinations
        }
    }

    /**
     *
     * This method tests copyDownPerDiemExpense from the bottom of an existing list.
     */
    public final void testCopyDownPerDiemExpenses_lastOneDown() {
        List<PerDiemExpense> mileages = createAListOfPerDiems();
        if (!mileages.isEmpty()) {
            Timestamp testDate1 = mileages.get(1).getMileageDate();
            Timestamp testDate2 = mileages.get(2).getMileageDate();

            mileages.get(0).setLodging(new KualiDecimal(25));

            mileages.get(1).setLodging(new KualiDecimal(50));
            mileages.get(1).setMiles(20);

            mileages.get(2).setLodging(new KualiDecimal(30));
            mileages.get(2).setMiles(10);

            TravelDocument ta = new TravelAuthorizationDocument();
            ta.setTripBegin(mileages.get(0).getMileageDate());
            ta.setTripEnd(mileages.get(mileages.size() - 1).getMileageDate());
            ta.setPrimaryDestinationId(mileages.get(0).getPrimaryDestinationId());

            travelDocumentService.copyDownPerDiemExpense(ta, 2, mileages);

            assertEquals(3, mileages.size());
            assertEquals(new KualiDecimal(50), mileages.get(1).getLodging());
            assertEquals(testDate1, mileages.get(1).getMileageDate());
            assertEquals(new KualiDecimal(30), mileages.get(2).getLodging());
            assertEquals(new Integer(10), mileages.get(2).getMiles());
            assertEquals(testDate2, mileages.get(2).getMileageDate());
        } else {
            assertTrue( true ); // couldn't actually run test because there aren't primary destinations
        }
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
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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

        final PerDiem perDiem = findSomePerDiem(new java.sql.Date(cal.getTimeInMillis()));
        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();

        if (perDiem != null) {
            perDiemExpense.setPrimaryDestinationId(perDiem.getPrimaryDestinationId());
            perDiemExpense.setLodging(perDiem.getLodging());
            perDiemExpense.setMileageDate(new Timestamp(today.getTime()));

            perDiemExpense.setBreakfast(true);
            perDiemExpense.setLunch(true);
            perDiemExpense.setDinner(true);

            PerDiemExpense perDiemExpense2 = this.copyPerDiem(perDiemExpense);

            cal.add(Calendar.DATE, 1);
            perDiemExpense2.setMileageDate(new Timestamp(cal.getTimeInMillis()));
            perDiemExpense2.setPrimaryDestinationId(perDiem.getPrimaryDestinationId());

            perDiemExpense2.setMiles(30);
            perDiemExpense2.setLodging(perDiem.getLodging());

            perDiemExpense2.setBreakfast(true);
            perDiemExpense2.setLunch(true);
            perDiemExpense2.setDinner(true);

            PerDiemExpense perDiemExpense3 = this.copyPerDiem(perDiemExpense2);

            cal.add(Calendar.DATE, 1);
            perDiemExpense3.setMileageDate(new Timestamp(cal.getTimeInMillis()));
            perDiemExpense3.setPrimaryDestinationId(perDiem.getPrimaryDestinationId());

            perDiemExpense3.setMiles(40);
            perDiemExpense3.setLodging(perDiem.getLodging());

            perDiemExpense3.setBreakfast(true);
            perDiemExpense3.setLunch(true);
            perDiemExpense3.setDinner(true);

            perDiemExpenses.add(perDiemExpense);
            perDiemExpenses.add(perDiemExpense2);
            perDiemExpenses.add(perDiemExpense3);
        }

        return perDiemExpenses;
    }

    /**
     * Find a PerDiem to use for the unit test
     * @param date a date for the unit test
     * @return a PerDiem
     */
    private PerDiem findSomePerDiem(java.sql.Date date) {
        final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        final PerDiemService perDiemService = SpringContext.getBean(PerDiemService.class);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        final List<PrimaryDestination> dests = new ArrayList<PrimaryDestination>();
        dests.addAll(boService.findMatching(PrimaryDestination.class, fieldValues));
        if (dests.size() > 1) {
            Random r = new Random();
            int priDestIndex = r.nextInt(dests.size());
            PerDiem perDiem = perDiemService.getPerDiem(dests.get(priDestIndex).getId(), new java.sql.Timestamp(date.getTime()), date);
            while (perDiem == null || org.apache.commons.lang.ObjectUtils.equals(perDiem.getPrimaryDestinationId(), TemConstants.CUSTOM_PRIMARY_DESTINATION_ID)) {
                priDestIndex = r.nextInt(dests.size());
                perDiem = perDiemService.getPerDiem(dests.get(priDestIndex).getId(), new java.sql.Timestamp(date.getTime()), date);
            }
            return perDiem;
        }
        return null;
    }

    /**
     *
     * This method tests calculateMileage.
     */
    public final void testCalculateMileage_mileageRatePresent() {
        TravelReimbursementDocument trDoc = new TravelReimbursementDocument();
        TravelFormBase form = new TravelReimbursementForm();
        form.setDocument(trDoc);
        KNSGlobalVariables.setKualiForm(form);

        ActualExpense actualExpense = new ActualExpense() {
            @Override
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
     * This method tests isHostedMeal
     */
    @Test
    public void testIsHostedMeal() {
        boolean isHostedMeal = false;
        // test with null actualExpense

        isHostedMeal = travelDocumentService.isHostedMeal(null);

        assertFalse(isHostedMeal);

        ActualExpense ote = new ActualExpense();

        // test with HOSTED_BREAKFAST travelExpenseTypeCode
        ote.setExpenseTypeCode(HOSTED_BREAKFAST);
        isHostedMeal = travelDocumentService.isHostedMeal(ote);
        assertTrue(isHostedMeal);

        // test with AIRLINE_EXPENSE_TYPE_CODE travelExpenseTypeCode
        ote.setExpenseTypeCode(AIRLINE_EXPENSE_TYPE_CODE);
        isHostedMeal = travelDocumentService.isHostedMeal(ote);
        assertFalse(isHostedMeal);
    }

    /**
     *
     * This method tests calculateProratePercentage
     */
    @Test
    public void testCalculateProratePercentage() {
        PerDiemExpense perDiemExpense = new PerDiemExpense();

        perDiemExpense.setMiles(20);
        perDiemExpense.setLodging(new KualiDecimal(75.00));
        Calendar cal = Calendar.getInstance();
        perDiemExpense.setMileageDate(new Timestamp(cal.getTimeInMillis()));

        perDiemExpense.setBreakfast(true);
        perDiemExpense.setLunch(true);
        perDiemExpense.setDinner(true);
        perDiemExpense.setProrated(true);

        perDiemExpense.setBreakfastValue(new KualiDecimal(12));
        perDiemExpense.setLunchValue(new KualiDecimal(13));
        perDiemExpense.setDinnerValue(new KualiDecimal(25));
        perDiemExpense.setIncidentalsValue(new KualiDecimal(10));

        perDiemExpense.setPrimaryDestinationId(22342);

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
            public MileageRate getMileageRate(java.sql.Date effectiveDate) {
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
