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

import org.apache.log4j.Logger;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;

public class PurApLineForm extends KualiForm {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    
    private Integer purchaseOrderIdentifier;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;
    private Integer currentSeqNbr;
    
    private List<PurchasingAccountsPayableDocument> purApDocList;
    
    public PurApLineForm() {
        this.purApDocList = new TypedArrayList(PurchasingAccountsPayableDocument.class);
    }
    
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }
    public List<PurchasingAccountsPayableDocument> getPurApDocList() {
        return purApDocList;
    }
    public void setPurApDocList(List<PurchasingAccountsPayableDocument> purApDocList) {
        this.purApDocList = purApDocList;
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

    public Integer getCurrentSeqNbr() {
        return currentSeqNbr;
    }

    public void setCurrentSeqNbr(Integer currentSeqNbr) {
        this.currentSeqNbr = currentSeqNbr;
    }

}
