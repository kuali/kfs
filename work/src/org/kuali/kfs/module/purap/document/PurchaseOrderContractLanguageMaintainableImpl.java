/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * A special implementation of Maintainable specifically for PurchaseOrderContractLanguage
 * maintenance page to override the behavior when the PurchaseOrderContractLanguage 
 * maintenance document is copied.
 */
public class PurchaseOrderContractLanguageMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Overrides the method in KualiMaintainableImpl to set the contract language create date
     * to current date.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        PurchaseOrderContractLanguage pocl = (PurchaseOrderContractLanguage) super.getBusinessObject();
        pocl.setContractLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        super.processAfterCopy(document, parameters);
    }

}
