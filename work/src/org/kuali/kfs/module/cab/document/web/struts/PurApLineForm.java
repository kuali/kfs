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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class PurApLineForm extends KualiForm {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    private String purchaseOrderIdentifier;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;
    
    private List<PurchasingAccountsPayableDocument> purApDocs;
    
    public PurApLineForm() {
        this.purApDocs = new TypedArrayList(PurchasingAccountsPayableDocument.class);
    }

    public String getPurApContactEmailAddress() {
        return purApContactEmailAddress;
    }

    public void setPurApContactEmailAddress(String purApContactEmailAddress) {
        this.purApContactEmailAddress = purApContactEmailAddress;
    }

    public String getPurApContactPhoneNumber() {
        return purApContactPhoneNumber;
    }

    public void setPurApContactPhoneNumber(String purApContactPhoneNumber) {
        this.purApContactPhoneNumber = purApContactPhoneNumber;
    }

    @Override
    public void populate(HttpServletRequest request) {
        // TODO Auto-generated method stub
        super.populate(request);
    }


    public List<PurchasingAccountsPayableDocument> getPurApDocs() {
        return purApDocs;
    }

    public void setPurApDocs(List<PurchasingAccountsPayableDocument> purApDocs) {
        this.purApDocs = purApDocs;
    }

    public String getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(String purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

}
