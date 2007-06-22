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

package org.kuali.module.labor.bo;

import static org.kuali.module.labor.LaborConstants.LABOR_USER_SERVICE_NAME;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.service.LaborUserService;

/**
 * 
 */
public class LedgerBalance extends Balance {
    
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String positionNumber;
    private String emplid;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private KualiDecimal month1AccountLineAmount;
    private KualiDecimal month2AccountLineAmount;
    private KualiDecimal month3AccountLineAmount;
    private KualiDecimal month4AccountLineAmount;
    private KualiDecimal month5AccountLineAmount;
    private KualiDecimal month6AccountLineAmount;
    private KualiDecimal month7AccountLineAmount;
    private KualiDecimal month8AccountLineAmount;
    private KualiDecimal month9AccountLineAmount;
    private KualiDecimal month10AccountLineAmount;
    private KualiDecimal month11AccountLineAmount;
    private KualiDecimal month12AccountLineAmount;
    private KualiDecimal month13AccountLineAmount;
    private Timestamp transactionDateTimeStamp;
    private String financialObjectFringeOrSalaryCode;

    private Chart chartOfAccounts;
    private ObjectType financialObjectType;
    private Balance financialBalance;

    private UniversalUser ledgerPerson; // follow naming convention?
    private LaborObject laborObject;

    /**
     * Default constructor.
     */
    public LedgerBalance() {
        super();
        this.setAccountLineAnnualBalanceAmount(KualiDecimal.ZERO);
        this.setFinancialBeginningBalanceLineAmount(KualiDecimal.ZERO);
        this.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);

