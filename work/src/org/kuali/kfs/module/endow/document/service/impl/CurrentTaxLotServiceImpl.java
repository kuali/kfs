/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.dataaccess.CurrentTaxLotBalanceDao;
import org.kuali.kfs.module.endow.dataaccess.TransactionArchiveDao;
import org.kuali.kfs.module.endow.document.service.CurrentTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation to provide services for CurrentTaxLotBalance business object.
 */
@Transactional
public class CurrentTaxLotServiceImpl implements CurrentTaxLotService {
    protected BusinessObjectService businessObjectService;
    protected SecurityService securityService;
    protected KEMService kEMService;
    protected DataDictionaryService dataDictionaryService;
    protected CurrentTaxLotBalanceDao currentTaxLotBalanceDao;
    protected TransactionArchiveDao transactionArchiveDao;

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String, int, java.lang.String)
     */
    public CurrentTaxLotBalance getByPrimaryKey(String kemid, String securityId, String registrationCode, KualiInteger lotNumber, String ipIndicator) {
        Map<String, String> primaryKeys = new HashMap<String, String>();

        primaryKeys.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID, securityId);
        primaryKeys.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD, registrationCode);
        primaryKeys.put(EndowPropertyConstants.CURRENT_TAX_LOT_LOT_NBR, String.valueOf(lotNumber));
        primaryKeys.put(EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND, ipIndicator);

        return (CurrentTaxLotBalance) businessObjectService.findByPrimaryKey(CurrentTaxLotBalance.class, primaryKeys);

    }

    /**
     * @org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalancesForMatchingSecurityClassCode(String)
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesForMatchingSecurityClassCode(String securityClassCode) {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = new ArrayList();

        Collection<Security> securities = new ArrayList();

        if (StringUtils.isNotBlank(securityClassCode)) {
            Map criteria = new HashMap();

            if (dataDictionaryService.getAttributeForceUppercase(Security.class, EndowPropertyConstants.SECURITY_CLASS_CODE)) {
                securityClassCode = securityClassCode.toUpperCase();
            }
            criteria.put(EndowPropertyConstants.SECURITY_CLASS_CODE, securityClassCode);

            securities = businessObjectService.findMatching(Security.class, criteria);

            for (Security security : securities) {
                criteria.clear();
                criteria.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID, security.getId());

                currentTaxLotBalances.addAll(businessObjectService.findMatching(HoldingHistory.class, criteria));
            }
        }

        return currentTaxLotBalances;
    }

    /**
     * @org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalancesForMatchingSecurityClassCodeAndSecurityId(String, 
     *                                                                                                                                             String
     *                                                                                                                                             )
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesForMatchingSecurityClassCodeAndSecurityId(String securityClassCode, String securityId) {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = new ArrayList();

        currentTaxLotBalances = getCurrentTaxLotBalancesForMatchingSecurityClassCode(securityClassCode);
        currentTaxLotBalances.addAll(getCurrentTaxLotBalancesBySecurityId(securityId));

        return currentTaxLotBalances;
    }


    /**
     * @org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalancesBySecurityId(String)
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesBySecurityId(String securityId) {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = new ArrayList();

        if (StringUtils.isNotBlank(securityId)) {
            Map criteria = new HashMap();

            if (dataDictionaryService.getAttributeForceUppercase(CurrentTaxLotBalance.class, EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID)) {
                securityId = securityId.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_SECURITY_ID, securityId);

            currentTaxLotBalances = businessObjectService.findMatching(CurrentTaxLotBalance.class, criteria);
        }

        return currentTaxLotBalances;
    }

    /**
     * @org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalancesBySecurityId(String)
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalancesByIncomePrincipalIndicator(String incomePrincipalIndicator) {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = new ArrayList();

        if (StringUtils.isNotBlank(incomePrincipalIndicator)) {
            Map criteria = new HashMap();

            if (dataDictionaryService.getAttributeForceUppercase(CurrentTaxLotBalance.class, EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_INCOME_PRINCIPAL_INDICATOR)) {
                incomePrincipalIndicator = incomePrincipalIndicator.toUpperCase();
            }

            criteria.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_INCOME_PRINCIPAL_INDICATOR, incomePrincipalIndicator);

            currentTaxLotBalances = businessObjectService.findMatching(CurrentTaxLotBalance.class, criteria);
        }

        return currentTaxLotBalances;
    }

    /**
     * @org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getAllCurrentTaxLotBalance()
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalance() {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = new ArrayList();

        currentTaxLotBalances = businessObjectService.findAll(CurrentTaxLotBalance.class);

        return currentTaxLotBalances;
    }

    /**
     * Service Method to create a new current tax lot balance record and copy HoldingTaxLot record to it
     * 
     * @param holdingTaxLot
     * @return currentTaxLotBalance
     */
    public CurrentTaxLotBalance copyHoldingTaxLotToCurrentTaxLotBalance(HoldingTaxLot holdingTaxLot) {
        CurrentTaxLotBalance currentTaxLotBalance = new CurrentTaxLotBalance();

        currentTaxLotBalance.setKemid(holdingTaxLot.getKemid());
        currentTaxLotBalance.setSecurityId(holdingTaxLot.getSecurityId());
        currentTaxLotBalance.setRegistrationCode(holdingTaxLot.getRegistrationCode());
        currentTaxLotBalance.setLotNumber(holdingTaxLot.getLotNumber());
        currentTaxLotBalance.setIncomePrincipalIndicator(holdingTaxLot.getIncomePrincipalIndicator());

        currentTaxLotBalance.setUnits(holdingTaxLot.getUnits());
        currentTaxLotBalance.setCost(holdingTaxLot.getCost());
        currentTaxLotBalance.setAcquiredDate(holdingTaxLot.getAcquiredDate());
        currentTaxLotBalance.setPriorAccrual(holdingTaxLot.getPriorAccrual());
        currentTaxLotBalance.setCurrentAccrual(holdingTaxLot.getCurrentAccrual());
        currentTaxLotBalance.setLastTransactionDate(holdingTaxLot.getLastTransactionDate());

        return currentTaxLotBalance;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String, int, java.lang.String)
     */
    public void updateCurrentTaxLotBalance(CurrentTaxLotBalance currentTaxLotBalance) {
        if (currentTaxLotBalance == null) {
            throw new IllegalArgumentException("invalid (null) currentTaxLotBalance");
        }

        businessObjectService.save(currentTaxLotBalance);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#clearAllCurrentTaxLotRecords() clears all the records
     *      from the CurrentTaxLotBalance table.
     */
    public void clearAllCurrentTaxLotRecords() {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = businessObjectService.findAll(CurrentTaxLotBalance.class);

        for (CurrentTaxLotBalance currentTaxLotBalance : currentTaxLotBalances) {
            businessObjectService.delete(currentTaxLotBalance);
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalanceSecurityUnitValue(String) Method
     *      to get the security unit value for the current balance tax lot record
     * @param securityId
     * @return securityUnitValue
     */
    public BigDecimal getCurrentTaxLotBalanceSecurityUnitValue(String securityId) {
        BigDecimal securityUnitValue = BigDecimal.ZERO;

        Security security = securityService.getByPrimaryKey(securityId);

        return security.getUnitValue();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getNextTwelveMonthsEstimatedValue(String) Method to
     *      calculate Next Twelve Months Estimated value
     * @param securityId
     * @return nextTwelveMonthsEstimatedValue
     */
    public BigDecimal getNextTwelveMonthsEstimatedValue(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal nextTweleveMonthsEstimatedValue = BigDecimal.ZERO;

        Security security = securityService.getByPrimaryKey(securityId);

        return KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getRemainderOfFiscalYearEstimatedIncome(HoldingTaxLot,
     *      String) Method to calculate remainder of fiscal year estimated income
     * @param securityId
     * @return remainderOfFiscalYearEstimatedIncome
     */
    public BigDecimal getRemainderOfFiscalYearEstimatedIncome(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal incomeAmount = BigDecimal.ZERO;

        Security security = securityService.getByPrimaryKey(securityId);

        String classCodeType = security.getClassCode().getClassCodeType();

        if (EndowConstants.ClassCodeTypes.ALTERNATIVE_INVESTMENT.equalsIgnoreCase(classCodeType)) {
            return BigDecimal.ZERO;
        }
        // calculations for BONDS
        if (EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForBonds(security, holdingTaxLot);
        }

        // calculations for CASH
        if (EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForCash(security, holdingTaxLot);
        }

        // calculations for LIABILITIES
        if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCodeType)) {
            return BigDecimal.ZERO;
        }

        // calculations for OTHER
        if (EndowConstants.ClassCodeTypes.OTHER.equalsIgnoreCase(classCodeType)) {
            return BigDecimal.ZERO;
        }

        // calculations for POOLED FUNDS
        if (EndowConstants.ClassCodeTypes.POOLED_INVESTMENT.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForPooledFunds(security, holdingTaxLot);
        }

        // calculations for STOCKS
        if (EndowConstants.ClassCodeTypes.STOCKS.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForStocks(security, holdingTaxLot);
        }

        return incomeAmount;

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getNextFiscalYearInvestmentIncome(HoldingTaxLot,
     *      String) Method to calculate next fiscal year investment income
     * @param securityId
     * @return nextFiscalyearInvestmentIncome
     */
    public BigDecimal getNextFiscalYearInvestmentIncome(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal nextFiscalyearInvestmentIncome = BigDecimal.ZERO;

        Security security = securityService.getByPrimaryKey(securityId);
        nextFiscalyearInvestmentIncome = KEMCalculationRoundingHelper.multiply(security.getNextFiscalYearDistributionAmount(), holdingTaxLot.getUnits(), EndowConstants.Scale.SECURITY_MARKET_VALUE);

        return nextFiscalyearInvestmentIncome;
    }

    /**
     * calculates the remainder of fiscal year estimated income for bonds
     * 
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForBonds(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;

        if (ObjectUtils.isNull(security.getIncomeRate()) || security.getIncomeRate().compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }

        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        if (ObjectUtils.isNull(nextIncomeDueDate)) {
            return amount;
        }

        Date fiscalYearEndDate = getFiscalYearEndDate();

        // BONDS - rule 2.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }

        int numberOfMonthsRemaining = getNumberOfMonthsRemaining(fiscalYearEndDate, nextIncomeDueDate);
        // rule 2.b
        if (nextIncomeDueDate.before(fiscalYearEndDate) && numberOfMonthsRemaining < EndowConstants.NUMBER_OF_MONTHS_REMAINING) {
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            amount = KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(2), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        }
        else {
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        }

        return amount;
    }

    /**
     * Helper method to calculate the number of months
     * 
     * @param fiscalYearEndDate
     * @param nextIncomeDueDate
     * @return numberOfMonths
     */
    protected int getNumberOfMonthsRemaining(Date fiscalYearEndDate, Date nextIncomeDueDate) {
        int numberOfMonths = 0;

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fiscalYearEndDate);
        int fiscalMonths = calendar.get(Calendar.MONTH) + 1;
        int fiscalYear = calendar.get(Calendar.YEAR);
        
        calendar.setTime(nextIncomeDueDate);
        int nextIncomeMonths = calendar.get(Calendar.MONTH) + 1;
        int nextIncomeYear = calendar.get(Calendar.YEAR);
        
        numberOfMonths = ((fiscalYear-nextIncomeYear) * 12);
        numberOfMonths += fiscalMonths - nextIncomeMonths;
        
        return numberOfMonths;
    }

    /**
     * Helper method to get the system parameter FISCAL_YEAR_END_MONTH_AND_DAY and convert the value into a date value
     */
    protected Date getFiscalYearEndDate() {
        Date fiscalYearEndDate = null;

        fiscalYearEndDate = kEMService.getFiscalYearEndDayAndMonth();

        if (fiscalYearEndDate == null) {
            throw new RuntimeException("ParseException: CurrentTaxLotBalanceUpdateStep job stopped because System Parameter FISCAL_YEAR_END_MONTH_AND_DAY is invalid");
        }

        return fiscalYearEndDate;
    }

    /**
     * calculates the remainder of fiscal year estimated income for cash
     * 
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForCash(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;

        if (ObjectUtils.isNull(security.getIncomeRate()) || security.getIncomeRate().compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }

        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        Date fiscalYearEndDate = getFiscalYearEndDate();
        String incomePayFrequency = security.getIncomePayFrequency();

        if (ObjectUtils.isNull(nextIncomeDueDate) || ObjectUtils.isNull(incomePayFrequency)) {
            return amount;
        }

        // BONDS - rule 3.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }

        // rule 3.b
        if (nextIncomeDueDate.before(fiscalYearEndDate)) {
            Date lastPaymentDate = getLastPaymentDate(incomePayFrequency, fiscalYearEndDate);
            long daysToLastPayment = getTotalDaysToLastPayment(lastPaymentDate, nextIncomeDueDate);

            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            amount = amount.multiply(BigDecimal.valueOf(daysToLastPayment));
            amount = KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(EndowConstants.NUMBER_OF_DAYS_IN_YEAR), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            amount = amount.add(holdingTaxLot.getCurrentAccrual());
        }

        return amount;
    }

    /**
     * Helper method to calculate the number of days to the last payment
     */
    protected long getTotalDaysToLastPayment(Date lastPaymentDate, Date nextIncomeDueDate) {
        long totalDays = 0;
        long MILLISECS_PER_DAY = (1000 * 60 * 60 * 24); // total milliseconds in a day

        Calendar currentDateCalendar = Calendar.getInstance();
        currentDateCalendar.setTime(kEMService.getCurrentDate());
        currentDateCalendar.set(Calendar.HOUR, 0);
        currentDateCalendar.set(Calendar.MINUTE, 0);
        currentDateCalendar.set(Calendar.SECOND, 0);

        Calendar lastPaymentDateCalendar = Calendar.getInstance();
        lastPaymentDateCalendar.setTime(lastPaymentDate);
        lastPaymentDateCalendar.set(Calendar.HOUR, 0);
        lastPaymentDateCalendar.set(Calendar.MINUTE, 0);
        lastPaymentDateCalendar.set(Calendar.SECOND, 0);

        // to take care of leap year and day light savings time.
        long endL = lastPaymentDateCalendar.getTimeInMillis() + lastPaymentDateCalendar.getTimeZone().getOffset(lastPaymentDateCalendar.getTimeInMillis());
        long startL = currentDateCalendar.getTimeInMillis() + currentDateCalendar.getTimeZone().getOffset(currentDateCalendar.getTimeInMillis());

        return (endL - startL) / MILLISECS_PER_DAY;
    }

    /**
     * Helper method to examine the SEC_INC_PAY_FREQ and determine the date of the last payment to be made in the fiscal year.
     */
    protected Date getLastPaymentDate(String incomePayFrequency, Date fiscalYearEndDate) {
        Date lastPaymentDate = null;

        String frequencyType = incomePayFrequency.substring(0, 1);

        Date currentDate = kEMService.getCurrentDate();

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            return fiscalYearEndDate;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            return calculateLastPaymentWeekDate(currentDate, fiscalYearEndDate);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {
            String dayOfSemiMonthly = incomePayFrequency.substring(1, 3);
            return calculateLastPaymentSemiMonthlyDate(currentDate, fiscalYearEndDate, dayOfSemiMonthly);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            String dayOfMonth = incomePayFrequency.substring(1, 3);
            return calculateLastPaymentMonthlyDate(currentDate, fiscalYearEndDate, dayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return calculateLastPaymentQuarterlyDate(currentDate, fiscalYearEndDate, dayOfMonth, month);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return calculateLastPaymentSemiAnnuallyDate(currentDate, fiscalYearEndDate, dayOfMonth, month);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return calculateLastPaymentAnnuallyDate(currentDate, fiscalYearEndDate, dayOfMonth, month);
        }

        return lastPaymentDate;
    }

    /**
     * Method to calculate the last payment date for WEEKLY frequency code
     * 
     * @param currentDate, fiscalYearEndDate
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentWeekDate(Date currentDate, Date fiscalYearEndDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.DAY_OF_MONTH, -7);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the last payment date for semimonthly frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfSemiMonthly
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentSemiMonthlyDate(Date currentDate, Date fiscalYearEndDate, String dayOfSemiMonthly) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int dayOfMonthToSet = Integer.parseInt(dayOfSemiMonthly);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.DAY_OF_MONTH, 15);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.DAY_OF_MONTH, -15);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the last payment date for semimonthly frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentMonthlyDate(Date currentDate, Date fiscalYearEndDate, String dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int dayOfMonthToSet = Integer.parseInt(dayOfMonth);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, 1);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, -1);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the last payment date for quarterly frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentQuarterlyDate(Date currentDate, Date fiscalYearEndDate, String dayOfMonth, String month) {
        Calendar calendar = setCaledarWithMonth(month, currentDate);
        setCalendarWithDays(calendar, dayOfMonth);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, 3);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, -3);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the last payment date for SemiAnnually frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentSemiAnnuallyDate(Date currentDate, Date fiscalYearEndDate, String dayOfMonth, String month) {
        Calendar calendar = setCaledarWithMonth(month, currentDate);
        setCalendarWithDays(calendar, dayOfMonth);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, 6);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.MONTH, -6);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the last payment date for Annually frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected Date calculateLastPaymentAnnuallyDate(Date currentDate, Date fiscalYearEndDate, String dayOfMonth, String month) {
        Calendar calendar = setCaledarWithMonth(month, currentDate);
        setCalendarWithDays(calendar, dayOfMonth);

        while (calendar.getTime().before(fiscalYearEndDate)) {
            calendar.add(Calendar.YEAR, 1);
        }

        if (calendar.getTime().after(fiscalYearEndDate)) {
            calendar.add(Calendar.YEAR, -1);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * This method will check the current month and set the calendar to that month
     * 
     * @param month month to set the calendar, currentDate currentDate
     * @return calendar calendar is set to the month selected
     */
    protected Calendar setCaledarWithMonth(String month, Date lastPaymentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastPaymentDate);

        int calendarMonth = 1;

        if (EndowConstants.FrequencyMonths.JANUARY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.JANUARY;
        }
        else if (EndowConstants.FrequencyMonths.FEBRUARY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.FEBRUARY;
        }
        else if (EndowConstants.FrequencyMonths.MARCH.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.MARCH;
        }
        else if (EndowConstants.FrequencyMonths.APRIL.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.APRIL;
        }
        else if (EndowConstants.FrequencyMonths.MAY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.MAY;
        }
        else if (EndowConstants.FrequencyMonths.JUNE.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.JUNE;
        }
        else if (EndowConstants.FrequencyMonths.JULY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.JULY;
        }
        else if (EndowConstants.FrequencyMonths.AUGUST.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.AUGUST;
        }
        else if (EndowConstants.FrequencyMonths.SEPTEMBER.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.SEPTEMBER;
        }
        else if (EndowConstants.FrequencyMonths.OCTOBER.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.OCTOBER;
        }
        else if (EndowConstants.FrequencyMonths.NOVEMBER.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.NOVEMBER;
        }
        else if (EndowConstants.FrequencyMonths.DECEMBER.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.DECEMBER;
        }

        calendar.set(Calendar.MONTH, calendarMonth);

        return calendar;
    }

    /**
     * This method will check the current month and set the calendar to that month
     * 
     * @param month, dayOfMonth month to set the calendar, dayOfMonth day of the month to set to
     * @return calendar calendar is set to the month selected
     */
    protected void setCalendarWithDays(Calendar calendar, String dayOfMonth) {
        int dayInMonthToSet;
        int calendarMonth = calendar.get(Calendar.MONTH);

        if (StringUtils.equalsIgnoreCase(dayOfMonth, EndowConstants.FrequencyMonthly.MONTH_END)) { // month end for the month so
            // need to get max days...
            dayInMonthToSet = checkMaximumDaysInMonth(calendar.get(Calendar.MONTH));
        }
        else {
            dayInMonthToSet = Integer.parseInt(dayOfMonth);

            if (dayInMonthToSet > 29 && calendarMonth == Calendar.FEBRUARY) {
                dayInMonthToSet = checkMaximumDaysInFebruary();
            }
            else if (dayInMonthToSet > 30 && (calendarMonth == Calendar.APRIL || calendarMonth == Calendar.JUNE || calendarMonth == Calendar.SEPTEMBER || calendarMonth == Calendar.NOVEMBER)) {
                dayInMonthToSet = 30;
                dayInMonthToSet = checkMaximumDaysInMonth(calendarMonth);
            }
        }

        calendar.set(Calendar.DAY_OF_MONTH, dayInMonthToSet);
    }

    /**
     * This method will check and return either maximum days in the month as 28 or 29 for leap year. It first sets the month to
     * February and then checks the maximum days..
     * 
     * @return maxDays Maximum number of days in the month of February for calendar.
     */
    protected int checkMaximumDaysInFebruary() {
        int maxDays;
        Calendar februaryMonthlyDateCalendar = Calendar.getInstance();
        februaryMonthlyDateCalendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        maxDays = februaryMonthlyDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return maxDays;
    }

    /**
     * This method will check and return maximum days in a month.
     * 
     * @param monthNumber The number of the month to test for maximum days..
     * @return maxDays Maximum number of days in the month of February for calendar.
     */
    protected int checkMaximumDaysInMonth(int monthNumber) {
        int maxDays;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);
        maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return maxDays;
    }

    /**
     * calculates the remainder of fiscal year estimated income for pooled funds
     * 
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForPooledFunds(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;

        if (ObjectUtils.isNull(security.getIncomeNextPayDate()) || ObjectUtils.isNull(security.getFrequencyCode())) {
            return amount;
        }

        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        if (ObjectUtils.isNull(nextIncomeDueDate)) {
            return amount;
        }

        Date fiscalYearEndDate = getFiscalYearEndDate();

        // BONDS - rule 4.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }

        // rule 4.b
        if (nextIncomeDueDate.before(fiscalYearEndDate)) {
            String incomePayFrequency = security.getIncomePayFrequency();
            if (ObjectUtils.isNull(incomePayFrequency)) {
                return amount;
            }

            Date lastPaymentDate = getLastPaymentDate(incomePayFrequency, fiscalYearEndDate);

            long paymentsRemaining = getTotalPaymentsRemaining(lastPaymentDate, fiscalYearEndDate, incomePayFrequency, nextIncomeDueDate);

            long totalNumberOfPayments = kEMService.getTotalNumberOfPaymentsForFiscalYear();

            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            amount = amount.multiply(BigDecimal.valueOf(paymentsRemaining));
            amount = KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(totalNumberOfPayments), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            amount = amount.add(holdingTaxLot.getCurrentAccrual());
        }

        return amount;
    }

    /**
     * Helper method to calculate the remaining payments till the last payment date for the fiscal year
     * 
     * @param lastPaymentDate, fiscalYearEndDate, incomePayFrequency
     * @return totalPaymentsRemaining
     */
    protected long getTotalPaymentsRemaining(Date lastPaymentDate, Date fiscalYearEndDate, String incomePayFrequency, Date nextIncomeDueDate) {
        long totalPaymentsRemaining = 0;
        long totalDaysToLastPayment = getTotalDaysToLastPayment(lastPaymentDate, nextIncomeDueDate);

        String frequencyType = incomePayFrequency.substring(0, 1);

        Date currentDate = kEMService.getCurrentDate();

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            return totalDaysToLastPayment;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            return (totalDaysToLastPayment / 7);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {
            return (totalDaysToLastPayment / 15);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            String dayOfMonth = incomePayFrequency.substring(1, 3);
            return getNumberOfPaymentsRemainingForMonthlyFrequency(nextIncomeDueDate, lastPaymentDate, dayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return getNumberOfPaymentsRemainingForQuarterlyFrequency(nextIncomeDueDate, lastPaymentDate, dayOfMonth, month);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return getNumberOfPaymentsRemainingForSemiAnnuallyDate(nextIncomeDueDate, lastPaymentDate, dayOfMonth, month);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            String month = incomePayFrequency.substring(1, 2);
            String dayOfMonth = incomePayFrequency.substring(2, 4);

            return getNumberOfPaymentsRemainingForAnnuallyDate(nextIncomeDueDate, lastPaymentDate, dayOfMonth, month);
        }

        return totalPaymentsRemaining;
    }

    /**
     * Method to calculate the last payment date for monthly frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return totalPayments
     */
    protected long getNumberOfPaymentsRemainingForMonthlyFrequency(Date nextIncomeDueDate, Date lastPaymentDate, String dayOfMonth) {
        long totalPayments = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextIncomeDueDate);

        int dayOfMonthToSet = Integer.parseInt(dayOfMonth);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);

        if (calendar.getTime().before(lastPaymentDate)) {
            totalPayments = totalPayments + 1; 
        }
        else {
            return totalPayments;
        }
        
        while (calendar.getTime().before(lastPaymentDate)) {
            calendar.add(Calendar.MONTH, 1);
            totalPayments = totalPayments + 1;
        }

        return totalPayments;
    }

    /**
     * Method to calculate the last payment date for quarterly frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected long getNumberOfPaymentsRemainingForQuarterlyFrequency(Date nextIncomeDueDate, Date lastPaymentDate, String dayOfMonth, String month) {
        long totalPayments = 0;

        Calendar calendar = setCaledarWithMonth(month, nextIncomeDueDate);
        setCalendarWithDays(calendar, dayOfMonth);

        if (calendar.getTime().before(lastPaymentDate)) {
            totalPayments = totalPayments + 1; 
        }
        else {
            return totalPayments;
        }
        
        while (calendar.getTime().before(lastPaymentDate)) {
            calendar.add(Calendar.MONTH, 3); 
            totalPayments = totalPayments + 1;            
        }
        
        return totalPayments;
    }

    /**
     * Method to calculate the last payment date for SemiAnnually frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected long getNumberOfPaymentsRemainingForSemiAnnuallyDate(Date nextIncomeDueDate, Date lastPaymentDate, String dayOfMonth, String month) {
        long totalPayments = 0;

        Calendar calendar = setCaledarWithMonth(month, nextIncomeDueDate);
        setCalendarWithDays(calendar, dayOfMonth);

        if (calendar.getTime().before(lastPaymentDate)) {
            totalPayments = totalPayments + 1; 
        }
        else {
            return totalPayments;
        }
        
        while (calendar.getTime().before(lastPaymentDate)) {
            calendar.add(Calendar.MONTH, 6);    
            totalPayments = totalPayments + 1;            
        }
        
        return totalPayments;
    }

    /**
     * Method to calculate the last payment date for Annually frequency code
     * 
     * @param currentDate, fiscalYearEndDate, dayOfMonth
     * @return lastPaymentDate
     */
    protected long getNumberOfPaymentsRemainingForAnnuallyDate(Date nextIncomeDueDate, Date lastPaymentDate, String dayOfMonth, String month) {
        long totalPayments = 0;

        Calendar calendar = setCaledarWithMonth(month, nextIncomeDueDate);
        setCalendarWithDays(calendar, dayOfMonth);

        if (calendar.getTime().before(lastPaymentDate)) {
            totalPayments = totalPayments + 1; 
        }
        else {
            return totalPayments;
        }
        
        while (calendar.getTime().before(lastPaymentDate)) {
            calendar.add(Calendar.YEAR, 1);  
            totalPayments = totalPayments + 1;
        }

        return totalPayments;
    }

    /**
     * calculates the remainder of fiscal year estimated income for stocks
     * 
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForStocks(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;

        if (ObjectUtils.isNull(security.getIncomeRate()) || security.getIncomeRate().compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }

        String incomePayFrequency = security.getIncomePayFrequency();
        Date nextIncomeDueDate = security.getIncomeNextPayDate();

        if (ObjectUtils.isNull(nextIncomeDueDate)) {
            return amount;
        }

        Date fiscalYearEndDate = getFiscalYearEndDate();

        // BONDS - rule 4.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }

        int numberOfMonthsRemaing = getNumberOfMonthsRemaining(fiscalYearEndDate, nextIncomeDueDate);

        if (nextIncomeDueDate.before(fiscalYearEndDate) && numberOfMonthsRemaing < 4) {
            return BigDecimal.ZERO;
        }
        
        long quartersLeftToFiscalYear = getQuartersLeftToFiscalYear(fiscalYearEndDate);
        
        //calculate holding units times security rate....
        amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        
        // now multiply the above amount by 4 to get amount for 4 quarters or for the year...
        amount = KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(4), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        
        //now compute the amount for the quarters remaining in the fiscal year....
        amount = KEMCalculationRoundingHelper.multiply(amount, BigDecimal.valueOf(quartersLeftToFiscalYear), EndowConstants.Scale.SECURITY_MARKET_VALUE);

        return amount;
    }

    /**
     * Helper method to calculate the quarter number of the fiscal year.
     * 
     * @param nextIncomeDueDate
     * @return quarterOfFiscalYear
     */
    protected int getQuartersLeftToFiscalYear(Date fiscalYearEndDate) {
        int numberOfMonthsRemaing = getNumberOfMonthsRemaining(fiscalYearEndDate, kEMService.getCurrentDate());
        return Math.round(numberOfMonthsRemaing / 3); 
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getHoldingMarketValueSumForSecurity(java.lang.String)
     */
    public BigDecimal getHoldingMarketValueSumForSecurity(String securityId) {
        BigDecimal sum = BigDecimal.ZERO;

        List<CurrentTaxLotBalance> currentTaxLotBalances = (List<CurrentTaxLotBalance>) currentTaxLotBalanceDao.getAllCurrentTaxLotBalanceEntriesForSecurity(securityId);

        if (currentTaxLotBalances != null) {
            for (CurrentTaxLotBalance currentTaxLotBalance : currentTaxLotBalances) {
                if (currentTaxLotBalance.getHoldingMarketValue() != null) {
                    sum = sum.add(currentTaxLotBalance.getHoldingMarketValue());
                }
            }
        }

        return sum;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getHoldingMarketValue(HoldingTaxLot, String)
     */
    public BigDecimal getHoldingMarketValue(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal holdingMarketValue = BigDecimal.ZERO;

        Security security = securityService.getByPrimaryKey(securityId);

        String classCodeType = security.getClassCode().getClassCodeType();

        if (EndowConstants.ClassCodeTypes.ALTERNATIVE_INVESTMENT.equalsIgnoreCase(classCodeType)) {
            String kemid = holdingTaxLot.getKemid();
            if (dataDictionaryService.getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_KEM_ID)) {
                kemid = kemid.toUpperCase();
            }
            BigDecimal totalCashActivity = transactionArchiveDao.getTransactionArchivesTotalCashActivity(kemid, securityId);
            return (security.getSecurityValueByMarket().subtract(totalCashActivity));
        }
        // calculations for BONDS
        if (EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCodeType)) {
            holdingMarketValue = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getUnitValue(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            holdingMarketValue = KEMCalculationRoundingHelper.divide(holdingMarketValue, BigDecimal.valueOf(100), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            return holdingMarketValue;
        }

        // other cases...
        holdingMarketValue = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getUnitValue(), EndowConstants.Scale.SECURITY_MARKET_VALUE);

        return holdingMarketValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalanceTotalHoldingMarketValue(org.kuali.kfs.module.endow.businessobject.FeeMethod)
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingMarketValue(FeeMethod feeMethod) {
        BigDecimal totalHoldingMarkteValue = BigDecimal.ZERO;
        Collection securityClassCodes = new ArrayList();
        Collection securityIds = new ArrayList();
        String feeMethodCodeForClassCodes = feeMethod.getCode();
        if (StringUtils.isNotBlank(feeMethodCodeForClassCodes)) {

            if (dataDictionaryService.getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCodeForClassCodes = feeMethodCodeForClassCodes.toUpperCase();
            }
        }
        securityClassCodes = currentTaxLotBalanceDao.getSecurityClassCodes(feeMethodCodeForClassCodes);

        String feeMethodCodeForSecurityIds = feeMethod.getCode();
        if (StringUtils.isNotBlank(feeMethodCodeForSecurityIds)) {

            if (dataDictionaryService.getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCodeForSecurityIds = feeMethodCodeForSecurityIds.toUpperCase();
            }
        }
        securityIds = currentTaxLotBalanceDao.getSecurityIds(feeMethod.getCode());

        Collection<CurrentTaxLotBalance> currentTaxLotBalanceRecords = currentTaxLotBalanceDao.getCurrentTaxLotBalances(feeMethod, securityClassCodes, securityIds);
        for (CurrentTaxLotBalance currentTaxLotBalance : currentTaxLotBalanceRecords) {
            totalHoldingMarkteValue = totalHoldingMarkteValue.add(currentTaxLotBalance.getMarketValue());
        }

        return totalHoldingMarkteValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalanceTotalHoldingUnits(org.kuali.kfs.module.endow.businessobject.FeeMethod)
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingUnits(FeeMethod feeMethod) {
        BigDecimal totalHoldingUnits = BigDecimal.ZERO;

        Collection securityClassCodes = new ArrayList();
        Collection securityIds = new ArrayList();
        String feeMethodCodeForClassCodes = feeMethod.getCode();
        if (StringUtils.isNotBlank(feeMethodCodeForClassCodes)) {

            if (dataDictionaryService.getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCodeForClassCodes = feeMethodCodeForClassCodes.toUpperCase();
            }
        }
        securityClassCodes = currentTaxLotBalanceDao.getSecurityClassCodes(feeMethodCodeForClassCodes);

        String feeMethodCodeForSecurityIds = feeMethod.getCode();
        if (StringUtils.isNotBlank(feeMethodCodeForSecurityIds)) {

            if (dataDictionaryService.getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCodeForSecurityIds = feeMethodCodeForSecurityIds.toUpperCase();
            }
        }
        securityIds = currentTaxLotBalanceDao.getSecurityIds(feeMethod.getCode());

        Collection<CurrentTaxLotBalance> currentTaxLotBalanceRecords = currentTaxLotBalanceDao.getCurrentTaxLotBalances(feeMethod, securityClassCodes, securityIds);
        for (CurrentTaxLotBalance currentTaxLotBalance : currentTaxLotBalanceRecords) {
            totalHoldingUnits = totalHoldingUnits.add(currentTaxLotBalance.getUnits());
        }

        return totalHoldingUnits;
    }
    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the securityService.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Sets the kEMService.
     * 
     * @param kEMService
     */
    public void setkEMService(KEMService kEMService) {
        this.kEMService = kEMService;
    }

    public void setCurrentTaxLotBalanceDao(CurrentTaxLotBalanceDao currentTaxLotBalanceDao) {
        this.currentTaxLotBalanceDao = currentTaxLotBalanceDao;
    }

    /**
     * Sets the transactionArchiveDao attribute value.
     * 
     * @param transactionArchiveDao The transactionArchiveDao to set.
     */
    public void setTransactionArchiveDao(TransactionArchiveDao transactionArchiveDao) {
        this.transactionArchiveDao = transactionArchiveDao;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}