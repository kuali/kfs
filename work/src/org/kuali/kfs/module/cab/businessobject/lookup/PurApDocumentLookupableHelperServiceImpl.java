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
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.util.Properties;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * This class overrids the base getActionUrls method
 */
public class PurApDocumentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApDocumentLookupableHelperServiceImpl.class);

    /**
     * Custom action urls for CAB PurAp lines.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        PurchasingAccountsPayableDocument purApDoc = (PurchasingAccountsPayableDocument) bo;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, CabConstants.PURAP_START_METHOD);
        parameters.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, purApDoc.getPurchaseOrderIdentifier().toString());

        String url = UrlFactory.parameterizeUrl(CabConstants.CB_INVOICE_LINE_ACTION, parameters);

        return url = "<a href=\"../" + url + "\">" + CabConstants.Actions.PROCESS + "</a>";
    }

}
