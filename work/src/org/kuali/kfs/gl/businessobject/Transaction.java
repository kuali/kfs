/*
 * Copyright 2005-2007 The Kuali Foundation.
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
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.bo;

import java.sql.Date;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * This interface represents a financial transaction
 */
public interface Transaction {
    public String getAccountNumber();

    public String getFinancialBalanceTypeCode();

    public String getChartOfAccountsCode();

    public String getTransactionDebitCreditCode();

    public String getDocumentNumber();

    public Date getFinancialDocumentReversalDate();

    public String getFinancialDocumentTypeCode();

    public String getTransactionEncumbranceUpdateCode();

    public String getFinancialObjectCode();

    public String getFinancialObjectTypeCode();

    public String getOrganizationDocumentNumber();

    public String getOrganizationReferenceId();

    public String getFinancialSystemOriginationCode();

    public String getProjectCode();

    public String getReferenceFinancialDocumentNumber();

    public String getReferenceFinancialDocumentTypeCode();

    public String getReferenceFinancialSystemOriginationCode();

    public String getSubAccountNumber();

    public String getFinancialSubObjectCode();

    public Date getTransactionDate();

    public Integer getTransactionLedgerEntrySequenceNumber();

    public KualiDecimal getTransactionLedgerEntryAmount();

    public String getTransactionLedgerEntryDescription();

    public String getUniversityFiscalPeriodCode();

    public Integer getUniversityFiscalYear();

    // bo mappings
    public Chart getChart();

    public Account getAccount();

    public ObjectCode getFinancialObject();

    public BalanceTyp getBalanceType();

    public Options getOption();

    public ObjectType getObjectType();

    public void refreshNonUpdateableReferences();

    public SubAccount getSubAccount();

    public SubObjCd getFinancialSubObject();

    public DocumentType getDocumentType();
}
