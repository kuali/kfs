/*
 * Copyright 2005 The Kuali Foundation
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
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.kfs.gl.businessobject;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This interface represents a financial transaction
 */
public interface Transaction extends BusinessObject {
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

    public BalanceType getBalanceType();

    public SystemOptions getOption();

    public ObjectType getObjectType();

    public void refreshNonUpdateableReferences();

    public SubAccount getSubAccount();

    public SubObjectCode getFinancialSubObject();

    public DocumentTypeEBO getFinancialSystemDocumentTypeCode();
    
    public void setChart(Chart chart);

    public void setAccount(Account account);

    public void setFinancialObject(ObjectCode objectCode);

    public void setBalanceType(BalanceType balanceTyp);

    public void setOption(SystemOptions options);

    public void setObjectType(ObjectType objectType);
}
