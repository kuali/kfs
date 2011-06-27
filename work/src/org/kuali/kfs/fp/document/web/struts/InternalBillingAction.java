/*
 * Copyright 2005 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.InternalBillingItem;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DictionaryValidationService;

/**
 * This class handles Actions for InternalBilling.
 */
public class InternalBillingAction extends CapitalAccountingLinesActionBase {

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
    protected static boolean validateNewItem(InternalBillingForm internalBillingForm) {
        return SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(internalBillingForm.getNewItem(), KFSPropertyConstants.NEW_ITEM);
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
