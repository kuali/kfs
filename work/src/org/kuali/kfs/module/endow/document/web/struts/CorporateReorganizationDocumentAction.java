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

import static org.kuali.kfs.module.endow.EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.CorporateReorganizationDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateSecurityTransferTargetTaxLotsService;
import org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.RefreshTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CorporateReorganizationDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#updateTransactionLineTaxLots(boolean, boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {
        CorporateReorganizationDocument corporateReorganizationDocument = (CorporateReorganizationDocument) etlDocument;

        if (transLine instanceof EndowmentSourceTransactionLine) {
            UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
            taxLotsService.updateTransactionLineTaxLots(isUpdate, corporateReorganizationDocument, transLine);
        }

        // TODO:  WHAT TO DO ABOUT THIS?
        if (transLine instanceof EndowmentTargetTransactionLine) {
            UpdateSecurityTransferTargetTaxLotsService taxLotsService = SpringContext.getBean(UpdateSecurityTransferTargetTaxLotsService.class);
            taxLotsService.updateTransactionLineTaxLots(corporateReorganizationDocument, transLine);
        }

    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#deleteSourceTransactionLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward actionForward = super.deleteSourceTransactionLine(mapping, form, request, response);
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;

        // clear the current TargetLine
        ((EndowmentTransactionLinesDocumentFormBase) form).setNewTargetTransactionLine(new EndowmentTargetTransactionLine());
        
        // delete target transaction lines as well
        List<EndowmentTransactionLine> targetTransactionLines = etlForm.getEndowmentTransactionLinesDocumentBase().getTargetTransactionLines();
        if (targetTransactionLines != null && targetTransactionLines.size() > 0) {
            for (int i = 0; i < targetTransactionLines.size(); i++) {
                deleteTransactionLine(false, etlForm, i);
            }
        }

        return actionForward;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#deleteSourceTaxLotLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceTaxLotLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward actionForward = super.deleteSourceTaxLotLine(mapping, form, request, response);
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        boolean rulePassed = true;

        // if the document has target transaction lines update the related tax lots
        if (endowmentDocument.getTargetTransactionLines() != null && endowmentDocument.getTargetTransactionLines().size() > 0) {
            for (int i = 0; i < endowmentDocument.getTargetTransactionLines().size(); i++) {

                EndowmentTransactionLine transLine = endowmentDocument.getTargetTransactionLines().get(i);

                // check any business rules
                rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine, i));

                if (rulePassed) {
                    updateTransactionLineTaxLots(true, true, endowmentDocument, transLine);
                }

                if (endowmentDocument instanceof AmountTotaling)
                    ((FinancialSystemDocumentHeader) documentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) endowmentDocument).getTotalDollarAmount());
            }

        }

        return actionForward;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#insertSourceTransactionLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentSourceTransactionLine sourceTransLine = (EndowmentSourceTransactionLine) documentForm.getNewSourceTransactionLine();
        EndowmentTargetTransactionLine targetTransLine = (EndowmentTargetTransactionLine) documentForm.getNewTargetTransactionLine();
        
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, sourceTransLine));

        if (rulePassed) {
            
            EndowmentSecurityDetailsDocumentBase securityDocument = (EndowmentSecurityDetailsDocumentBase)endowmentDocument;
            EndowmentTransactionSecurity sourceTranSecurity = securityDocument.getSourceTransactionSecurity();
            
            // Get the etran from the Security BO class code.
            Security sourceSecurity = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(sourceTranSecurity.getSecurityID());
            String sourceEtran = ObjectUtils.isNotNull(sourceSecurity) ? sourceSecurity.getClassCode().getSecurityEndowmentTransactionCode() : "";
            
            // Add the transaction line.
            sourceTransLine.setEtranCode(sourceEtran);
            insertTransactionLine(true,  documentForm, sourceTransLine);
            
            fillInTargetTransactionLine(targetTransLine, sourceTransLine);
            documentForm.setNewTargetTransactionLine(targetTransLine);
            
            // Clear the used newTargetLine
            documentForm.setNewSourceTransactionLine(new EndowmentSourceTransactionLine());
            
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#insertTargetTransactionLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentTargetTransactionLine transLine = (EndowmentTargetTransactionLine) documentForm.getNewTargetTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {

            EndowmentSecurityDetailsDocumentBase securityDocument = (EndowmentSecurityDetailsDocumentBase)endowmentDocument;
            EndowmentTransactionSecurity targetTranSecurity = securityDocument.getTargetTransactionSecurity();
            
            // Get the etran from the Security BO class code.
            Security targetSecurity = (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(targetTranSecurity.getSecurityID());
            String targetEtran = ObjectUtils.isNotNull(targetSecurity) ? targetSecurity.getClassCode().getSecurityEndowmentTransactionCode() : "";
            
            // Set the etran code.
            transLine.setEtranCode(targetEtran);
            
            // Add the transaction line.
            insertTransactionLine(false, documentForm, transLine);

            // Clear the used newTargetLine
            documentForm.setNewTargetTransactionLine(new EndowmentTargetTransactionLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#refreshSourceTaxLots(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refreshSourceTaxLots(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();
        int selectedLine = this.getSelectedLine(request);
        
        EndowmentTransactionLine sourceTransLine = endowmentDocument.getSourceTransactionLines().get(selectedLine);
//        EndowmentTransactionLine targetTransLine = endowmentDocument.getTargetTransactionLines().get(selectedLine);

        boolean sourceRulePassed = true;
        boolean targetRulePassed = true;
        
        // Check any business rules
        sourceRulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, sourceTransLine, selectedLine));
//        sourceRulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, targetTransLine, selectedLine));
        if (sourceRulePassed && targetRulePassed) {
            updateTransactionLineTaxLots(false, true,  endowmentDocument, sourceTransLine);
            
            List<EndowmentTransactionLine> targetTransactionLines = documentForm.getEndowmentTransactionLinesDocumentBase().getTargetTransactionLines();
            
            // If there are no target transaction lines, then we know that nothing
            // has been added, therefore just update the new line portion; otherwise,
            // update the line in the list.
            EndowmentTransactionLine targetTransLine = null;
            if (!targetTransactionLines.isEmpty()) {
                targetTransLine = endowmentDocument.getTargetTransactionLines().get(selectedLine);
                fillInTargetTransactionLine((EndowmentTargetTransactionLine)targetTransLine, (EndowmentSourceTransactionLine)sourceTransLine);
                updateTransactionLineTaxLots(false, false, endowmentDocument, targetTransLine);
            }
            else {
                targetTransLine = documentForm.getNewTargetTransactionLine();
                fillInTargetTransactionLine((EndowmentTargetTransactionLine)targetTransLine, (EndowmentSourceTransactionLine)sourceTransLine);
                documentForm.setNewTargetTransactionLine(targetTransLine);
            }
        }

        if (endowmentDocument instanceof AmountTotaling)
            ((FinancialSystemDocumentHeader) documentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) endowmentDocument).getTotalDollarAmount());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    
    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#refreshTargetTaxLots(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refreshTargetTaxLots(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.refreshTargetTaxLots(mapping, form, request, response);
        
        return actionForward;
      }


    private void fillInTargetTransactionLine(EndowmentTargetTransactionLine targetTransLine, EndowmentSourceTransactionLine sourceTransLine) {
        // Copy relevant source fields over to the target fields.
        targetTransLine.setTransactionIPIndicatorCode(sourceTransLine.getTransactionIPIndicatorCode());       
        targetTransLine.setTransactionAmount(sourceTransLine.getTransactionAmount());
        targetTransLine.setKemid(sourceTransLine.getKemid());
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#getRefreshTaxLotsOnSaveOrSubmit()
     */
    @Override
    protected boolean getRefreshTaxLotsOnSaveOrSubmit() {
        return false;
    }

}
