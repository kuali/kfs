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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.document.CreditCardReceiptDocument;
import org.kuali.module.financial.rules.AdvanceDepositDocumentRuleUtil;
import org.kuali.module.financial.web.struts.form.AdvanceDepositForm;
import org.kuali.module.financial.web.struts.form.CreditCardReceiptForm;

/**
 * This is the action class for the Advance Deposit document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AdvanceDepositAction extends KualiTransactionalDocumentActionBase {
    /**
     * Adds handling for advance deposit detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;

        if (adForm.hasDocumentId()) {
            AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

            adDoc.setTotalAdvanceDepositAmount(calculateAdvanceDepositTotal(adDoc)); // recalc b/c changes to the amounts could have happened
        }

        // proceed as usual
        return super.execute(mapping, form, request, response);
    }
    
    /**
     * Adds a AdvanceDepositDetail instance created from the current "new advanceDeposit" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addAdvanceDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;
        AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

        AdvanceDepositDetail newAdvanceDeposit = adForm.getNewAdvanceDeposit();
        adDoc.prepareNewAdvanceDeposit(newAdvanceDeposit);

        // advanceDeposit business rules
        boolean rulePassed = validateNewAdvanceDeposit(newAdvanceDeposit);
        if (rulePassed) {
            // add advanceDeposit
            adDoc.addAdvanceDeposit(newAdvanceDeposit);

            // clear the used advanceDeposit
            adForm.setNewAdvanceDeposit(new AdvanceDepositDetail());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected advanceDeposit (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAdvanceDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;
        AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

        int deleteIndex = getLineToDelete(request);
        // delete advanceDeposit
        adDoc.removeAdvanceDeposit(deleteIndex);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method validates a new advance deposit detail record.
     * 
     * @param advanceDeposit
     * @return boolean
     */
    private boolean validateNewAdvanceDeposit(AdvanceDepositDetail advanceDeposit) {
        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.NEW_ADVANCE_DEPOSIT);
        boolean isValid = AdvanceDepositDocumentRuleUtil.validateAdvanceDeposit(advanceDeposit);
        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.NEW_ADVANCE_DEPOSIT);
        return isValid;
    }
    
    /**
     * Recalculates the advance deposit total since user could have changed it during their update.
     * 
     * @param advanceDepositDocument
     */
    private KualiDecimal calculateAdvanceDepositTotal(AdvanceDepositDocument advanceDepositDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        Iterator<AdvanceDepositDetail> deposits = advanceDepositDocument.getAdvanceDeposits().iterator();
        while(deposits.hasNext()) {
            AdvanceDepositDetail deposit = deposits.next();
            total = total.add(deposit.getFinancialDocumentAdvanceDepositAmount());
        }
        return total;
    }
 }