/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.document;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

public class AssetPaymentDocument extends AccountingDocumentBase {
    private Long   capitalAssetNumber;
    private String representativeUniversalIdentifier;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private String campusCode;
    private String buildingCode;
    private Integer nextCapitalAssetPaymentLineNumber;

    private DocumentHeader  documentHeader;
    private Account         organizationOwnerAccount;
    private Chart           organizationOwnerChartOfAccounts;
    private Campus          campus;
    private Building        building;

    
    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    @Override
    public boolean isDebit(GeneralLedgerPostable postable) {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return CustomerInvoiceDetail.class;
    }
    
    /**
     * Still need implement.
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @return true if there are no issues creating GLPE's
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     *
    @Override
    public void processGenerateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        //TODO Still need to implement.
    } */       
}