        this.month1AccountLineAmount = KualiDecimal.ZERO;
        this.month2AccountLineAmount = KualiDecimal.ZERO;
        this.month3AccountLineAmount = KualiDecimal.ZERO;
        this.month4AccountLineAmount = KualiDecimal.ZERO;
        this.month5AccountLineAmount = KualiDecimal.ZERO;
        this.month6AccountLineAmount = KualiDecimal.ZERO;
        this.month7AccountLineAmount = KualiDecimal.ZERO;
        this.month8AccountLineAmount = KualiDecimal.ZERO;
        this.month9AccountLineAmount = KualiDecimal.ZERO;
        this.month10AccountLineAmount = KualiDecimal.ZERO;
        this.month11AccountLineAmount = KualiDecimal.ZERO;
        this.month12AccountLineAmount = KualiDecimal.ZERO;
        this.month13AccountLineAmount = KualiDecimal.ZERO;
    }

    /**
     * Gets the emplid attribute. 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {      
        this.emplid = emplid;

        // Try to find a ledger person for this emplid if one exists
        try {
            setLedgerPerson(((LaborUserService) SpringServiceLocator.getLocalKFSService(LABOR_USER_SERVICE_NAME))
                            .getLaborUserByPersonPayrollIdentifier(emplid).getUniversalUser());
        }
        catch (UserNotFoundException unfe) {
            // The user is not valid. We don't have a user
            setLedgerPerson(null);
        }
    }

    /**
     * Gets the financialBalance attribute. 
     * @return Returns the financialBalance.
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute value.
     * @param financialBalance The financialBalance to set.
     */
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * Gets the financialBalanceTypeCode attribute. 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialBeginningBalanceLineAmount attribute. 
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * Sets the financialBeginningBalanceLineAmount attribute value.
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute. 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the month10AccountLineAmount attribute. 
     * @return Returns the month10AccountLineAmount.
     */
    public KualiDecimal getMonth10AccountLineAmount() {
        return month10AccountLineAmount;
    }

    /**
     * Sets the month10AccountLineAmount attribute value.
     * @param month10AccountLineAmount The month10AccountLineAmount to set.
     */
    public void setMonth10AccountLineAmount(KualiDecimal month10AccountLineAmount) {
        this.month10AccountLineAmount = month10AccountLineAmount;
    }

    /**
     * Gets the month11AccountLineAmount attribute. 
     * @return Returns the month11AccountLineAmount.
     */
    public KualiDecimal getMonth11AccountLineAmount() {
        return month11AccountLineAmount;
    }

    /**
     * Sets the month11AccountLineAmount attribute value.
     * @param month11AccountLineAmount The month11AccountLineAmount to set.
     */
    public void setMonth11AccountLineAmount(KualiDecimal month11AccountLineAmount) {
        this.month11AccountLineAmount = month11AccountLineAmount;
    }

    /**
     * Gets the month12AccountLineAmount attribute. 
     * @return Returns the month12AccountLineAmount.
     */
    public KualiDecimal getMonth12AccountLineAmount() {
        return month12AccountLineAmount;
    }

    /**
     * Sets the month12AccountLineAmount attribute value.
     * @param month12AccountLineAmount The month12AccountLineAmount to set.
     */
    public void setMonth12AccountLineAmount(KualiDecimal month12AccountLineAmount) {
        this.month12AccountLineAmount = month12AccountLineAmount;
    }

    /**
     * Gets the month13AccountLineAmount attribute. 
     * @return Returns the month13AccountLineAmount.
     */
    public KualiDecimal getMonth13AccountLineAmount() {
        return month13AccountLineAmount;
    }

    /**
     * Sets the month13AccountLineAmount attribute value.
     * @param month13AccountLineAmount The month13AccountLineAmount to set.
     */
    public void setMonth13AccountLineAmount(KualiDecimal month13AccountLineAmount) {
        this.month13AccountLineAmount = month13AccountLineAmount;
    }

    /**
     * Gets the month1AccountLineAmount attribute. 
     * @return Returns the month1AccountLineAmount.
     */
    public KualiDecimal getMonth1AccountLineAmount() {
        return month1AccountLineAmount;
    }

    /**
     * Sets the month1AccountLineAmount attribute value.
     * @param month1AccountLineAmount The month1AccountLineAmount to set.
     */
    public void setMonth1AccountLineAmount(KualiDecimal month1AccountLineAmount) {
        this.month1AccountLineAmount = month1AccountLineAmount;
    }

    /**
     * Gets the month2AccountLineAmount attribute. 
     * @return Returns the month2AccountLineAmount.
     */
    public KualiDecimal getMonth2AccountLineAmount() {
        return month2AccountLineAmount;
    }

    /**
     * Sets the month2AccountLineAmount attribute value.
     * @param month2AccountLineAmount The month2AccountLineAmount to set.
     */
    public void setMonth2AccountLineAmount(KualiDecimal month2AccountLineAmount) {
        this.month2AccountLineAmount = month2AccountLineAmount;
    }

    /**
     * Gets the month3AccountLineAmount attribute. 
     * @return Returns the month3AccountLineAmount.
     */
    public KualiDecimal getMonth3AccountLineAmount() {
        return month3AccountLineAmount;
    }

    /**
     * Sets the month3AccountLineAmount attribute value.
     * @param month3AccountLineAmount The month3AccountLineAmount to set.
     */
    public void setMonth3AccountLineAmount(KualiDecimal month3AccountLineAmount) {
        this.month3AccountLineAmount = month3AccountLineAmount;
    }

    /**
     * Gets the month4AccountLineAmount attribute. 
     * @return Returns the month4AccountLineAmount.
     */
    public KualiDecimal getMonth4AccountLineAmount() {
        return month4AccountLineAmount;
    }

    /**
     * Sets the month4AccountLineAmount attribute value.
     * @param month4AccountLineAmount The month4AccountLineAmount to set.
     */
    public void setMonth4AccountLineAmount(KualiDecimal month4AccountLineAmount) {
        this.month4AccountLineAmount = month4AccountLineAmount;
    }

    /**
     * Gets the month5AccountLineAmount attribute. 
     * @return Returns the month5AccountLineAmount.
     */
    public KualiDecimal getMonth5AccountLineAmount() {
        return month5AccountLineAmount;
    }

    /**
     * Sets the month5AccountLineAmount attribute value.
     * @param month5AccountLineAmount The month5AccountLineAmount to set.
     */
    public void setMonth5AccountLineAmount(KualiDecimal month5AccountLineAmount) {
        this.month5AccountLineAmount = month5AccountLineAmount;
    }

    /**
     * Gets the month6AccountLineAmount attribute. 
     * @return Returns the month6AccountLineAmount.
     */
    public KualiDecimal getMonth6AccountLineAmount() {
        return month6AccountLineAmount;
    }

    /**
     * Sets the month6AccountLineAmount attribute value.
     * @param month6AccountLineAmount The month6AccountLineAmount to set.
     */
    public void setMonth6AccountLineAmount(KualiDecimal month6AccountLineAmount) {
        this.month6AccountLineAmount = month6AccountLineAmount;
    }

    /**
     * Gets the month7AccountLineAmount attribute. 
     * @return Returns the month7AccountLineAmount.
     */
    public KualiDecimal getMonth7AccountLineAmount() {
        return month7AccountLineAmount;
    }

    /**
     * Sets the month7AccountLineAmount attribute value.
     * @param month7AccountLineAmount The month7AccountLineAmount to set.
     */
    public void setMonth7AccountLineAmount(KualiDecimal month7AccountLineAmount) {
        this.month7AccountLineAmount = month7AccountLineAmount;
    }

    /**
     * Gets the month8AccountLineAmount attribute. 
     * @return Returns the month8AccountLineAmount.
     */
    public KualiDecimal getMonth8AccountLineAmount() {
        return month8AccountLineAmount;
    }

    /**
     * Sets the month8AccountLineAmount attribute value.
     * @param month8AccountLineAmount The month8AccountLineAmount to set.
     */
    public void setMonth8AccountLineAmount(KualiDecimal month8AccountLineAmount) {
        this.month8AccountLineAmount = month8AccountLineAmount;
    }

    /**
     * Gets the month9AccountLineAmount attribute. 
     * @return Returns the month9AccountLineAmount.
     */
    public KualiDecimal getMonth9AccountLineAmount() {
        return month9AccountLineAmount;
    }

    /**
     * Sets the month9AccountLineAmount attribute value.
     * @param month9AccountLineAmount The month9AccountLineAmount to set.
     */
    public void setMonth9AccountLineAmount(KualiDecimal month9AccountLineAmount) {
        this.month9AccountLineAmount = month9AccountLineAmount;
    }

    /**
     * Gets the positionNumber attribute. 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the transactionDateTimeStamp attribute. 
     * @return Returns the transactionDateTimeStamp.
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute value.
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    @Override
    public String getBalanceTypeCode() {
        return this.getFinancialBalanceTypeCode();
    }

    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.setFinancialBalanceTypeCode(balanceTypeCode);
    }

    @Override
    public Chart getChart() {
        return this.getChartOfAccounts();
    }

    @Override
    public void setChart(Chart chart) {
        this.setChartOfAccounts(chart);
    }

    /**
     * Gets the chartOfAccounts attribute. 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    @Override
    public KualiDecimal getMonth10Amount() {
        return this.getMonth10AccountLineAmount();
    }

    @Override
    public void setMonth10Amount(KualiDecimal month10Amount) {
        this.setMonth10AccountLineAmount(month10Amount);
    }

    @Override
    public KualiDecimal getMonth11Amount() {
        return this.getMonth11AccountLineAmount();
    }

    @Override
    public void setMonth11Amount(KualiDecimal month11Amount) {
        this.setMonth11AccountLineAmount(month11Amount);
    }


    @Override
    public KualiDecimal getMonth12Amount() {
        return this.getMonth12AccountLineAmount();
    }

    @Override
    public void setMonth12Amount(KualiDecimal month12Amount) {
        this.setMonth12AccountLineAmount(month12Amount);
    }


    @Override
    public KualiDecimal getMonth13Amount() {
        return this.getMonth13AccountLineAmount();
    }

    @Override
    public void setMonth13Amount(KualiDecimal month13Amount) {
        this.setMonth13AccountLineAmount(month13Amount);
    }


    @Override
    public KualiDecimal getMonth1Amount() {
        return this.getMonth1AccountLineAmount();
    }

    @Override
    public void setMonth1Amount(KualiDecimal month1Amount) {
        this.setMonth1AccountLineAmount(month1Amount);
    }


    @Override
    public KualiDecimal getMonth2Amount() {
        return this.getMonth2AccountLineAmount();
    }

    @Override
    public void setMonth2Amount(KualiDecimal month2Amount) {
        this.setMonth2AccountLineAmount(month2Amount);
    }

    @Override
    public KualiDecimal getMonth3Amount() {
        return this.getMonth3AccountLineAmount();
    }

    @Override
    public void setMonth3Amount(KualiDecimal month3Amount) {
        this.setMonth3AccountLineAmount(month3Amount);
    }

    @Override
    public KualiDecimal getMonth4Amount() {
        return this.getMonth4AccountLineAmount();
    }

    @Override
    public void setMonth4Amount(KualiDecimal month4Amount) {
        this.setMonth4AccountLineAmount(month4Amount);
    }
    
    @Override
    public KualiDecimal getMonth5Amount() {
        return this.getMonth5AccountLineAmount();
    }

    @Override
    public void setMonth5Amount(KualiDecimal month5Amount) {
        this.setMonth5AccountLineAmount(month5Amount);
    }

    @Override
    public KualiDecimal getMonth6Amount() {
        return this.getMonth6AccountLineAmount();
    }

    @Override
    public void setMonth6Amount(KualiDecimal month6Amount) {
        this.setMonth6AccountLineAmount(month6Amount);
    }

    @Override
    public KualiDecimal getMonth7Amount() {
        return this.getMonth7AccountLineAmount();
    }

    @Override
    public void setMonth7Amount(KualiDecimal month7Amount) {
        this.setMonth7AccountLineAmount(month7Amount);
    }

    @Override
    public KualiDecimal getMonth8Amount() {
        return this.getMonth8AccountLineAmount();
    }

    @Override
    public void setMonth8Amount(KualiDecimal month8Amount) {
        this.setMonth8AccountLineAmount(month8Amount);
    }

    @Override
    public KualiDecimal getMonth9Amount() {
        return this.getMonth9AccountLineAmount();
    }

    @Override
    public void setMonth9Amount(KualiDecimal month9Amount) {
        this.setMonth9AccountLineAmount(month9Amount);
    }

    @Override
    public String getObjectCode() {
        return this.getFinancialObjectCode();
    }

    @Override
    public void setObjectCode(String objectCode) {
        this.setFinancialObjectCode(objectCode);        
    }

    @Override
    public ObjectType getObjectType() {
        return this.getFinancialObjectType();
    }

    @Override
    public void setObjectType(ObjectType objectType) {
        this.setFinancialObjectType(objectType);
    }

    @Override
    public String getObjectTypeCode() {
        return this.getFinancialObjectTypeCode();
    }

    @Override
    public void setObjectTypeCode(String objectTypeCode) {
        this.setFinancialObjectTypeCode(objectTypeCode);
    }

    @Override
    public String getSubObjectCode() {
        return this.getFinancialSubObjectCode();
    }

    @Override
    public void setSubObjectCode(String subObjectCode) {
        this.setFinancialSubObjectCode(subObjectCode);
    }
    
    @Override
    public void addAmount(String period, KualiDecimal amount) {
        if (KFSConstants.ANNUAL_BALANCE.equals(period)) {
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.BEGINNING_BALANCE.equals(period)) {
            this.setFinancialBeginningBalanceLineAmount(this.getFinancialBeginningBalanceLineAmount().add(amount));
        }
        else if (KFSConstants.CG_BEGINNING_BALANCE.equals(period)) {
            this.setContractsGrantsBeginningBalanceAmount(this.getContractsGrantsBeginningBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH1.equals(period)) {
            this.month1AccountLineAmount = month1AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH2.equals(period)) {
            this.month2AccountLineAmount = month2AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH3.equals(period)) {
            this.month3AccountLineAmount = month3AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH4.equals(period)) {
            this.month4AccountLineAmount = month4AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH5.equals(period)) {
            this.month5AccountLineAmount = month5AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH6.equals(period)) {
            this.month6AccountLineAmount = month6AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH7.equals(period)) {
            this.month7AccountLineAmount = month7AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH8.equals(period)) {
            this.month8AccountLineAmount = month8AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH9.equals(period)) {
            this.month9AccountLineAmount = month9AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH10.equals(period)) {
            this.month10AccountLineAmount = month10AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH11.equals(period)) {
            this.month11AccountLineAmount = month11AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH12.equals(period)) {
            this.month12AccountLineAmount = month12AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH13.equals(period)) {
            this.month13AccountLineAmount = month13AccountLineAmount.add(amount);
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
    }
    
    /**
     * Retrieve the associated <code>{@link LaborObject}</code> linked by chart of accounts code, financial object code, and university fiscal year
     *
     * @return Labor Object
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }
    
    /**
     * For OJB to assign a Labor Object association
     *
     * @param lobj
     */
    @Deprecated
    public void setLaborObject(LaborObject lobj) {
        laborObject = lobj;
    }
    
    public UniversalUser getLedgerPerson() {
        return ledgerPerson;
    }

    /**
     * Sets the ledgerPerson attribute.
     * 
     * @param ledgerPerson The ledgerPerson to set.
     */
    public void setLedgerPerson(UniversalUser ledgerPerson) {
        this.ledgerPerson = ledgerPerson;
    }
}
