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
package org.kuali.kfs.module.tem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelDocument;
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

    public List<TemSourceAccountingLine> distributionToSouceAccountingLines(List<TemDistributionAccountingLine> distributionAccountingLines, List<AccountingDistribution> accountingDistributionList, KualiDecimal accountingLinesTotal, KualiDecimal expenseLimit);


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
