/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;

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

    public Timestamp getTransactionEntryProcessedTimestamp();

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

    public DocumentTypeEBO getReferenceFinancialSystemDocumentTypeCode();

    public OriginationCode getReferenceFinancialSystemOrigination();

    public AccountingPeriod getPayrollEndDateFiscalPeriod();
}
