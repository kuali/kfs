/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document;

import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteLanguage;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;

/* 
 * A special implementation of Maintainable specifically for PurchaseOrderQuoteLanguage
 * maintenance page to override the behavior when the PurchaseOrderQuoteLanguage 
 * maintenance document is copied.
*/
public class PurchaseOrderQuoteLanguageMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Overrides the method in KualiMaintainableImpl to invoke the
     * initializePoQuoteLanguage to set the create date to the current date.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        intializePoQuoteLangauge();
        super.processAfterCopy(document, parameters);
    }

    /**
     * Sets the create date of the PurchaseOrderQuoteLanguage document to the 
     * current date.
     */
    private void intializePoQuoteLangauge() {
        // set create date
        PurchaseOrderQuoteLanguage poql = (PurchaseOrderQuoteLanguage) super.getBusinessObject();
        poql.setPurchaseOrderQuoteLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }
}
