/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation;

import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetLocation;
import org.kuali.kfs.module.purap.document.PurchasingDocument;

/**
 * Add Purchasing Capital Asset Location Rule Interface.
 * Defines a rule which gets invoked immediately before a capital asset location is added to a Purchasing Document.
 */
public interface AddPurchasingCapitalAssetLocationRule {

    /**
     * Checks all the business rules relevant to adding a capital asset location
     * 
     * @param location the location to check
     * @param purchasingDocument the PurchasingDocument to check 
     * @return true if the business rules pass
     */
    public boolean processAddCapitalAssetLocationBusinessRules(PurchasingDocument purchasingDocument, PurchasingCapitalAssetLocation location);
}
