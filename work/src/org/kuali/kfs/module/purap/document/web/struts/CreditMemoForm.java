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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.CreditMemoDocument;

/**
 * ActionForm for the Credit Memo Document. Stores document values to and from the JSP.
 */
public class CreditMemoForm extends AccountsPayableFormBase {
    boolean showTotalOverride;

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public CreditMemoForm() {
        super();
        setDocument(new CreditMemoDocument());
        showTotalOverride = false;
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo1()
     */
    public KeyLabelPair getAdditionalDocInfo1() {
        if (ObjectUtils.isNotNull(((CreditMemoDocument) getDocument()).getPurapDocumentIdentifier())) {
            return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.purapDocumentIdentifier", ((CreditMemoDocument) getDocument()).getPurapDocumentIdentifier().toString());
        }
        return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.purapDocumentIdentifier", "Not Available");
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getAdditionalDocInfo2()
     */
    public KeyLabelPair getAdditionalDocInfo2() {
        if (ObjectUtils.isNotNull(((CreditMemoDocument) getDocument()).getStatus())) {
            return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.statusCode", ((CreditMemoDocument) getDocument()).getStatus().getStatusDescription());
        }
        return new KeyLabelPair("DataDictionary.KualiCreditMemoDocument.attributes.statusCode", "Not Available");
    }

    /**
     * @return the CreditMemoInitiated attribute for JSP
     */
    public boolean isCreditMemoInitiated() {
        return StringUtils.equals(((CreditMemoDocument) getDocument()).getStatusCode(), PurapConstants.CreditMemoStatuses.INITIATE);
    }

    /**
     * Build additional credit memo specific buttons and set extraButtons list.
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        // clear out the extra buttons array
        extraButtons.clear();

        CreditMemoDocument cmDocument = (CreditMemoDocument) getDocument();

        String externalImageURL = SpringServiceLocator.getKualiConfigurationService().getPropertyString(Constants.EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringServiceLocator.getKualiConfigurationService().getPropertyString(Constants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY);

        if (getEditingMode().containsKey(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB)) {
            if (getEditingMode().get(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB).equals("TRUE")) {
                addExtraButton("methodToCall.continueCreditMemo", externalImageURL + "buttonsmall_continue.gif", "Continue");
                addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
            }
            else {
                if (SpringServiceLocator.getCreditMemoService().canHoldCreditMemo(cmDocument, GlobalVariables.getUserSession().getUniversalUser())) {
                    addExtraButton("methodToCall.addHoldOnCreditMemo", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
                }
                else if (SpringServiceLocator.getCreditMemoService().canRemoveHoldCreditMemo(cmDocument, GlobalVariables.getUserSession().getUniversalUser())) {
                    addExtraButton("methodToCall.removeHoldFromCreditMemo", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
                }

                // add the calcuate button
                if (PurapConstants.CreditMemoStatuses.IN_PROCESS.equals(cmDocument.getStatusCode())) {
                    addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
                }
            }
        }

        return extraButtons;
    }

    /**
     * Gets the showTotalOverride attribute.
     * 
     * @return Returns the showTotalOverride.
     */
    public boolean isShowTotalOverride() {
        return showTotalOverride;
    }

    /**
     * Sets the showTotalOverride attribute value.
     * 
     * @param showTotalOverride The showTotalOverride to set.
     */
    public void setShowTotalOverride(boolean showTotalOverride) {
        this.showTotalOverride = showTotalOverride;
    }

}