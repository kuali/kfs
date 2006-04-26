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
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.CreditCardDetail;
import org.kuali.module.financial.document.CreditCardReceiptDocument;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.web.struts.form.CreditCardReceiptForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CreditCardReceiptAction extends KualiTransactionalDocumentActionBase {
    /**
     * Adds a CreditCardDetail instance created from the current "new creditCardReceipt" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCreditCardReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CreditCardReceiptForm ccrForm = (CreditCardReceiptForm) form;
        CreditCardReceiptDocument ccrDoc = ccrForm.getCreditCardReceiptDocument();

        CreditCardDetail newCreditCardReceipt = ccrForm.getNewCreditCardReceipt();
        ccrDoc.prepareNewCreditCardReceipt(newCreditCardReceipt);

        // creditCardReceipt business rules
        boolean rulePassed = validateNewCreditCardReceipt(newCreditCardReceipt);
        if (rulePassed) {
            // add creditCardReceipt
            ccrDoc.addCreditCardReceipt(newCreditCardReceipt);

            // clear the used creditCardReceipt
            ccrForm.setNewCreditCardReceipt(new CreditCardDetail());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        CreditCardReceiptDocument crDoc = (CreditCardReceiptDocument)kualiDocumentFormBase.getDocument();

        CashReceiptService crs = SpringServiceLocator.getCashReceiptService();
        String verificationUnit = crs.getCashReceiptVerificationUnit(GlobalVariables.getUserSession().getKualiUser());
        String campusCode = crs.getCampusCodeForCashReceiptVerificationUnit(verificationUnit);
        crDoc.setCampusLocationCode(campusCode);
    }

    /**
     * Deletes the selected creditCardReceipt (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteCreditCardReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CreditCardReceiptForm ccrForm = (CreditCardReceiptForm) form;
        CreditCardReceiptDocument ccrDoc = ccrForm.getCreditCardReceiptDocument();

        int deleteIndex = getLineToDelete(request);
        // delete creditCardReceipt
        ccrDoc.removeCreditCardReceipt(deleteIndex);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method validates a new credit card receipt detail record.
     * 
     * @param creditCardReceipt
     * @return boolean
     */
    private boolean validateNewCreditCardReceipt(CreditCardDetail creditCardReceipt) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        errorMap.addToErrorPath(PropertyConstants.NEW_CREDIT_CARD_RECEIPT);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(creditCardReceipt);
        errorMap.removeFromErrorPath(PropertyConstants.NEW_CREDIT_CARD_RECEIPT);
        int currentErrorCount = errorMap.getErrorCount();
        return currentErrorCount == originalErrorCount;
    }
}