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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalance;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocumentBase;
import org.kuali.kfs.module.endow.document.validation.event.AddEndowmentAccountingLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.module.endow.exception.EndowmentAccountingLineException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class EndowmentAccountingLinesDocumentActionBase extends EndowmentTransactionLinesDocumentActionBase {
    protected static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EndowmentAccountingLinesDocumentActionBase.class);

    /**
     * This action executes an insert of an TargetEndowmentAccountingLine into a document only after validating the Accounting line
     * and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertTargetAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentAccountingLinesDocumentFormBase documentForm = (EndowmentAccountingLinesDocumentFormBase) form;
        EndowmentAccountingLinesDocument endowmentDocument = (EndowmentAccountingLinesDocument) documentForm.getDocument();

        TargetEndowmentAccountingLine accLine = (TargetEndowmentAccountingLine) documentForm.getNewTargetAccountingLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddEndowmentAccountingLineEvent(EndowConstants.NEW_TARGET_ACC_LINE_PROPERTY_NAME, endowmentDocument, accLine));

        if (rulePassed) {
            // add accountingLine

            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertAccountingLine(false, documentForm, accLine);

            // clear the used newTargetLine
            documentForm.setNewTargetAccountingLine(new TargetEndowmentAccountingLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action executes an insert of an SourceEndowmentAccountingLine into a document only after validating the Accounting line
     * and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertSourceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentAccountingLinesDocumentFormBase documentForm = (EndowmentAccountingLinesDocumentFormBase) form;
        EndowmentAccountingLinesDocument endowmentDocument = (EndowmentAccountingLinesDocument) documentForm.getDocument();

        SourceEndowmentAccountingLine accLine = (SourceEndowmentAccountingLine) documentForm.getNewSourceAccountingLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddEndowmentAccountingLineEvent(EndowConstants.NEW_SOURCE_ACC_LINE_PROPERTY_NAME, endowmentDocument, accLine));

        if (rulePassed) {
            // add accountingLine
            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertAccountingLine(true, documentForm, accLine);

            // clear the used newTargetLine
            documentForm.setNewSourceAccountingLine(new SourceEndowmentAccountingLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds the given accountingLine to the appropriate form-related data structures.
     * 
     * @param isSource
     * @param etlDocumentForm
     * @param line
     */
    protected void insertAccountingLine(boolean isSource, EndowmentAccountingLinesDocumentFormBase etaDocumentForm, EndowmentAccountingLine line) {
        EndowmentAccountingLinesDocumentBase etaDoc = etaDocumentForm.getEndowmentAccountingLinesDocumentBase();

        if (isSource) {
            // add it to the document
            etaDoc.addSourceAccountingLine((SourceEndowmentAccountingLine) line);
        }
        else {
            // add it to the document
            etaDoc.addTargetAccountingLine((TargetEndowmentAccountingLine) line);
        }

        // Update the doc total
        if (etaDoc instanceof AmountTotaling)
            ((FinancialSystemDocumentHeader) etaDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) etaDoc).getTotalDollarAmount());
    }

    /**
     * This action deletes an EndowmentSourceAccountingLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteSourceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentAccountingLinesDocumentFormBase etlForm = (EndowmentAccountingLinesDocumentFormBase) form;
        EndowmentAccountingLinesDocument etlDoc = etlForm.getEndowmentAccountingLinesDocumentBase();

        int deleteIndex = getLineToDelete(request);
        String errorPath = EndowPropertyConstants.EXISTING_SOURCE_ACCT_LINE_PREFIX + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, etlDoc, etlDoc.getSourceAccountingLine(deleteIndex)));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(true, etlForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_DELETING_ACCOUNTING_LINE, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action deletes an EndowmentTargetAccountingLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTargetAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentAccountingLinesDocumentFormBase etlForm = (EndowmentAccountingLinesDocumentFormBase) form;
        EndowmentAccountingLinesDocument etlDoc = etlForm.getEndowmentAccountingLinesDocumentBase();

        int deleteIndex = getLineToDelete(request);
        String errorPath = EndowPropertyConstants.EXISTING_TARGET_ACCT_LINE_PREFIX + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, etlDoc, etlDoc.getTargetAccountingLine(deleteIndex)));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(false, etlForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "target", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_DELETING_ACCOUNTING_LINE, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a Accounting Line.
     * 
     * @param isSource
     * @param etlDocumentForm
     * @param index
     */
    protected void deleteAccountingLine(boolean isSource, EndowmentAccountingLinesDocumentFormBase etlDocumentForm, int index) {
        if (isSource) {
            // remove from document
            etlDocumentForm.getEndowmentAccountingLinesDocumentBase().getSourceAccountingLines().remove(index);

        }
        else {
            // remove from document
            etlDocumentForm.getEndowmentAccountingLinesDocumentBase().getTargetAccountingLines().remove(index);
        }
        // update the doc total
        EndowmentAccountingLinesDocument tdoc = etlDocumentForm.getEndowmentAccountingLinesDocumentBase();
        if (tdoc instanceof AmountTotaling) {
            ((FinancialSystemDocumentHeader) etlDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }
    }

    /**
     * This method returns the balance inquiry for target accounting lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForTargetAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return performBalanceInquiry(false, mapping, form, request, response);
    }

    /**
     * This method returns the balance inquiry for source accounting lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForSourceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        EndowmentAccountingLinesDocumentFormBase etlForm = (EndowmentAccountingLinesDocumentFormBase) form;
        EndowmentAccountingLinesDocumentBase etlDoc = ((EndowmentAccountingLinesDocumentFormBase) form).getEndowmentAccountingLinesDocumentBase();


        // when we return from the lookup, our next request's method to call is going to be refresh
        etlForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        EndowmentAccountingLine etLine;
        if (isSource) {
            etLine = etlDoc.getSourceAccountingLines().get(this.getSelectedLine(request));
        }
        else {
            etLine = etlDoc.getTargetAccountingLines().get(this.getSelectedLine(request));
        }

        // build out base path for return location, use config service
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        // this hack sets the return anchor we want to return too after the inquiry
        // do this here so it gets into the session stored form version
        // refresh checks for this after and resets the anchor
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            etlForm.setBalanceInquiryReturnAnchor(((KualiForm) form).getAnchor());
        }

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObjectWithGeneratedKey(form);

        // now add required parameters
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boName);
        params.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        params.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        // params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");


        if (StringUtils.isNotBlank(etLine.getChartOfAccountsCode())) {
            params.put("chartOfAccountsCode", etLine.getChartOfAccountsCode());
        }
        if (StringUtils.isNotBlank(etLine.getAccountNumber())) {
            params.put("accountNumber", etLine.getAccountNumber());
        }
        if (StringUtils.isNotBlank(etLine.getFinancialObjectCode())) {
            params.put("financialObjectCode", etLine.getFinancialObjectCode());
        }
        if (StringUtils.isNotBlank(etLine.getSubAccountNumber())) {
            params.put("subAccountNumber", etLine.getSubAccountNumber());
        }
        if (StringUtils.isNotBlank(etLine.getFinancialSubObjectCode())) {
            params.put("financialSubObjectCode", etLine.getFinancialSubObjectCode());
        }
        if (StringUtils.isNotBlank(etLine.getProjectCode())) {
            params.put("projectCode", etLine.getProjectCode());
        }


        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.BALANCE_INQUIRY_REPORT_MENU_ACTION, params);

        // register that we're going to come back w/ to this form w/ a refresh methodToCall
        etlForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * This action executes a call to upload CSV accounting line values as TargetAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward uploadTargetLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // call method that sourceform and destination list
        uploadAccountingLines(false, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes a call to upload CSV accounting line values as SourceAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ActionForward uploadSourceLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {

        // call method that sourceform and destination list
        uploadAccountingLines(true, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method determines whether we are uploading source or target lines, and then calls uploadAccountingLines directly on the
     * document object. This method handles retrieving the actual upload file as an input stream into the document.
     * 
     * @param isSource
     * @param form
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        EndowmentAccountingLinesDocumentFormBase tmpForm = (EndowmentAccountingLinesDocumentFormBase) form;

        List importedLines = null;

        EndowmentAccountingLinesDocument financialDocument = tmpForm.getEndowmentAccountingLinesDocumentBase();
        EndowmentAccountingLineParser accountingLineParser = financialDocument.getEndowmentAccountingLineParser();

        // import the lines
        String errorPathPrefix = null;
        try {
            if (isSource) {
                errorPathPrefix = EndowPropertyConstants.EXISTING_SOURCE_ACCT_LINE_PREFIX;
                FormFile sourceFile = tmpForm.getSourceFile();
                checkUploadFile(sourceFile);
                importedLines = accountingLineParser.importSourceEndowmentAccountingLines(sourceFile.getFileName(), sourceFile.getInputStream(), financialDocument);
            }
            else {
                errorPathPrefix = EndowPropertyConstants.EXISTING_TARGET_ACCT_LINE_PREFIX;
                FormFile targetFile = tmpForm.getTargetFile();
                checkUploadFile(targetFile);
                importedLines = accountingLineParser.importTargetEndowmentAccountingLines(targetFile.getFileName(), targetFile.getInputStream(), financialDocument);
            }
        }
        catch (EndowmentAccountingLineException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add line to list for those lines which were successfully imported
        if (importedLines != null) {
            for (Iterator i = importedLines.iterator(); i.hasNext();) {
                EndowmentAccountingLine importedLine = (EndowmentAccountingLine) i.next();
                insertAccountingLine(isSource, tmpForm, importedLine);
            }
        }
    }

    /**
     * This method...
     * 
     * @param file
     */
    protected void checkUploadFile(FormFile file) {
        if (file == null) {
            throw new EndowmentAccountingLineException("invalid (null) upload file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
    }


}
