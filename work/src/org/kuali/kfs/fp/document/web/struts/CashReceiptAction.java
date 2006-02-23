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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.rule.event.AddCheckEvent;
import org.kuali.core.rule.event.DeleteCheckEvent;
import org.kuali.core.rule.event.UpdateCheckEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.web.struts.form.CashReceiptForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashReceiptAction extends KualiTransactionalDocumentActionBase {
    /**
     * Adds handling for check updates
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        CashReceiptForm cform = (CashReceiptForm) form;

        // handle changes to checks (but only if the checkEntryMode is 'detail')
        if (cform.hasDocumentId()) {
            CashReceiptDocument cdoc = cform.getCashReceiptDocument();

            if (CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(cdoc.getCheckEntryMode())) {
                processChecks(cdoc, cform);
            }
        }

        // proceed as usual
        return super.execute(mapping, form, request, response);
    }

    private void processChecks(CashReceiptDocument cdoc, CashReceiptForm cform) {
        List baseChecks = cform.getBaselineChecks();
        List formChecks = cdoc.getChecks();

        Map baseCheckMap = new HashMap();
        for (Iterator i = baseChecks.iterator(); i.hasNext();) {
            Check check = (Check) i.next();
            baseCheckMap.put(check.getSequenceId(), check);
        }

        // find and process corresponding form and base checks
        int index = 0;
        for (Iterator i = formChecks.iterator(); i.hasNext(); index++) {
            Check formCheck = (Check) i.next();
            Check baseCheck = (Check) baseCheckMap.get(formCheck.getSequenceId());

            // only generate update events for specific action methods
            String methodToCall = cform.getMethodToCall();
            if (UPDATE_EVENT_ACTIONS.contains(methodToCall)) {
                handleUpdate(cdoc, "check[" + index + "]", formCheck, baseCheck);
            }
        }
    }

    private void handleUpdate(CashReceiptDocument cdoc, String errorPathPrefix, Check formCheck, Check baseCheck) {
        if ((baseCheck != null) && !formCheck.isLike(baseCheck)) {
            SpringServiceLocator.getKualiRuleService()
                    .applyRules(new UpdateCheckEvent(errorPathPrefix, cdoc, baseCheck, formCheck));
        }
    }


    /**
     * Adds Check instance created from the current "new check" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        Check newCheck = crForm.getNewCheck();
        newCheck.setFinancialDocumentNumber(crDoc.getFinancialDocumentNumber());

        // check business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(
                new AddCheckEvent(Constants.NEW_CHECK_PROPERTY_NAME, crDoc, newCheck));
        if (rulePassed) {
            // add check
            crDoc.addCheck(newCheck);

            // clear the used newCheck
            crForm.setNewCheck(new CheckBase());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected check (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        int deleteIndex = getLineToDelete(request);
        Check oldCheck = crDoc.getCheck(deleteIndex);


        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(
                new DeleteCheckEvent(Constants.EXISTING_CHECK_PROPERTY_NAME, crDoc, oldCheck));

        if (rulePassed) {
            // delete check
            crDoc.removeCheck(deleteIndex);

            // delete baseline check, if any
            if (crForm.hasBaselineCheck(deleteIndex)) {
                crForm.getBaselineChecks().remove(deleteIndex);
            }
        }
        else {
            GlobalVariables.getErrorMap().put("document.check[" + deleteIndex + "]", KeyConstants.Check.ERROR_CHECK_DELETERULE,
                    Integer.toString(deleteIndex));
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Changes the current check-entry mode, if necessary
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward changeCheckEntryMode(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        String formMode = crForm.getCheckEntryMode();
        String docMode = crDoc.getCheckEntryMode();

        if (CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(formMode) || CashReceiptDocument.CHECK_ENTRY_TOTAL.equals(formMode)) {
            if (!formMode.equals(docMode)) {

                if (formMode.equals(CashReceiptDocument.CHECK_ENTRY_DETAIL)) {
                    // save current checkTotal, for future restoration
                    crForm.setCheckTotal(crDoc.getTotalCheckAmount());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);
                    crDoc.setTotalCheckAmount(crDoc.calculateCheckTotal());

                    // notify user
                    GlobalVariables.getMessageList().add(KeyConstants.CashReceipt.MSG_CHECK_ENTRY_INDIVIDUAL);
                }
                else {
                    // restore saved checkTotal
                    crDoc.setTotalCheckAmount(crForm.getCheckTotal());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);

                    // notify user
                    GlobalVariables.getMessageList().add(KeyConstants.CashReceipt.MSG_CHECK_ENTRY_TOTAL);
                }
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        initDerivedCheckValues((CashReceiptForm) kualiDocumentFormBase);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        initDerivedCheckValues((CashReceiptForm) kualiDocumentFormBase);
    }

    /**
     * Initializes form values which must be derived form document contents (i.e. those which aren't directly available from the
     * document)
     * 
     * @param cform
     */
    private void initDerivedCheckValues(CashReceiptForm cform) {
        CashReceiptDocument cdoc = cform.getCashReceiptDocument();

        cform.setCheckEntryMode(cdoc.getCheckEntryMode());
        cform.setCheckTotal(cdoc.getTotalCheckAmount());

        cform.getBaselineChecks().clear();
        cform.getBaselineChecks().addAll(cform.getCashReceiptDocument().getChecks());
    }
}
