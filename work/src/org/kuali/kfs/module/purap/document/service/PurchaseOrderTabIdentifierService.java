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
package org.kuali.kfs.module.purap.document.service;

import java.util.List;

import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;

/**
 *
 * This interface defines the methods for identifying the tab that is modified
 */
public interface PurchaseOrderTabIdentifierService {

    /**
     *
     * This method determines whether the vendor tab is modified
     * @param newDoc
     * @return
     */
    public boolean isVendorTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the Stipulation tab is modified
     * @param newDoc
     * @return
     */
    public boolean isStipulationTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the additional tab is modified
     * @param newDoc
     * @return
     */
    public boolean isAdditionalTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the Delivery tab is modified
     * @param newDoc
     * @return
     */
    public boolean isDeliveryTabModifed(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the payment tab is modified
     * @param newDoc
     * @return
     */
    public boolean isPaymentTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the Quote tab is modified
     * @param newDoc
     * @return
     */
    public boolean isQuoteTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the cams tab is modified
     * @param newDoc
     * @return
     */
    public boolean isCamsTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method determines whether the items tab is modified
     * @param newDoc
     * @return
     */
    public boolean isItemsTabModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method any tab other then items tabs is modified
     * @param newDoc
     * @return
     */
    public boolean isAnyTabsModified(PurchaseOrderAmendmentDocument newDoc) ;

    /**
     *
     * This method returns the list of modified tabs
     * @param newDoc
     * @return
     */
    public List<String> getModifiedTabs(PurchaseOrderAmendmentDocument newDoc);
}
