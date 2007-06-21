/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.sql.Date;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public interface PurapService {

    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document, 
            String statusToSet, Note statusHistoryNote );
    
    public boolean updateStatus( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document,
            String statusToSet, Note statusHistoryNote );
    
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier);

    public void addBelowLineItems(PurchasingAccountsPayableDocument document);
    
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document);
    
    public List<SourceAccountingLine> generateSummary(List<PurchasingApItem> items);
    
    /**
     * We are obliged not to simply use a dateDiff and compare the result to 365, because we have
     * to worry about leap years.
     * 
     * @param compareDate   An SQL date (not a DateFormatter date, or a util Date)
     * @return  True if the date given for comparison is more than a year in the past, not including today.
     */
    public boolean isDateAYearBeforeToday(Date compareDate);
    
    /*
     *    PURCHASING DOCUMENT METHODS
     * 
     */

    /**
     * This method gets the Automatic Purchase Order Limit amount based first on the derived contract limit
     * (see {@link org.kuali.module.vendor.service.VendorService#getApoLimitFromContract(Integer, String, String)})
     * and if that is null then based on the {@link org.kuali.module.purap.bo.OrganizationParameter} associated
     * with the given 'chart' and 'org' values.
     * 
     * @param vendorContractGeneratedIdentifier
     * @param chart chart code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} 
     * and {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @param org organization code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} 
     * and {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @return a KualiDecimal if a valid limit amount is found or null if one is not found
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org);
}
