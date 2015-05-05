package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.impl.AccountingPeriodServiceImpl;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;

import java.sql.Date;
import java.util.Calendar;

public class BillingPeriodTest {

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_nullLastBilled_2() {
        String awardStartDate = "2014-08-01";
        String currentDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-08-01";
        String expectedBillingPeriodEnd = "2015-04-19";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillSingleDay() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-19";
        String expectedBillingPeriodStart = "2015-04-19";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillSeveralMonths() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-11-15";
        String expectedBillingPeriodStart = "2014-11-15";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_BillAcrossFiscalYears() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-06-15";
        String expectedBillingPeriodStart = "2014-06-15";
        String expectedBillingPeriodEnd = "2015-04-20";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_LOC_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.LOC_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-03-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-02-28";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_LastBilledLastMonth() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-03-29";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-03-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_LastBilledTwoMonthsAgo() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-02-17";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-02-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_SpanCalendarYears() {

        String awardStartDate = "2013-07-01";
        String lastBilled = "2014-06-15";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-06-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-15";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_MayBillNowLastYear() {
        String awardStartDate = "2014-03-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-04-15";
        String expectedBillingPeriodStart = "2014-04-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.MONTHLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.QUATERLY_BILLING_SCHEDULE_CODE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.QUATERLY_BILLING_SCHEDULE_CODE);
    }

    protected void verifyBillingPeriodPriorTo(String awardStartDate, String currentDate, String lastBilledDate, String expectedBillingPeriodStart, String expectedBillingPeriodEnd, boolean expectedBillable, String billingFrequencyCode) {
        Date lastBilledDateAsDate = nullSafeDateFromString(lastBilledDate);
        AccountingPeriodService accountingPeriodService = getMockAccountingPeriodService();

        BillingPeriod priorBillingPeriod = BillingPeriod.determineBillingPeriodPriorTo(Date.valueOf(awardStartDate), Date.valueOf(currentDate), lastBilledDateAsDate, billingFrequencyCode, accountingPeriodService);

        Date expectedStartDate = nullSafeDateFromString(expectedBillingPeriodStart);
        Assert.assertEquals("Billing period start wasn't what we thought it was going to be", expectedStartDate, priorBillingPeriod.getStartDate());
        Date expectedEndDate = nullSafeDateFromString(expectedBillingPeriodEnd);
        Assert.assertEquals("Billing period end wasn't what we thought it was going to be", expectedEndDate, priorBillingPeriod.getEndDate());
        Assert.assertEquals("Billing period billable wasn't what we thought it was going to be", expectedBillable, priorBillingPeriod.isBillable());
    }

    @Test
    public void canThisBeBilledLastBilledDateIsNull() {
        boolean expectedCanThisBeBilled = true;

        String lastBilledDate = null;
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsToday() {
        boolean expectedCanThisBeBilled = false;

        String lastBilledDate = "2015-04-21";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsYesterday() {
        boolean expectedCanThisBeBilled = false;

        String lastBilledDate = "2015-04-20";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    @Test
    public void canThisBeBilledLastBilledDateIsBeforeYesterday() {
        boolean expectedCanThisBeBilled = true;

        String lastBilledDate = "2015-04-19";
        String currentDate = "2015-04-21";
        validateCanThisBeBilled(expectedCanThisBeBilled, lastBilledDate, currentDate);
    }

    protected void validateCanThisBeBilled(boolean expectedCanThisBeBilled, String lastBilledDate, String currentDate) {
        BillingPeriod billingPeriod = new BillingPeriod();
        AccountingPeriodService accountingPeriodService = getMockAccountingPeriodService();
        Assert.assertEquals(expectedCanThisBeBilled, billingPeriod.canThisBeBilled(nullSafeDateFromString(lastBilledDate), Date.valueOf(currentDate), ArConstants.LOC_BILLING_SCHEDULE_CODE, accountingPeriodService));
    }

    protected AccountingPeriodServiceImpl getMockAccountingPeriodService() {
        return new AccountingPeriodServiceImpl() {

            @Override
            public AccountingPeriod getByDate(final Date currentDate) {
                return new AccountingPeriod() {
                    @Override
                    public Date getUniversityFiscalPeriodEndDate() {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(currentDate);
                        if (cal.get(Calendar.MONTH) == Calendar.APRIL) {
                            return Date.valueOf(cal.get(Calendar.YEAR)+"-04-30");
                        } else if (cal.get(Calendar.MONTH) == Calendar.MARCH) {
                            return Date.valueOf(cal.get(Calendar.YEAR)+"-03-31");
                        } else if (cal.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                            return Date.valueOf(cal.get(Calendar.YEAR)+"-02-28");
                        } else if (cal.get(Calendar.MONTH) == Calendar.JUNE) {
                            return Date.valueOf(cal.get(Calendar.YEAR)+"-06-30");
                        } else if (cal.get(Calendar.MONTH) == Calendar.AUGUST) {
                            return Date.valueOf(cal.get(Calendar.YEAR) + "-08-31");
                        } else {
                            return Date.valueOf(cal.get(Calendar.YEAR)+"-01-31");
                        }
                    }

                    @Override
                    public Integer getUniversityFiscalYear() {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(currentDate);
                        if (KfsDateUtils.isSameDayOrEarlier(currentDate, Date.valueOf(cal.get(Calendar.YEAR)+"-06-30"))) {
                            return cal.get(Calendar.YEAR);
                        }
                        return cal.get(Calendar.YEAR)+1;
                    }

                    @Override
                    public String getUniversityFiscalPeriodCode() {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(currentDate);
                        if (cal.get(Calendar.MONTH) == Calendar.APRIL) {
                            return "10";
                        } else if (cal.get(Calendar.MONTH) == Calendar.MARCH) {
                            return "09";
                        } else if (cal.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                            return "08";
                        } else if (cal.get(Calendar.MONTH) == Calendar.JUNE) {
                            return "12";
                        } else if (cal.get(Calendar.MONTH) == Calendar.AUGUST) {
                            return "02";
                        } else {
                            return "07";
                        }
                    }
                };
            }

            @Override
            public AccountingPeriod getByPeriod(final String periodCode, final Integer fiscalYear) {
                return new AccountingPeriod() {
                    @Override
                    public Date getUniversityFiscalPeriodEndDate() {

                        if (StringUtils.equals("10", periodCode)) {
                            return Date.valueOf(fiscalYear+"-04-30");
                        } else if (StringUtils.equals("09", periodCode)) {
                            return Date.valueOf(fiscalYear+"-03-31");
                        } else if (StringUtils.equals("08", periodCode)) {
                            return Date.valueOf(fiscalYear+"-02-28");
                        } else if (StringUtils.equals("11", periodCode)) {
                            return Date.valueOf(fiscalYear+"-05-31");
                        } else if (StringUtils.equals("02", periodCode)) {
                            return Date.valueOf(fiscalYear+"-08-31");
                        } else if (StringUtils.equals("12", periodCode)) {
                            return Date.valueOf(fiscalYear+"-06-30");
                        } else {
                            return Date.valueOf(fiscalYear+"-01-31");
                        }
                    }

                    @Override
                    public Integer getUniversityFiscalYear() {
                        return fiscalYear;
                    }

                    @Override
                    public String getUniversityFiscalPeriodCode() {
                        return periodCode;
                    }
                };
            }
        };
    }

    private Date nullSafeDateFromString(String date) {
        return (StringUtils.isBlank(date)) ? null : Date.valueOf(date);
    }


}
