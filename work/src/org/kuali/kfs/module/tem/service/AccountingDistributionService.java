/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface AccountingDistributionService {
    
    /**
     * For the purposes of user interface domain, creates a {@link Collection} of {@link AccountingDistribution} instances
     * that are displayed to the user. The user can then choose one to distribute as accounting lines, but then the {@link Collection}
     * must be updated and rebuilt.
     *
     * @param reimbursement instance of a {@link TravelDocument} to distribute expenses from
     * @return {@link Collection} of {@link AccountingDistribution} instances
     */
    List<AccountingDistribution> buildDistributionFrom(final TravelDocument document);
    
    /**
     * Create the distributions for all the expenses in the travel document 
     * 
     * @param travelDocument
     * @return
     */
    public List<AccountingDistribution> createDistributions(TravelDocument travelDocument);
    
    public TemDistributionAccountingLine distributionToDistributionAccountingLine(List<AccountingDistribution> accountingDistributionList);
    
    public List<TemSourceAccountingLine> distributionToSouceAccountingLines(List<TemDistributionAccountingLine> distributionAccountingLines, List<AccountingDistribution> accountingDistributionList, Integer sequenceNumber);
    
    //TODO: remove when sure.
    //public void normalizeAmountAndPercents(List<TemDistributionAccountingLine> lines, KualiDecimal remainingTotal);

    public KualiDecimal getTotalAmount(List<TemDistributionAccountingLine> lines);
    
    public BigDecimal getTotalPercent(List<TemDistributionAccountingLine> lines);
    
    /**
     * From the accounting line list, calculate the percentage of each of the accounting line distribution.
     * 
     * This does not take into consideration of card type in TEM accounting lines, so the lines must be pre-filtered when used for
     * a particular card type
     * 
     * @param accountingLine
     * @return
     */
    public Map<AccountingLineDistributionKey, KualiDecimal> calculateAccountingLineDistributionPercent(List<SourceAccountingLine> accountingLine);
}
