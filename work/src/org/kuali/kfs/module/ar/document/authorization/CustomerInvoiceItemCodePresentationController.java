/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class CustomerInvoiceItemCodePresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropNames = super.getConditionallyReadOnlyPropertyNames(document);
        
        //  On an edit of an existing ItemCode, the billing org is always readonly
        if (document.isEdit()) {
            readOnlyPropNames.add(ArPropertyConstants.CustomerInvoiceItemCodes.CHART_OF_ACCOUNTS_CODE);
            readOnlyPropNames.add(ArPropertyConstants.CustomerInvoiceItemCodes.ORGANIZATION_CODE);
        }
        
        return readOnlyPropNames;
    }

}
