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
package org.kuali.kfs.module.purap.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kuluser;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = kuluser, shouldCommitTransactions=true)
public class PurapAccountingServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Stub method; add initiations of services in here.
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Stub method; null out services here.
        super.tearDown();
    }
    
    /*
     * Tests of generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz)
     */
    
    /*
     * Tests of generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapdoc)
     */
    
    /*
     * Tests of generateSummaryAccounts(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummary(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotals(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes)
     */
    
    /*
     * Tests of updateAccountAmounts(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of updateItemAccountAmounts(PurApItem item)
     */
    
    /*
     * Tests of getAccountsFromItem(PurApItem item)
     */
    
    /*
     * Tests of deleteSummaryAccounts(Integer purapDocumentIdentifier)
     */
    
    /*
     * Tests of generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of convertMoneyToPercent(PaymentRequestDocument pr)
     */
    
    /*
     * Tests of generateUseTaxAccount(PurchasingAccountsPayableDocument document)
     */
}
