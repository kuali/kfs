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
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;

/**
 * This class is used to generate Account Summaries for PURAP Module account lists as well as
 * to generate account lists that can be used for distribution to below the line items or any
 * other items that may require distribution
 */
public interface PurapAccountingService {

    public List<PurApAccountingLine> generateAccountDistributionForProration(List<PurApAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale);

    public List<PurApAccountingLine> generateAccountDistributionForProrationWithZeroTotal(List<PurApAccountingLine> accounts, Integer percentScale);

    public List<SourceAccountingLine> generateSummary(List<PurchasingApItem> items);
    public List<SourceAccountingLine> generateSummaryWithNoZeroTotals(List<PurchasingApItem> items);
    public List<SourceAccountingLine> generateSummaryExcludeItemTypes(List<PurchasingApItem> items, Set excludedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurchasingApItem> items, Set excludedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryIncludeItemTypes(List<PurchasingApItem> items, Set includedItemTypeCodes);
    public List<SourceAccountingLine> generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurchasingApItem> items, Set includedItemTypeCodes);
}
