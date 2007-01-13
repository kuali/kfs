/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;

/**
 * This class handles specific Actions requests for the Purchasing Ap.
 */
public class PurchasingActionBase extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingActionBase.class);

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        PurchasingDocumentBase document = (PurchasingDocumentBase)baseForm.getDocument();
        
        //Set a few fields on the delivery tag in a data-dependent manner (KULPURAP-260).
        if (!(Constants.KUALI_LOOKUPABLE_IMPL.equals(baseForm.getRefreshCaller())) && (ObjectUtils.isNotNull(document.isDeliveryBuildingOther()))) {
            if (document.isDeliveryBuildingOther()) {
                document.setDeliveryBuildingName("Other");
                document.setDeliveryBuildingCode("OTH");
                baseForm.setNotOtherDelBldg(false);
            }
            else {
                document.setDeliveryBuildingName(null);
                document.setDeliveryBuildingCode(null);
                baseForm.setNotOtherDelBldg(true);
            }
        }
        return super.refresh(mapping, form, request, response);
    }

    /**
     * Add a new item to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call add line event/rules here
        PurchasingItem item = purchasingForm.getAndResetNewPurchasingItemLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.addItem(item);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Delete an item from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call delete line event/rules here

        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.deleteItem(getSelectedLine(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

}