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
package org.kuali.module.purap.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.CreditMemoDocument;

/**
 * ActionForm for the Credit Memo Document. Stores document values to and from the JSP.
 */
public class CreditMemoForm extends AccountsPayableFormBase {
    private PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine;

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public CreditMemoForm() {
        super();
        setDocument(new CreditMemoDocument());
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());
        setButtons();
    }

    /**
     * @return Returns the internalBillingDocument.
     */
    public CreditMemoDocument getCreditMemoDocument() {
        return (CreditMemoDocument) getDocument();
    }

    /**
     * @param internalBillingDocument The internalBillingDocument to set.
     */
    public void setCreditMemoDocument(CreditMemoDocument creditMemoDocument) {
        setDocument(creditMemoDocument);
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo1()
     */
    public KeyLabelPair getAdditionalDocInfo1() {
        if (ObjectUtils.isNotNull(getCreditMemoDocument().getPurapDocumentIdentifier())) {
            return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.purapDocumentIdentifier", ((CreditMemoDocument) getDocument()).getPurapDocumentIdentifier().toString());
        }
        return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.purapDocumentIdentifier", "Not Available");
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo2()
     */
    public KeyLabelPair getAdditionalDocInfo2() {
        if (ObjectUtils.isNotNull(getCreditMemoDocument().getStatus())) {
            return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.statusCode", ((CreditMemoDocument) getDocument()).getStatus().getStatusDescription());
        }
        return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.statusCode", "Not Available");
    }

    /**
     * @see org.kuali.module.purap.web.struts.form.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurchasingApItem setupNewPurchasingItemLine() {
        return new PurchaseOrderItem();
    }

    public PurchaseOrderVendorStipulation getAndResetNewPurchaseOrderVendorStipulationLine() {
        PurchaseOrderVendorStipulation aPurchaseOrderVendorStipulationLine = getNewPurchaseOrderVendorStipulationLine();
        setNewPurchaseOrderVendorStipulationLine(new PurchaseOrderVendorStipulation());

        // aPurchaseOrderVendorStipulationLine.setDocumentNumber(getPurchaseOrderDocument().getDocumentNumber());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationAuthorEmployeeIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        aPurchaseOrderVendorStipulationLine.setVendorStipulationCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());

        return aPurchaseOrderVendorStipulationLine;
    }

    public PurchaseOrderVendorStipulation getNewPurchaseOrderVendorStipulationLine() {
        return newPurchaseOrderVendorStipulationLine;
    }

    public void setNewPurchaseOrderVendorStipulationLine(PurchaseOrderVendorStipulation newPurchaseOrderVendorStipulationLine) {
        this.newPurchaseOrderVendorStipulationLine = newPurchaseOrderVendorStipulationLine;
    }

    /**
     * @return the CreditMemoInitiated attribute for JSP
     */
    public boolean isCreditMemoInitiated() {
        return StringUtils.equals(this.getCreditMemoDocument().getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE);
    }

    /**
     * Build additional credit memo specific buttons and set extraButtons list.
     */
    private void setButtons() {
        String externalImageURL = SpringServiceLocator.getKualiConfigurationService().getPropertyString(Constants.EXTERNALIZABLE_IMAGES_URL_KEY);
        ExtraButton continueButton = new ExtraButton();
        continueButton.setExtraButtonProperty("methodToCall.continueCreditMemo");
        continueButton.setExtraButtonSource(externalImageURL + "buttonsmall_continue.gif");
        this.getExtraButtons().add(continueButton);
        
        ExtraButton clearButton = new ExtraButton();
        clearButton.setExtraButtonProperty("methodToCall.clearInitFields");
        clearButton.setExtraButtonSource(externalImageURL + "buttonsmall_clear.gif");
        this.getExtraButtons().add(clearButton);
    }

}