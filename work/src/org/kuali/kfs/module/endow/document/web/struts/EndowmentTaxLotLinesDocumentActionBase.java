/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import static org.kuali.kfs.module.endow.EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.validation.event.DeleteTaxLotLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.RefreshTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.KRADConstants;

public abstract class EndowmentTaxLotLinesDocumentActionBase extends EndowmentTransactionLinesDocumentActionBase {

    /**
     * Updates the tax lots for the given transaction line.
     * 
     * @param isSource
     * @param etlDocument
     * @param transLine
     */
    protected abstract void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine);

    /**
     * Updates the tax lots for the given document.
     * 
     * @param isSource
     * @param etlDocument
     */
    protected void updateTaxLots(EndowmentTransactionLinesDocument etlDocument) {


        if (etlDocument.getSourceTransactionLines() != null) {
            for (int i = 0; i < etlDocument.getSourceTransactionLines().size(); i++) {
                EndowmentTransactionLine endowmentTransactionLine = (EndowmentTransactionLine) etlDocument.getSourceTransactionLines().get(i);
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, etlDocument, endowmentTransactionLine, i));

                if (rulePassed) {
                    updateTransactionLineTaxLots(true, true, etlDocument, endowmentTransactionLine);
                }

            }
        }

        if (etlDocument.getTargetTransactionLines() != null) {
            for (int i = 0; i < etlDocument.getTargetTransactionLines().size(); i++) {
                EndowmentTransactionLine endowmentTransactionLine = (EndowmentTransactionLine) etlDocument.getTargetTransactionLines().get(i);
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME, etlDocument, endowmentTransactionLine, i));

                if (rulePassed) {
                    updateTransactionLineTaxLots(true, false, etlDocument, endowmentTransactionLine);
                }
            }
        }
    }

    /**
     * Refreshes the tax lots for the selected target transaction line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshTargetTaxLots(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();
        int selectedLine = this.getSelectedLine(request);
        EndowmentTransactionLine transLine = endowmentDocument.getTargetTransactionLines().get(selectedLine);

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine, selectedLine));

        if (rulePassed) {
            updateTransactionLineTaxLots(false, false, endowmentDocument, transLine);
        }

        if (endowmentDocument instanceof AmountTotaling)
            ((FinancialSystemDocumentHeader) documentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) endowmentDocument).getTotalDollarAmount());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refreshes the tax lots for the selected source transaction line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshSourceTaxLots(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();
        int selectedLine = this.getSelectedLine(request);
        EndowmentTransactionLine transLine = endowmentDocument.getSourceTransactionLines().get(selectedLine);

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine, selectedLine));

        if (rulePassed) {
            updateTransactionLineTaxLots(false, true, endowmentDocument, transLine);
        }

        if (endowmentDocument instanceof AmountTotaling)
            ((FinancialSystemDocumentHeader) documentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) endowmentDocument).getTotalDollarAmount());


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#insertTransactionLine(boolean,
     *      org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void insertTransactionLine(boolean isSource, EndowmentTransactionLinesDocumentFormBase etlDocumentForm, EndowmentTransactionLine line) {
        EndowmentTransactionLinesDocumentBase etlDoc = etlDocumentForm.getEndowmentTransactionLinesDocumentBase();

        super.insertTransactionLine(isSource, etlDocumentForm, line);

        updateTransactionLineTaxLots(false, isSource, etlDoc, line);
    }

    /**
     * Deletes a source tax lot line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteSourceTaxLotLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTaxLotLinesDocument etlDoc = (EndowmentTaxLotLinesDocument) etlForm.getEndowmentTransactionLinesDocumentBase();

        int transLineindex = getLineToDelete(request);
        int taxLotIndex = getTaxLotToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME + "[" + transLineindex + "]";
        EndowmentTransactionLine transLine = (EndowmentTransactionLine) etlDoc.getSourceTransactionLines().get(transLineindex);
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteTaxLotLineEvent(errorPath, etlDoc, transLine.getTaxLotLines().get(taxLotIndex), transLine, transLineindex, taxLotIndex));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteTaxLot(true, etlForm, transLineindex, taxLotIndex);
            updateTransactionLineTaxLots(true, true, etlDoc, transLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a target tax lot line
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTargetTaxLotLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTaxLotLinesDocument etlDoc = (EndowmentTaxLotLinesDocument) etlForm.getEndowmentTransactionLinesDocumentBase();

        int transLineindex = getLineToDelete(request);
        int taxLotIndex = getTaxLotToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME + "[" + transLineindex + "]";
        EndowmentTransactionLine transLine = (EndowmentTransactionLine) etlDoc.getTargetTransactionLines().get(transLineindex);
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteTaxLotLineEvent(errorPath, etlDoc, transLine.getTaxLotLines().get(taxLotIndex), transLine, transLineindex, taxLotIndex));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteTaxLot(false, etlForm, transLineindex, taxLotIndex);
            updateTransactionLineTaxLots(true, false, etlDoc, transLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a tax lot.
     * 
     * @param isSource
     * @param etlDocumentForm
     * @param transLineindex
     * @param taxLotIndex
     */
    private void deleteTaxLot(boolean isSource, EndowmentTransactionLinesDocumentFormBase etlDocumentForm, int transLineindex, int taxLotIndex) {
        if (isSource) {
            etlDocumentForm.getEndowmentTransactionLinesDocumentBase().getSourceTransactionLines().get(transLineindex).getTaxLotLines().remove(taxLotIndex);
        }
        else {
            etlDocumentForm.getEndowmentTransactionLinesDocumentBase().getTargetTransactionLines().get(transLineindex).getTaxLotLines().remove(taxLotIndex);
        }
    }

    /**
     * Gets the index of the tax lot line to be deleted.
     * 
     * @param request
     * @return the index of the tax lot line to be deleted
     */
    protected int getTaxLotToDelete(HttpServletRequest request) {
        int selectedTaxLot = -1;
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lotNumber = StringUtils.substringBetween(parameterName, ".taxLot", ".");
            selectedTaxLot = Integer.parseInt(lotNumber);
        }

        return selectedTaxLot;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        // on AssetDecreaseDocument and SecurityTransferDocument we can delete tax lots and that would be overridden by this
        // updateTaxLots call
        if (getRefreshTaxLotsOnSaveOrSubmit()) {
            updateTaxLots(endowmentDocument);
        }

        return super.save(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        // on AssetDecreaseDocument and SecurityTransferDocument we can delete tax lots and that would be overridden by this
        // updateTaxLots call
        if (getRefreshTaxLotsOnSaveOrSubmit()) {
            updateTaxLots(endowmentDocument);
        }

        return super.route(mapping, form, request, response);
    }

    /**
     * Tells whether the tax lot lines related to the transaction lines should be refreshed on save or submit. For documents that
     * support tax lots deletion this method should return false. For documents that do not support tax lots deletion this method
     * can return true so that the tax lots are updated on save or submit.
     * 
     * @return true or false depending on the document
     */
    abstract protected boolean getRefreshTaxLotsOnSaveOrSubmit();
}
