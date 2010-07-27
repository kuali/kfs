/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.CurrentTaxLotService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * Implementation to provide services for CurrentTaxLotBalance business object.
 */
public class CurrentTaxLotServiceImpl implements CurrentTaxLotService {
    private BusinessObjectService businessObjectService;
    protected SecurityService securityService;
    protected KEMService kEMService;
    protected HoldingTaxLotService holdingTaxLotService;
    
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
    * Service Method to create a new current tax lot balance record and copy HoldingTaxLot record to it
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
        currentTaxLotBalance.setForeignTaxWithheld(holdingTaxLot.getForeignTaxWithheld());
        
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
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#clearAllCurrentTaxLotRecords()
     * clears all the records from the CurrentTaxLotBalance table.
     */
    public void clearAllCurrentTaxLotRecords() {
        Collection<CurrentTaxLotBalance> currentTaxLotBalances = businessObjectService.findAll(CurrentTaxLotBalance.class);

        for (CurrentTaxLotBalance currentTaxLotBalance : currentTaxLotBalances) {
            businessObjectService.delete(currentTaxLotBalance);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getCurrentTaxLotBalanceSecurityUnitValue(String)
     * Method to get the security unit value for the current balance tax lot record
     * @param securityId
     * @return securityUnitValue
     */
    public BigDecimal getCurrentTaxLotBalanceSecurityUnitValue(String securityId) {
        BigDecimal securityUnitValue = BigDecimal.ZERO;
        
        Security security = securityService.getByPrimaryKey(securityId);
        
        return security.getUnitValue();
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getNextTwelveMonthsEstimatedValue(String)
     * Method to calculate Next Twelve Months Estimated value
     * @param securityId
     * @return nextTwelveMonthsEstimatedValue
     */
    public BigDecimal getNextTwelveMonthsEstimatedValue(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal nextTweleveMonthsEstimatedValue = BigDecimal.ZERO;
        
        Security security = securityService.getByPrimaryKey(securityId);

        return KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getRemainderOfFiscalYearEstimatedIncome(HoldingTaxLot, String)
     * Method to calculate remainder of fiscal year estimated income
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
        //calculations for BONDS
        if (EndowConstants.ClassCodeTypes.BOND.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForBonds(security, holdingTaxLot);
        }        
        
        //calculations for CASH
        if (EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForCash(security, holdingTaxLot);
        }        
        
        //calculations for LIABILITIES
        if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCodeType)) {
            return BigDecimal.ZERO;
        }        

        //calculations for OTHER
        if (EndowConstants.ClassCodeTypes.OTHER.equalsIgnoreCase(classCodeType)) {
            return BigDecimal.ZERO;
        }        

        //calculations for POOLED FUNDS
        if (EndowConstants.ClassCodeTypes.POOLED_INVESTMENT.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForPooledFunds(security, holdingTaxLot);
        }        
        
        //calculations for STOCKS
        if (EndowConstants.ClassCodeTypes.STOCKS.equalsIgnoreCase(classCodeType)) {
            return getRemainderOfFiscalYearEstimatedIncomeForStocks(security, holdingTaxLot);
        }        
        
        return incomeAmount;
        
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.CurrentTaxLotService#getNextFiscalYearInvestmentIncome(HoldingTaxLot, String)
     * Method to calculate next fiscal year investment income
     * @param securityId
     * @return nextFiscalyearInvestmentIncome
     */
    public BigDecimal getNextFiscalYearInvestmentIncome(HoldingTaxLot holdingTaxLot, String securityId) {
        BigDecimal nextFiscalyearInvestmentIncome = BigDecimal.ZERO;
        
        Security security = securityService.getByPrimaryKey(securityId);
        nextFiscalyearInvestmentIncome = KEMCalculationRoundingHelper.multiply(security.getNextFiscalYearDistributionAmount(), holdingTaxLot.getUnits(), EndowConstants.Scale.SECURITY_UNIT_VALUE);

        return nextFiscalyearInvestmentIncome;
    }
    
    /**
     * calculates the remainder of fiscal year estimated income for bonds
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForBonds(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;
        
        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        Date fiscalYearEndDate = getFiscalYearEndDate();
        
        //BONDS - rule 2.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }
        
        int numberOfMonthsRemaining = getNumberOfMonthsRemaining(fiscalYearEndDate, nextIncomeDueDate);
        //rule 2.b
        if (nextIncomeDueDate.before(fiscalYearEndDate) && numberOfMonthsRemaining < EndowConstants.NUMBER_OF_MONTHS_REMAINING) {
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            amount =  KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(2), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }
        else {
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);            
        }
        
        return amount;
    }
    
    /**
     * Helper method to calculate the number of months
     * @param fiscalYearEndDate
     * @param nextIncomeDueDate
     * @return numberOfMonths
     */
    protected int getNumberOfMonthsRemaining(Date fiscalYearEndDate, Date nextIncomeDueDate) {
        int numberOfMonths  = 0;
        
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime(fiscalYearEndDate);
        int fiscalMonths = calendar.get(Calendar.MONTH);
        int fiscalYear = calendar.get(Calendar.YEAR);
        
        calendar.setTime(nextIncomeDueDate);
        int nextIncomeMonths = calendar.get(Calendar.MONTH);
        int nextIncomeYear = calendar.get(Calendar.YEAR);
        
        //income due date in the previous fiscal year then add the 6 months to the number of months
        // in the previous fiscal year
        //example: fiscalYear = 06/30/2010 and nextIncomeDueDate = 10/01/2008
        if (nextIncomeYear < fiscalYear) {
            numberOfMonths =  (((fiscalYear-nextIncomeYear) * 12)- nextIncomeMonths) + EndowConstants.NUMBER_OF_MONTHS_REMAINING;
        }
        else {
            if (fiscalYearEndDate.before(nextIncomeDueDate)) {
                numberOfMonths = nextIncomeMonths-fiscalMonths;
            }
            else {
                numberOfMonths = fiscalMonths-nextIncomeMonths;
            }
        }
        
        return numberOfMonths;
    }

    /**
     * Helper method to get the system parameter FISCAL_YEAR_END_DAY_AND_MONTH and convert the value
     * into a date value
     */
    protected Date getFiscalYearEndDate() {
       Date fiscalYearEndDate = null;
       
       fiscalYearEndDate = kEMService.getFiscalYearEndDayAndMonth();
       
       if (fiscalYearEndDate == null) {
           throw new RuntimeException("ParseException: CurrentTaxLotBalanceUpdateStep job stopped because System Parameter FISCAL_YEAR_END_DAY_AND_MONTH is invalid");
       }
       
       return fiscalYearEndDate;
    }
    
    /**
     * calculates the remainder of fiscal year estimated income for cash
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForCash(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;
        
        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        Date fiscalYearEndDate = getFiscalYearEndDate();
        
        String incomePayFrequency = security.getIncomePayFrequency();
        
        //BONDS - rule 3.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }
        
        //rule 3.b
        if (nextIncomeDueDate.before(fiscalYearEndDate)) {
            Date lastPaymentDate = getLastPaymentDate(incomePayFrequency, fiscalYearEndDate);
            long daysToLastPayment = getTotalDaysToLastPayment(lastPaymentDate);
            
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            amount = amount.multiply(BigDecimal.valueOf(daysToLastPayment));
            amount =  KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(EndowConstants.NUMBER_OF_DAYS_IN_YEAR), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            amount = amount.add(holdingTaxLot.getCurrentAccrual());
        }
        
        return amount;
    }
    
    /**
     * Helper method to calculate the number of days to the last payment 
     */
    protected long getTotalDaysToLastPayment(Date lastPaymentDate) {
        long totalDays = 0;
        
     // TODO need to implement the logic here..
        return totalDays;
    }
    /**
     * Helper method to examine the SEC_INC_PAY_FREQ and determine the date of the last payment 
     * to be made in the fiscal year.
     */
    protected Date getLastPaymentDate(String incomePayFrequency, Date fiscalYearEndDate) {
        Date lastPaymentDate = null;
        
     // TODO need to implement the logic here..
        return lastPaymentDate;
    }
    
    /**
     * calculates the remainder of fiscal year estimated income for pooled funds
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForPooledFunds(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;
        
        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        Date fiscalYearEndDate = getFiscalYearEndDate();
        
        String incomePayFrequency = security.getIncomePayFrequency();
        
        //BONDS - rule 4.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }
        
        //rule 4.b
        if (nextIncomeDueDate.before(fiscalYearEndDate)) {
            Date lastPaymentDate = getLastPaymentDate(incomePayFrequency, fiscalYearEndDate);
            long paymentsRemaining = getTotalPaymentsRemaining(lastPaymentDate);
            long totalNumberOfPayments = getTotalPaymentsForFiscalYear(fiscalYearEndDate, incomePayFrequency);
            
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            amount = amount.multiply(BigDecimal.valueOf(paymentsRemaining));
            amount =  KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(totalNumberOfPayments), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            amount = amount.add(holdingTaxLot.getCurrentAccrual());
        }
        
        return amount;
    }
    
    /**
     * Helper method to calculate the remaining payments till the last payment date for the fiscal year
     */
    protected int getTotalPaymentsRemaining(Date lastPaymentDate) {
        int totalPaymentsRemaining = 0;
        
     // TODO need to implement the logic here..
        return totalPaymentsRemaining;
    }
    
    /**
     * Helper method to calculate the total number of payments for the fiscal year
     * @param fiscalYearEndDate
     * @param incomePayFrequency
     * @return numberOfPayments
     */
    protected int getTotalPaymentsForFiscalYear(Date fiscalYearEndDate, String incomePayFrequency) {
        int numberOfPayments = 0;
        
     // TODO need to implement the logic here..
        return numberOfPayments;
        
    }
    
    
    /**
     * calculates the remainder of fiscal year estimated income for stocks
     * @param security
     * @param holdingTaxLot
     * @return amount
     */
    protected BigDecimal getRemainderOfFiscalYearEstimatedIncomeForStocks(Security security, HoldingTaxLot holdingTaxLot) {
        BigDecimal amount = BigDecimal.ZERO;
        
        Date nextIncomeDueDate = security.getIncomeNextPayDate();
        Date fiscalYearEndDate = getFiscalYearEndDate();
        
        String incomePayFrequency = security.getIncomePayFrequency();
        
        //BONDS - rule 4.a
        if (nextIncomeDueDate.after(fiscalYearEndDate)) {
            return BigDecimal.ZERO;
        }
        
        
        int numberOfMonthsRemaing = getNumberOfMonthsRemaining(fiscalYearEndDate, nextIncomeDueDate);
        
        if (nextIncomeDueDate.before(fiscalYearEndDate) && numberOfMonthsRemaing < 4) {
            return BigDecimal.ZERO;
        }
        
        //rule 4.b
        if (nextIncomeDueDate.before(fiscalYearEndDate)) {
            Date lastPaymentDate = getLastPaymentDate(incomePayFrequency, fiscalYearEndDate);
            long quarterOfFiscalYear = getQuarterOfFiscalYear(nextIncomeDueDate);
            
            long totalNumberOfPayments = getTotalPaymentsForFiscalYear(fiscalYearEndDate, incomePayFrequency);
            
            amount = KEMCalculationRoundingHelper.multiply(holdingTaxLot.getUnits(), security.getIncomeRate(), EndowConstants.Scale.SECURITY_UNIT_VALUE);

            
            if (quarterOfFiscalYear == 1) {
                return amount;
            }
            
            if (quarterOfFiscalYear == 2) {
                amount = KEMCalculationRoundingHelper.multiply(amount, BigDecimal.valueOf(3), EndowConstants.Scale.SECURITY_UNIT_VALUE);
                KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(4),  EndowConstants.Scale.SECURITY_UNIT_VALUE);
                
                return amount;
            }

            if (quarterOfFiscalYear == 3) {
                KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(2),  EndowConstants.Scale.SECURITY_UNIT_VALUE);
                
                return amount;
            }
            
            if (quarterOfFiscalYear == 4) {
                KEMCalculationRoundingHelper.divide(amount, BigDecimal.valueOf(4),  EndowConstants.Scale.SECURITY_UNIT_VALUE);
                
                return amount;
            }            
        }
        
        return amount;
    }
    
    /**
     * Helper method to calculate the quarter number of the fiscal year.
     * @param nextIncomeDueDate
     * @return quarterOfFiscalYear
     */
    protected int getQuarterOfFiscalYear(Date nextIncomeDueDate) {
        int quarterOfFiscalYear = 0;

     // TODO need to implement the logic here..
        return quarterOfFiscalYear;
    }
    
    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
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
     * Gets the holdingTaxLotService.
     * 
     * @return holdingTaxLotService
     */
    public HoldingTaxLotService getHoldingTaxLotService() {
        return holdingTaxLotService;
    }

    /**
     * Sets the holdingTaxLotService.
     * 
     * @param holdingTaxLotService
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Gets the securityService.
     * 
     * @return securityService
     */
    protected SecurityService getSecurityService() {
        return securityService;
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
     * gets the kEMService.
     * @param kEMService
     */    
    protected KEMService getkEMService() {
        return kEMService;
    }

    /**
     * Sets the kEMService.
     * @param kEMService
     */    
    public void setkEMService(KEMService kEMService) {
        this.kEMService = kEMService;
    }

    
}
