/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.dao.ojb.PurApAccountingDaoOjb;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.util.SummaryAccount;

/**
 * This class is used to generate Account Summaries for PURAP Module account lists as well as
 * to generate account lists that can be used for distribution to below the line items or any
 * other items that may require distribution
 */
public interface PurapAccountingService {
    
    /**
     * 
     * @deprecated
     * @param accounts
     * @param totalAmount
     * @param percentScale
     * @return
     */
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale);
    
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz);
    
    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(List<PurApAccountingLine> accounts, Integer percentScale);

    public List<SourceAccountingLine> generateSummary(List<PurApItem> items);
//    public Map<SourceAccountingLine, List<PurchasingApItem>> generateSummaryWithItems(List<PurchasingApItem> items);
    
    /**
     * generates a list of summary accounts for a document
     */
    public List<SummaryAccount> generateSummaryAccounts(PurchasingAccountsPayableDocument document);
    
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurApItem> items);
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items);
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurApItem> items, Set includedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes);
    
    public void updateAccountAmounts(PurchasingAccountsPayableDocument document);
    
    public List<PurApAccountingLine> getAccountsFromItem(PurApItem item);
}
