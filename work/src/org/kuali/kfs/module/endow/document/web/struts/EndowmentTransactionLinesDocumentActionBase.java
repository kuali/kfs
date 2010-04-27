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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.RegistrationCodeService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.DeleteTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;

public abstract class EndowmentTransactionLinesDocumentActionBase extends FinancialSystemTransactionalDocumentActionBase {

    private static final String SECURITY_SOURCE_REFRESH = "document.sourceTransactionSecurity.securityID";
    private static final String SECURITY_TARGET_REFRESH = "document.targetTransactionSecurity.securityID";
    private static final String REGISTRATION_REFRESH = "document.sourceTransactionSecurity.registrationCode";


    /**
     * This action executes an insert of an EndowmentSourceTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentSourceTransactionLine transLine = (EndowmentSourceTransactionLine) documentForm.getNewSourceTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine
            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertTransactionLine(true, documentForm, transLine);

            // clear the used newTargetLine
            documentForm.setNewSourceTransactionLine(new EndowmentSourceTransactionLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action executes an insert of an EndowmentTargetTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentTargetTransactionLine transLine = (EndowmentTargetTransactionLine) documentForm.getNewTargetTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine

            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertTransactionLine(false, documentForm, transLine);

            // clear the used newTargetLine
            documentForm.setNewTargetTransactionLine(new EndowmentTargetTransactionLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds the given transactionLine to the appropriate form-related datastructures.
     * 
     * @param isSource
     * @param etlDocumentForm
     * @param line
     */
    protected void insertTransactionLine(boolean isSource, EndowmentTransactionLinesDocumentFormBase etlDocumentForm, EndowmentTransactionLine line) {
        EndowmentTransactionLinesDocumentBase etlDoc = etlDocumentForm.getEndowmentTransactionLinesDocumentBase();
        if (isSource) {
            // add it to the document
            etlDoc.addSourceTransactionLine((EndowmentSourceTransactionLine) line);

            // Update the doc total
            // TODO check why total updated only on source
            if (etlDoc instanceof AmountTotaling)
                ((FinancialSystemDocumentHeader) etlDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) etlDoc).getTotalDollarAmount());
        }
        else {
            // add it to the document
            etlDoc.addTargetTransactionLine((EndowmentTargetTransactionLine) line);
        }
    }

    /**
     * This action deletes an EndowmentSourceTransactionLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument etlDoc = etlForm.getEndowmentTransactionLinesDocumentBase();

        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteTransactionLineEvent(errorPath, etlDoc, etlDoc.getSourceTransactionLine(deleteIndex)));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteTransactionLine(false, etlForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, EndowKeyConstants.TransactionalDocuments.ERROR_DELETING_TRANSACTION_LINE, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action deletes an EndowmentTargetTransactionLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument etlDoc = etlForm.getEndowmentTransactionLinesDocumentBase();

        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteTransactionLineEvent(errorPath, etlDoc, etlDoc.getTargetTransactionLine(deleteIndex)));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteTransactionLine(false, etlForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "target", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, EndowKeyConstants.TransactionalDocuments.ERROR_DELETING_TRANSACTION_LINE, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a Transaction Line.
     * 
     * @param isSource
     * @param etlDocumentForm
     * @param index
     */
    private void deleteTransactionLine(boolean isSource, EndowmentTransactionLinesDocumentFormBase etlDocumentForm, int index) {
        if (isSource) {
            // remove from document
            etlDocumentForm.getEndowmentTransactionLinesDocumentBase().getSourceTransactionLines().remove(index);

        }
        else {
            // remove from document
            etlDocumentForm.getEndowmentTransactionLinesDocumentBase().getTargetTransactionLines().remove(index);
        }
        // update the doc total
        EndowmentTransactionLinesDocument tdoc = etlDocumentForm.getEndowmentTransactionLinesDocumentBase();
        if (tdoc instanceof AmountTotaling) {
            ((FinancialSystemDocumentHeader) etlDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }
    }


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocumentBase etlDoc = ((EndowmentTransactionLinesDocumentFormBase) form).getEndowmentTransactionLinesDocumentBase();

        // To Determine if the refresh is coming from Security lookup
        if (request.getParameterMap().containsKey(SECURITY_SOURCE_REFRESH) || request.getParameterMap().containsKey(SECURITY_TARGET_REFRESH)) {
            refreshSecurityDetails(mapping, form, request, response);
        }

        // To Determine if the refresh is coming from Registration lookup
        if (request.getParameterMap().containsKey(REGISTRATION_REFRESH)) {
            refreshRegistrationDetails(mapping, form, request, response);
        }

        // To determine if the refresh is coming from KEMID lookup
        if (request.getParameterMap().containsKey("newSourceTransactionLine.kemid")) {
            refreshKemid(etlForm, true);
        }
        if (request.getParameterMap().containsKey("newTargetTransactionLine.kemid")) {
            refreshKemid(etlForm, false);
        }

        // To determine if the refresh is coming from Etran Code lookup
        if (request.getParameterMap().containsKey("newSourceTransactionLine.etranCode")) {
            refreshEtranCode(etlForm, true);
        }
        if (request.getParameterMap().containsKey("newTargetTransactionLine.etranCode")) {
            refreshEtranCode(etlForm, false);
        }


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves and sets the reference kemid object on newSourceTransactionLine or newTargetTransactionLine based on the kemid
     * looked up value.
     * 
     * @param etlForm
     * @param isSource
     */
    private void refreshKemid(EndowmentTransactionLinesDocumentFormBase etlForm, boolean isSource) {
        KEMID kemid = null;

        if (isSource) {
            kemid = SpringContext.getBean(KEMIDService.class).getByPrimaryKey(etlForm.getNewSourceTransactionLine().getKemid());
            etlForm.getNewSourceTransactionLine().setKemidObj(kemid);
        }
        else {
            kemid = SpringContext.getBean(KEMIDService.class).getByPrimaryKey(etlForm.getNewTargetTransactionLine().getKemid());
            etlForm.getNewTargetTransactionLine().setKemidObj(kemid);
        }

    }

    /**
     * Retrieves and sets the reference endowment transaction code object on newSourceTransactionLine or newTargetTransactionLine
     * based on the etranCode looked up value.
     * 
     * @param etlForm
     * @param isSource
     */
    private void refreshEtranCode(EndowmentTransactionLinesDocumentFormBase etlForm, boolean isSource) {
        EndowmentTransactionCode etranCode = null;

        if (isSource) {
            etranCode = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(etlForm.getNewSourceTransactionLine().getEtranCode());
            etlForm.getNewSourceTransactionLine().setEtranCodeObj(etranCode);
        }
        else {
            etranCode = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(etlForm.getNewTargetTransactionLine().getEtranCode());
            etlForm.getNewTargetTransactionLine().setEtranCodeObj(etranCode);
        }

    }

    public ActionForward refreshSecurityDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = (EndowmentSecurityDetailsDocumentBase) ((EndowmentTransactionLinesDocumentFormBase) form).getDocument();

        Security security;
        if (request.getParameterMap().containsKey(SECURITY_SOURCE_REFRESH))
            security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().getSecurityID());
        else
            security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getTargetTransactionSecurity().getSecurityID());

        ClassCode classCode = SpringContext.getBean(ClassCodeService.class).getByPrimaryKey(security.getSecurityClassCode());
        security.setClassCode(classCode);
        EndowmentTransactionCode endowmentTransactionCode = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(classCode.getSecurityEndowmentTransactionCode());
        classCode.setEndowmentTransactionCode(endowmentTransactionCode);

        if (request.getParameterMap().containsKey(SECURITY_SOURCE_REFRESH))
            endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().setSecurity(security);
        else
            endowmentSecurityDetailsDocumentBase.getTargetTransactionSecurity().setSecurity(security);

        return null;
    }

    public ActionForward refreshRegistrationDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = (EndowmentSecurityDetailsDocumentBase) ((EndowmentTransactionLinesDocumentFormBase) form).getDocument();

        RegistrationCode registrationCode = SpringContext.getBean(RegistrationCodeService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().getRegistrationCode());
        endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);

        return null;

    }
}
