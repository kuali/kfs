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
