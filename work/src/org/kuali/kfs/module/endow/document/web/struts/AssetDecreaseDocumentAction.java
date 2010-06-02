/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService;
import org.kuali.kfs.module.endow.document.validation.event.DeleteTaxLotLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.KNSConstants;

public class AssetDecreaseDocumentAction extends EndowmentTransactionLinesDocumentActionBase {

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {
        UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) etlDocument;
        taxLotsService.updateTransactionLineTaxLots(false, assetDecreaseDocument, transLine);

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
            UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
            AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) etlDoc;
            taxLotsService.updateTransactionLineTaxLots(true, assetDecreaseDocument, etlForm.getEndowmentTransactionLinesDocumentBase().getSourceTransactionLines().get(transLineindex));
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
            UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
            AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) etlDoc;
            taxLotsService.updateTransactionLineTaxLots(true, assetDecreaseDocument, etlForm.getEndowmentTransactionLinesDocumentBase().getTargetTransactionLines().get(transLineindex));
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
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lotNumber = StringUtils.substringBetween(parameterName, ".taxLot", ".");
            selectedTaxLot = Integer.parseInt(lotNumber);
        }

        return selectedTaxLot;
    }

}
