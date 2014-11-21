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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface PaymentSummaryService {
    /**
     * @param asset Asset that needs Payment Summary Information computed
     *        <ol>
     *        Computes following information and sets them to asset object
     *        <li>Federal contribution amount</li>
     *        <li>Payments till date</li>
     *        <li>Total Cost of Asset</li>
     *        <li>Accumulated Depreciation</li>
     *        <li>Primary Base Amount</li>
     *        <li>Book Value</li>
     *        <li>Previous Year Depreciation</li>
     *        <li>Year to Date Depreciation</li>
     *        <li>Current month depreciation</li>
     *        </ol>
     */
    void calculateAndSetPaymentSummary(Asset asset);

    /**
     * Sums up federal contribution amount for an asset
     * 
     * @param asset Asset
     * @return Federal Contribution Amount
     */
    KualiDecimal calculateFederalContribution(Asset asset);
    
    KualiDecimal calculatePrimaryAccumulatedDepreciation(Asset asset);
    
    KualiDecimal calculatePrimaryBookValue(Asset asset);
    
    KualiDecimal calculatePaymentTotalCost(Asset asset);
}
