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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.labor.service.LaborUserService;

/**
 * Represents a balance from labor ledger balance table.
 */
public class LedgerBalance extends Balance {

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
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;

        // Try to find a ledger person for this emplid if one exists
        try {
            setLedgerPerson(((LaborUserService) SpringServiceLocator.getService(LABOR_USER_SERVICE_NAME)).getLaborUserByPersonPayrollIdentifier(emplid).getUniversalUser());
        }
        catch (UserNotFoundException unfe) {
            // The user is not valid. We don't have a user
            setLedgerPerson(null);
        }
    }

    /**
     * Gets the financialBalance attribute.
     * 
     * @return Returns the financialBalance.
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute value.
     * 
     * @param financialBalance The financialBalance to set.
     */
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialBeginningBalanceLineAmount attribute.
     * 
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * Sets the financialBeginningBalanceLineAmount attribute value.
     * 
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp.
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute value.
     * 
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
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObjectType attribute.
     * 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * 
     * @param financialObjectType The financialObjectType to set.
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }


    @Override
    public void addAmount(String period, KualiDecimal amount) {
        if (KFSConstants.PERIOD_CODE_ANNUAL_BALANCE.equals(period)) {
            this.setAccountLineAnnualBalanceAmount(this.getAccountLineAnnualBalanceAmount().add(amount));
        }
        else if (KFSConstants.PERIOD_CODE_BEGINNING_BALANCE.equals(period)) {
            this.setFinancialBeginningBalanceLineAmount(this.getFinancialBeginningBalanceLineAmount().add(amount));
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
     * Retrieve the associated <code>{@link LaborObject}</code> linked by chart of accounts code, financial object code, and
     * university fiscal year
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

    /**
     * construct the primary key list of the business object
     * 
     * @return the primary key list of the business object
     */
    public List<String> getPrimaryKeyList() {
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
}
