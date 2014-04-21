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

package org.kuali.kfs.module.ld.businessobject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

/**
 * Labor business object for LedgerBalance.
 */
public class LedgerBalance extends Balance implements LaborLedgerBalance{
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String positionNumber;
    private String emplid;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private Timestamp transactionDateTimeStamp;
    private String financialObjectFringeOrSalaryCode;
    private Chart chartOfAccounts;
    private ObjectType financialObjectType;
    private Person ledgerPerson;
    private LaborObject laborObject;

    /**
     * Default constructor.
     */
    public LedgerBalance() {
        super();
        this.setAccountLineAnnualBalanceAmount(KualiDecimal.ZERO);
        this.setFinancialBeginningBalanceLineAmount(KualiDecimal.ZERO);
        super.setBeginningBalanceLineAmount(KualiDecimal.ZERO);
        this.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);
    }

    /**
     * Constructs a LedgerBalance.java.
     *
     * @param transaction
     */
    public LedgerBalance(LaborTransaction transaction) {
        this();
        this.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        this.setAccountNumber(transaction.getAccountNumber());
        this.setFinancialBalanceTypeCode(transaction.getFinancialBalanceTypeCode());
        this.setEmplid(transaction.getEmplid());
        this.setFinancialObjectCode(transaction.getFinancialObjectCode());
        this.setFinancialObjectTypeCode(transaction.getFinancialObjectTypeCode());
        this.setFinancialSubObjectCode(transaction.getFinancialSubObjectCode());
        this.setPositionNumber(transaction.getPositionNumber());
        this.setUniversityFiscalYear(transaction.getUniversityFiscalYear());
        this.setSubAccountNumber(transaction.getSubAccountNumber());
    }

    /**
     * Constructs a LedgerBalance.java.
     *
     * @param transaction
     */
    public LedgerBalance(LaborBalanceHistory ledgerBalanceHistory) {
        this();
        this.setChartOfAccountsCode(ledgerBalanceHistory.getChartOfAccountsCode());
        this.setAccountNumber(ledgerBalanceHistory.getAccountNumber());
        this.setFinancialBalanceTypeCode(ledgerBalanceHistory.getFinancialBalanceTypeCode());
        this.setEmplid(ledgerBalanceHistory.getEmplid());
        this.setFinancialObjectCode(ledgerBalanceHistory.getFinancialObjectCode());
        this.setFinancialObjectTypeCode(ledgerBalanceHistory.getFinancialObjectTypeCode());
        this.setFinancialSubObjectCode(ledgerBalanceHistory.getFinancialSubObjectCode());
        this.setPositionNumber(ledgerBalanceHistory.getPositionNumber());
        this.setUniversityFiscalYear(ledgerBalanceHistory.getUniversityFiscalYear());
        this.setSubAccountNumber(ledgerBalanceHistory.getSubAccountNumber());
    }

    /**
     * Gets the emplid
     *
     * @return Returns the emplid.
     */
    @Override
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid
     *
     * @param emplid The emplid to set.
     */
    @Override
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the financialBalanceTypeCode
     *
     * @return Returns the financialBalanceTypeCode.
     */
    @Override
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode
     *
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    @Override
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialBeginningBalanceLineAmount
     *
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    @Override
    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * Sets the financialBeginningBalanceLineAmount
     *
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    @Override
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
        super.setBeginningBalanceLineAmount(financialBeginningBalanceLineAmount);

    }

    /**
     * Gets the financialObjectCode
     *
     * @return Returns the financialObjectCode.
     */
    @Override
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    @Override
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode
     *
     * @return Returns the financialObjectTypeCode.
     */
    @Override
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode
     *
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    @Override
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode
     *
     * @return Returns the financialSubObjectCode.
     */
    @Override
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    @Override
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the positionNumber
     *
     * @return Returns the positionNumber.
     */
    @Override
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber
     *
     * @param positionNumber The positionNumber to set.
     */
    @Override
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the transactionDateTimeStamp
     *
     * @return Returns the transactionDateTimeStamp.
     */
    @Override
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp
     *
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    @Override
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the getFinancialBalanceTypeCode
     *
     * @return getFinancialBalanceTypeCode
     * @see org.kuali.kfs.gl.businessobject.Balance#getBalanceTypeCode()
     */

    @Override
    public String getBalanceTypeCode() {
        return this.getFinancialBalanceTypeCode();
    }

    /**
     * Sets the setFinancialBalanceTypeCode
     *
     * @param balanceTypeCode
     * @see org.kuali.kfs.gl.businessobject.Balance#setBalanceTypeCode(java.lang.String)
     */
    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.setFinancialBalanceTypeCode(balanceTypeCode);
    }

    /**
     * Gets the getChartOfAccounts
     *
     * @return getChartOfAccounts
     * @see org.kuali.kfs.gl.businessobject.Balance#getChart()
     */
    @Override
    public Chart getChart() {
        return this.getChartOfAccounts();
    }

    /**
     * Sets the setChartOfAccounts
     *
     * @param chart
     * @see org.kuali.kfs.gl.businessobject.Balance#setChart(org.kuali.kfs.coa.businessobject.Chart)
     */
    @Override
    public void setChart(Chart chart) {
        this.setChartOfAccounts(chart);
    }

    /**
     * Gets the chartOfAccounts
     *
     * @return Returns the chartOfAccounts.
     */
    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Override
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObjectType
     *
     * @return Returns the financialObjectType.
     */
    @Override
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType
     *
     * @param financialObjectType The financialObjectType to set.
     */
    @Override
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Adds amounts in a period.
     *
     * @param period, amount
     * @see org.kuali.kfs.gl.businessobject.Balance#addAmount(java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public void addAmount(String period, KualiDecimal amount) {
        if (KFSConstants.PERIOD_CODE_ANNUAL_BALANCE.equals(period)) {
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.PERIOD_CODE_BEGINNING_BALANCE.equals(period)) {
            this.setFinancialBeginningBalanceLineAmount(this.getFinancialBeginningBalanceLineAmount().add(amount));
            super.getBeginningBalanceLineAmount().add(amount);
        }
        else if (KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE.equals(period)) {
            this.setContractsGrantsBeginningBalanceAmount(this.getContractsGrantsBeginningBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH1.equals(period)) {
            setMonth1Amount(getMonth1Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH2.equals(period)) {
            setMonth2Amount(getMonth2Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH3.equals(period)) {
            setMonth3Amount(getMonth3Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH4.equals(period)) {
            setMonth4Amount(getMonth4Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH5.equals(period)) {
            setMonth5Amount(getMonth5Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH6.equals(period)) {
            setMonth6Amount(getMonth6Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH7.equals(period)) {
            setMonth7Amount(getMonth7Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH8.equals(period)) {
            setMonth8Amount(getMonth8Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH9.equals(period)) {
            setMonth9Amount(getMonth9Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH10.equals(period)) {
            setMonth10Amount(getMonth10Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH11.equals(period)) {
            setMonth11Amount(getMonth11Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH12.equals(period)) {
            setMonth12Amount(getMonth12Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.MONTH13.equals(period)) {
            setMonth13Amount(getMonth13Amount().add(amount));
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
    }

    /**
     * get the amount in the given period.
     *
     * @param periodCode the given period code
     */
    @Override
    public KualiDecimal getAmountByPeriod(String periodCode) {
        if (KFSConstants.PERIOD_CODE_ANNUAL_BALANCE.equals(periodCode)) {
            return this.getAccountLineAnnualBalanceAmount();
        }
        else if (KFSConstants.PERIOD_CODE_BEGINNING_BALANCE.equals(periodCode)) {
            return this.getFinancialBeginningBalanceLineAmount();
        }
        else if (KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE.equals(periodCode)) {
            return this.getContractsGrantsBeginningBalanceAmount();
        }
        else if (KFSConstants.MONTH1.equals(periodCode)) {
            return this.getMonth1Amount();
        }
        else if (KFSConstants.MONTH2.equals(periodCode)) {
            return this.getMonth2Amount();
        }
        else if (KFSConstants.MONTH3.equals(periodCode)) {
            return this.getMonth3Amount();
        }
        else if (KFSConstants.MONTH4.equals(periodCode)) {
            return this.getMonth4Amount();
        }
        else if (KFSConstants.MONTH5.equals(periodCode)) {
            return this.getMonth5Amount();
        }
        else if (KFSConstants.MONTH6.equals(periodCode)) {
            return this.getMonth6Amount();
        }
        else if (KFSConstants.MONTH7.equals(periodCode)) {
            return this.getMonth7Amount();
        }
        else if (KFSConstants.MONTH8.equals(periodCode)) {
            return this.getMonth8Amount();
        }
        else if (KFSConstants.MONTH9.equals(periodCode)) {
            return this.getMonth9Amount();
        }
        else if (KFSConstants.MONTH10.equals(periodCode)) {
            return this.getMonth10Amount();
        }
        else if (KFSConstants.MONTH11.equals(periodCode)) {
            return this.getMonth11Amount();
        }
        else if (KFSConstants.MONTH12.equals(periodCode)) {
            return this.getMonth12Amount();
        }
        else if (KFSConstants.MONTH13.equals(periodCode)) {
            return this.getMonth13Amount();
        }
        else {
            throw new IllegalArgumentException("Unsupport Period Code: " + periodCode);
        }
    }

    /**
     * @see org.kuali.module.effort.bo.LaborLedgerEntry#getLaborLedgerObject()
     */
    @Override
    public LaborLedgerObject getLaborLedgerObject() {
        return this.laborObject;
    }

    /**
     * @see org.kuali.module.effort.bo.LaborLedgerEntry#setLaborLedgerObject(org.kuali.kfs.bo.LaborLedgerObject)
     */
    @Override
    @Deprecated
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject) {
        this.laborObject = (LaborObject) laborLedgerObject;
    }

    /**
     * Gets the laborObject attribute.
     *
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject attribute value.
     *
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }

    /**
     * Gets the Person
     *
     * @return Returns the Person
     */
    @Override
    public Person getLedgerPerson() {
        if( (ledgerPerson == null || !StringUtils.equals(ledgerPerson.getEmployeeId(), emplid)) && StringUtils.isNotBlank(emplid) ) {
            ledgerPerson = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(emplid);
        }

        return ledgerPerson;
    }

    /**
     * Sets the ledgerPerson
     *
     * @param ledgerPerson The ledgerPerson to set.
     */
    @Override
    public void setLedgerPerson(Person ledgerPerson) {
        this.ledgerPerson = ledgerPerson;
    }

    /**
     * construct the primary key list of the business object
     *
     * @return the primary key list of the business object
     */
    public static List<String> getPrimaryKeyList() {
        List<String> primaryKeyList = new ArrayList<String>();
        primaryKeyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        primaryKeyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        primaryKeyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        primaryKeyList.add(KFSPropertyConstants.POSITION_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.EMPLID);

        return primaryKeyList;
    }

    /**
     * Returns true if all amounts except C/G beginning balance are zero; false otherwise.
     */
    public boolean isCGBeginningBalanceOnly() {
        return  getAccountLineAnnualBalanceAmount().isZero() &&
                getBeginningBalanceLineAmount().isZero() &&
                getFinancialBeginningBalanceLineAmount().isZero() &&
        		getMonth1Amount().isZero() &&
                getMonth2Amount().isZero() &&
                getMonth3Amount().isZero() &&
                getMonth4Amount().isZero() &&
                getMonth5Amount().isZero() &&
                getMonth6Amount().isZero() &&
                getMonth7Amount().isZero() &&
                getMonth8Amount().isZero() &&
                getMonth9Amount().isZero() &&
                getMonth10Amount().isZero() &&
                getMonth11Amount().isZero() &&
                getMonth12Amount().isZero() &&
                getMonth13Amount().isZero();
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.Balance#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityFiscalYear());
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, getFinancialObjectCode());
        map.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, getFinancialSubObjectCode());
        map.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, getFinancialBalanceTypeCode());
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, getFinancialObjectTypeCode());
        map.put(KFSPropertyConstants.POSITION_NUMBER, this.getPositionNumber());
        map.put(KFSPropertyConstants.EMPLID, this.getEmplid());
        return map;
    }
}

