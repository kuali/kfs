/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
