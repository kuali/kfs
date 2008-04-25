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
package org.kuali.module.purap.rule;

import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public interface ValidateCapitalAssetsForAutomaticPurchaseOrderRule<P extends PurchasingAccountsPayableDocument>   {

    /**
     * Provides a hook for the APO service logic to call the capital asset validations.
     * 
     * @param purapDocument     A document type extending PurchasingAccountsPayableDocument
     * @return  True if none of the capital asset validations fails for this document.
     */
    public boolean processCapitalAssetsForAutomaticPurchaseOrderRule(P purapDocument);
}