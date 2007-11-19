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
package org.kuali.module.purap.maintenance;

import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.PurchaseOrderContractLanguage;

/**
 * A special implementation of Maintainable specifically for PurchaseOrderContractLanguage
 * maintenance page to override the behavior when the PurchaseOrderContractLanguage 
 * maintenance document is copied.
 */
public class PurchaseOrderContractLanguageMaintainableImpl extends KualiMaintainableImpl {

    /**
     * Overrides the method in KualiMaintainableImpl to set the contract language create date
     * to current date.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy() {
        PurchaseOrderContractLanguage pocl = (PurchaseOrderContractLanguage) super.getBusinessObject();
        pocl.setContractLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }

}
