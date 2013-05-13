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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.LiabilityDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.LiabilityDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;


public class LiabilityDecreaseDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        // LiabilityIncreaseDocument liabilityIncreaseDocument = ((LiabilityIncreaseDocumentForm)
        // form).getLiabilityIncreaseDocument();

        /*
         * //Target //Saving a Trans Line EndowmentTransactionLine tranLine = new EndowmentTargetTransactionLine();
         * tranLine.setTransactionLineNumber(new KualiInteger("1")); tranLine.setKemid("099PLTF013");
         * tranLine.setEtranCode("00100"); tranLine.setTransactionIPIndicatorCode("I"); tranLine.setTransactionAmount(new
         * KualiDecimal(2.1)); //Setting the transaction line. List tranList = new ArrayList(); tranList.add(tranLine);
         * liabilityIncreaseDocument.setTargetTransactionLines(tranList); tranLine = new EndowmentTargetTransactionLine();
         * tranLine.setDocumentNumber("4160"); tranLine.setTransactionLineNumber(new KualiInteger("2"));
         * tranLine.setKemid("099PLTF013"); tranLine.setEtranCode("00100"); tranLine.setTransactionIPIndicatorCode("I");
         * tranLine.setTransactionAmount(new KualiDecimal(2.2)); tranList.add(tranLine); //Source tranLine = new
         * EndowmentSourceTransactionLine(); tranLine.setDocumentNumber("4160"); tranLine.setTransactionLineNumber(new
         * KualiInteger("3")); tranLine.setKemid("099PLTF013"); tranLine.setEtranCode("00100");
         * tranLine.setTransactionIPIndicatorCode("I"); tranLine.setTransactionAmount(new KualiDecimal(2.3)); //Setting the
         * transaction line. List stranList = new ArrayList(); stranList.add(tranLine);
         * liabilityIncreaseDocument.setSourceTransactionLines(stranList); EndowmentTransactionTaxLotLine hldg = new
         * EndowmentTransactionTaxLotLine(); hldg.setDocumentNumber("4160"); hldg.setDocumentLineNumber(new KualiInteger("1"));
         * hldg.setDocumentLineTypeCode("F"); hldg.setTransactionHoldingLongTermNumber(new KualiInteger("99")); hldg.setLotUnits(new
         * KualiDecimal("22")); List taxList = new ArrayList(); taxList.add(hldg); EndowmentTransactionTaxLotLine hldg = new
         * EndowmentTransactionTaxLotLine(); hldg.setDocumentLineNumber(new KualiInteger("2")); hldg.setDocumentLineTypeCode("F");
         * hldg.setTransactionHoldingLongTermNumber(new KualiInteger("99")); hldg.setLotUnits(new KualiDecimal("22")); List taxList
         * = new ArrayList(); taxList.add(hldg); tranLine.setTaxLotLines(taxList);
         */

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#updateTransactionLineTaxLots(boolean, boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {

        LiabilityDocumentService taxLotsService = SpringContext.getBean(LiabilityDocumentService.class);
        LiabilityDecreaseDocument liabilityDecreaseDocument = (LiabilityDecreaseDocument) etlDocument;
        taxLotsService.updateLiabilityDecreaseTransactionLineTaxLots(isSource, liabilityDecreaseDocument, transLine);

    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#getRefreshTaxLotsOnSaveOrSubmit()
     */
    @Override
    protected boolean getRefreshTaxLotsOnSaveOrSubmit() {
        return true;
    }

}
