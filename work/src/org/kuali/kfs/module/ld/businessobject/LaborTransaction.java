/*
 * Copyright 2007 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.core.bo.DocumentType;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.gl.bo.Transaction;

/**
 * Labor business object for Labor Transaction
 */
public interface LaborTransaction extends Transaction {

    public String getPositionNumber();

    public Date getTransactionPostingDate();

    public Date getPayPeriodEndDate();

    public BigDecimal getTransactionTotalHours();

    public Integer getPayrollEndDateFiscalYear();

    public String getPayrollEndDateFiscalPeriodCode();

    public String getFinancialDocumentApprovedCode();

    public String getTransactionEntryOffsetCode();

    public Date getTransactionEntryProcessedTimestamp();

    public String getEmplid();

    public Integer getEmployeeRecord();

    public String getEarnCode();

    public String getPayGroup();

    public String getSalaryAdministrationPlan();

    public String getGrade();

    public String getRunIdentifier();

    public String getLaborLedgerOriginalChartOfAccountsCode();

    public String getLaborLedgerOriginalAccountNumber();

    public String getLaborLedgerOriginalSubAccountNumber();

    public String getLaborLedgerOriginalFinancialObjectCode();

    public String getLaborLedgerOriginalFinancialSubObjectCode();

    public String getHrmsCompany();

    public String getSetid();

    public DocumentType getReferenceFinancialDocumentType();

    public OriginationCode getReferenceFinancialSystemOrigination();

    public AccountingPeriod getPayrollEndDateFiscalPeriod();
}
