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
package org.kuali.kfs.module.cam.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public class BarcodeInventoryErrorForm extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorForm.class);

    public BarcodeInventoryErrorForm() {
        super();
        LOG.info("*******BarcodeInventoryErrorForm");
        setDocument(new BarcodeInventoryErrorDocument());
    }


    public BarcodeInventoryErrorDocument getBarcodeInventoryErrorDocument() {
        return (BarcodeInventoryErrorDocument) getDocument();
    }
    
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        LOG.info("*******BarcodeInventoryErrorForm - populate");
        BarcodeInventoryErrorDocument ccDoc = getBarcodeInventoryErrorDocument();

        for (BarcodeInventoryErrorDetail detail : ccDoc.getBarcodeInventoryErrorDetail()) {
            LOG.info("******* form:"+detail.getAssetTagNumber());
        }
    }
    
}
