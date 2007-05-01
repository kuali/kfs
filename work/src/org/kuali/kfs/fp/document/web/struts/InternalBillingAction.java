/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.financial.bo.InternalBillingItem;
import org.kuali.module.financial.web.struts.form.InternalBillingForm;

/**
 * This class handles Actions for InternalBilling.
 */
public class InternalBillingAction extends KualiAccountingDocumentActionBase {

    /**
     * Adds a new InternalBillingItem from the Form to the Document if valid. This method is called reflectively from KualiAction.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InternalBillingForm internalBillingForm = (InternalBillingForm) form;
        if (validateNewItem(internalBillingForm)) {
            internalBillingForm.getInternalBillingDocument().addItem(internalBillingForm.getNewItem());
            internalBillingForm.setNewItem(new InternalBillingItem());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Validates the new InternalBillingItem on the Form, adding a global error if invalid.
     * 
     * @param internalBillingForm
     * @return whether the new item is valid
     */
    private static boolean validateNewItem(InternalBillingForm internalBillingForm) {
        return SpringServiceLocator.getDictionaryValidationService().isBusinessObjectValid(internalBillingForm.getNewItem(), KFSPropertyConstants.NEW_ITEM);
    }

    /**
     * Deletes an InternalBillingItem from the Document. This method is called reflectively from KualiAction.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InternalBillingForm internalBillingForm = (InternalBillingForm) form;
        internalBillingForm.getInternalBillingDocument().getItems().remove(getLineToDelete(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}