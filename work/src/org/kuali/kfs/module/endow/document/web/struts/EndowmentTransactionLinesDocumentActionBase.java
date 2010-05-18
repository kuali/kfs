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

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalance;
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
import org.kuali.kfs.module.endow.document.validation.event.RefreshTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public abstract class EndowmentTransactionLinesDocumentActionBase extends FinancialSystemTransactionalDocumentActionBase {

    private static final String SECURITY_SOURCE_REFRESH = "document.sourceTransactionSecurity.securityID";
    private static final String SECURITY_TARGET_REFRESH = "document.targetTransactionSecurity.securityID";
    private static final String REGISTRATION_SOURCE_REFRESH = "document.sourceTransactionSecurity.registrationCode";
    private static final String REGISTRATION_TARGET_REFRESH = "document.targetTransactionSecurity.registrationCode";


    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward importTransactionLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

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
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

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
        }
        else {
            // add it to the document
            etlDoc.addTargetTransactionLine((EndowmentTargetTransactionLine) line);
        }

        updateTransactionLineTaxLots(isSource, etlDoc, line);

        // Update the doc total
        if (etlDoc instanceof AmountTotaling)
            ((FinancialSystemDocumentHeader) etlDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) etlDoc).getTotalDollarAmount());
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
            deleteTransactionLine(true, etlForm, deleteIndex);
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
        if (request.getParameterMap().containsKey(REGISTRATION_SOURCE_REFRESH) || request.getParameterMap().containsKey(REGISTRATION_TARGET_REFRESH)) {
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


        // balance inquiry anchor is set before doing a balance inquiry
        if (etlForm.getBalanceInquiryReturnAnchor() != null) {
            etlForm.setAnchor(etlForm.getBalanceInquiryReturnAnchor());
            etlForm.setBalanceInquiryReturnAnchor(null);
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

    /**
     * Retrieves and sets the reference Security object on Source or Target transactionsecurity based on the looked up value.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
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

    /**
     * Retrieves and sets the reference RegistrationCode object on Source or Target transactionsecurity based on the looked up
     * value.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshRegistrationDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = (EndowmentSecurityDetailsDocumentBase) ((EndowmentTransactionLinesDocumentFormBase) form).getDocument();

        RegistrationCode registrationCode = null;
        if (request.getParameterMap().containsKey(REGISTRATION_SOURCE_REFRESH))
            registrationCode = SpringContext.getBean(RegistrationCodeService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().getRegistrationCode());
        else
            registrationCode = SpringContext.getBean(RegistrationCodeService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getTargetTransactionSecurity().getRegistrationCode());

        if (request.getParameterMap().containsKey(REGISTRATION_SOURCE_REFRESH))
            endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);
        else
            endowmentSecurityDetailsDocumentBase.getTargetTransactionSecurity().setRegistrationCodeObj(registrationCode);

        return null;
    }


    /**
     * This method returns the balance inquiry for target transaction lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performBalanceInquiry(false, mapping, form, request, response);
    }

    /**
     * This method returns the balance inquiry for source transaction lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performBalanceInquiry(true, mapping, form, request, response);
    }

    /**
     * This method provides the KEMIDCurrentBalance as the default lookup object. If a different lookup is needed this method should
     * be overriden.
     * 
     * @param isSource
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiry(boolean isSource, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String boName = KEMIDCurrentBalance.class.getName();
        return performBalanceInquiry(isSource, boName, mapping, form, request, response);
    }

    /**
     * This method is similar to org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase.performBalanceInquiry()
     * 
     * @param isRevenue
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiry(boolean isSource, String boName, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;

        // get the selected line, setup parms and redirect to balance inquiry
        EndowmentTransactionLinesDocumentFormBase etlForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocumentBase etlDoc = ((EndowmentTransactionLinesDocumentFormBase) form).getEndowmentTransactionLinesDocumentBase();


        // when we return from the lookup, our next request's method to call is going to be refresh
        etlForm.registerEditableProperty(KNSConstants.DISPATCH_REQUEST_PARAMETER);
        etlForm.registerNextMethodToCallIsRefresh(true);

        EndowmentTransactionLine etLine;
        if (isSource) {
            etLine = etlDoc.getSourceTransactionLines().get(this.getSelectedLine(request));
        }
        else {
            etLine = etlDoc.getTargetTransactionLines().get(this.getSelectedLine(request));
        }

        // build out base path for return location, use config service
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        // this hack sets the return anchor we want to return too after the inquiry
        // do this here so it gets into the session stored form version
        // refresh checks for this after and resets the anchor
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            etlForm.setBalanceInquiryReturnAnchor(((KualiForm) form).getAnchor());
        }

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX);

        // now add required parameters
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boName);
        params.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + ".do");

        if (StringUtils.isNotBlank(etLine.getKemid())) {
            params.put("kemid", etLine.getKemid());
        }

        // anchor, if it exists
        // this doesn't seem to work with the balance inquiry infrastructure, so added hack above
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            params.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }


        String lookupUrl = UrlFactory.parameterizeUrl(KFSConstants.LOOKUP_ACTION, params);

        this.setupDocumentExit();
        return new ActionForward(lookupUrl, true);
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
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine, selectedLine));

        if (rulePassed) {
            updateTransactionLineTaxLots(false, endowmentDocument, transLine);
        }
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
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine, selectedLine));

        if (rulePassed) {
            updateTransactionLineTaxLots(true, endowmentDocument, transLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Updates the tax lots for the given transaction line.
     * 
     * @param isSource
     * @param etlDocument
     * @param transLine
     */
    protected abstract void updateTransactionLineTaxLots(boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine);

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
                rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME, etlDocument, endowmentTransactionLine, i));

                if (rulePassed) {
                    updateTransactionLineTaxLots(true, etlDocument, endowmentTransactionLine);
                }

            }
        }

        if (etlDocument.getTargetTransactionLines() != null) {
            for (int i = 0; i < etlDocument.getTargetTransactionLines().size(); i++) {
                EndowmentTransactionLine endowmentTransactionLine = (EndowmentTransactionLine) etlDocument.getTargetTransactionLines().get(i);
                boolean rulePassed = true;
                // check any business rules
                rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RefreshTransactionLineEvent(EndowConstants.EXISTING_TARGET_TRAN_LINE_PROPERTY_NAME, etlDocument, endowmentTransactionLine, i));

                if (rulePassed) {
                    updateTransactionLineTaxLots(false, etlDocument, endowmentTransactionLine);
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();
        updateTaxLots(endowmentDocument);

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
        updateTaxLots(endowmentDocument);

        return super.route(mapping, form, request, response);
    }

}
