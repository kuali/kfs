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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class PurApLineForm extends KualiForm {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    private String purchaseOrderIdentifier;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;

    private List<PurchasingAccountsPayableDocument> purApDocs;
    private int actionPurApDocIndex;
    private int actionItemAssetIndex;

    private KualiDecimal mergeQty;
    private String mergeDesc;
    
    public PurApLineForm() {
        this.purApDocs = new TypedArrayList(PurchasingAccountsPayableDocument.class);
    }


    public String getMergeDesc() {
        return mergeDesc;
    }


    public void setMergeDesc(String mergeDesc) {
        this.mergeDesc = mergeDesc;
    }


    public KualiDecimal getMergeQty() {
        return mergeQty;
    }


    public void setMergeQty(KualiDecimal mergeQty) {
        this.mergeQty = mergeQty;
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
        super.populate(request);

        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            // populate collection index
            String purApDocIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_DOC, ".");
            if (StringUtils.isNotBlank(purApDocIndex)) {
                this.setActionPurApDocIndex(Integer.parseInt(purApDocIndex));
            }
            String itemAssetIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_LINE, ".");
            if (StringUtils.isNotBlank(itemAssetIndex)) {
                this.setActionItemAssetIndex(Integer.parseInt(itemAssetIndex));
            }
        }
    }


    public int getActionPurApDocIndex() {
        return actionPurApDocIndex;
    }


    public void setActionPurApDocIndex(int purApDocIndex) {
        this.actionPurApDocIndex = purApDocIndex;
    }


    public int getActionItemAssetIndex() {
        return actionItemAssetIndex;
    }


    public void setActionItemAssetIndex(int itemAssetIndex) {
        this.actionItemAssetIndex = itemAssetIndex;
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
