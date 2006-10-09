/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import static org.kuali.core.util.SpringServiceLocator.getDictionaryValidationService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.module.financial.bo.InternalBillingItem;
import org.kuali.module.financial.web.struts.form.InternalBillingForm;

/**
 * This class handles Actions for InternalBilling.
 * 
 * 
 */


public class InternalBillingAction extends KualiTransactionalDocumentActionBase {

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
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Validates the new InternalBillingItem on the Form, adding a global error if invalid.
     * 
     * @param internalBillingForm
     * @return whether the new item is valid
     */
    private static boolean validateNewItem(InternalBillingForm internalBillingForm) {
        return getDictionaryValidationService().isBusinessObjectValid(internalBillingForm.getNewItem(), PropertyConstants.NEW_ITEM);
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
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}