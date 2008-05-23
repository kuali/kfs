/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.service.impl;

import org.kuali.core.util.GlobalVariables;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.FaxService;

public class MockFaxServiceImpl implements FaxService {

    public boolean faxPO(PurchaseOrderDocument po) {
        GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_FAX_TRANSMIT_SERVICE_NOT_IMPLEMENTED);
        return false;
    }

}
