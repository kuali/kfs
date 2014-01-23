/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.businessobject.options.CapitalAccountingLinesComparator;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.validation.event.CapitalAccountingLinesSameObjectCodeSubTypeEvent;
import org.kuali.kfs.fp.document.validation.impl.CapitalAssetAccountingLineUniquenessEnforcementValidation;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This is the action class for the CapitalAccountingLinesActionBase.
 */
public abstract class CapitalAccountingLinesActionBase extends CapitalAssetInformationActionBase {
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);

    /**
     * Upon entry we need to set the capitalAccountingLinesExist boolean and check the tab states
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountingDocument tdoc = (AccountingDocument) kualiDocumentFormBase.getDocument();

        initializeCapitalAccountingLinesExist(tdoc);
        setTabStatesForCapitalAssets(form);

        return super.execute(mapping, form, request, response);
    }

    /**
     * Upon entry we need to set the capitalAccountingLinesExist boolean and check the tab states
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountingDocument tdoc = (AccountingDocument) kualiDocumentFormBase.getDocument();

        initializeCapitalAccountingLinesExist(tdoc);
        setTabStatesForCapitalAssets(form);

        return super.docHandler(mapping, form, request, response);
    }

    /**
     * removes capitalaccountinglines which is a transient bo.. and call the super method to
     * create a error correction document.
     * @see org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase#correct(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward correct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) form;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();
        caldb.getCapitalAccountingLines().clear();

        super.correct(mapping, capitalAccountingLinesFormBase, request, response);

        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) form;
        List<CapitalAssetInformation> currentCapitalAssetInformation =  this.getCurrentCapitalAssetInformationObject(kadfb);
        for (CapitalAssetInformation capitalAsset : currentCapitalAssetInformation) {
            capitalAsset.setCapitalAssetProcessedIndicator(false);
            capitalAsset.setCapitalAssetLineAmount(capitalAsset.getCapitalAssetLineAmount().negated());
            //remove capital asset tag/location asset tag number and serial number as
            //they will fail because these values will be duplicates.
            List<CapitalAssetInformationDetail> tagLocationDetails = capitalAsset.getCapitalAssetInformationDetails();
            for (CapitalAssetInformationDetail tagLocationDetail : tagLocationDetails) {
                tagLocationDetail.setCapitalAssetTagNumber(null);
                tagLocationDetail.setCapitalAssetSerialNumber(null);
            }

            List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
                groupAccountLine.setAmount(groupAccountLine.getAmount().negated());
            }
        }

        String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

        AccountingDocument tdoc = (AccountingDocument) capitalAccountingLinesFormBase.getDocument();

        List<CapitalAccountingLines> capitalAccountingLines = new ArrayList();
        //for every source/target accounting line that has an object code that requires a
        //capital asset, creates a capital accounting line that the user can select to
        //perform create or modify asset functions.
        createCapitalAccountingLines(capitalAccountingLines, tdoc, distributionAmountCode);
        sortCaptitalAccountingLines(capitalAccountingLines);

        caldb.setCapitalAccountingLines(capitalAccountingLines);
        //checks capital accounting lines for capital assets and if so checks the select box
        checkSelectForCapitalAccountingLines(capitalAccountingLinesFormBase);

        checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        calculatePercentsForSelectedCapitalAccountingLines(capitalAccountingLinesFormBase);

        //setup the initial next sequence number column..
        setupIntialNextCapitalAssetLineNumber(capitalAccountingLinesFormBase);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * All document-load operations get routed through here
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) kualiDocumentFormBase;
        AccountingDocument tdoc = (AccountingDocument) kualiDocumentFormBase.getDocument();

        initializeCapitalAccountingLinesExist(tdoc);
        setTabStatesForCapitalAssets(kualiDocumentFormBase);

        // Since the UI display depends on whether or not capitalAccountingLines is empty to display asset information, we
        // need to be careful of only generating them if there actually is asset information. Otherwise the user is forced to
        // select the generate button when they are ready
        if (checkCreateAssetsExist(capitalAccountingLinesFormBase) || checkModifyAssetsExist(capitalAccountingLinesFormBase)) {
            String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

            List<CapitalAccountingLines> capitalAccountingLines = new ArrayList();
            //for every source/target accounting line that has an object code that requires a
            //capital asset, creates a capital accounting line that the user can select to
            //perform create or modify asset functions.
            createCapitalAccountingLines(capitalAccountingLines, tdoc, distributionAmountCode);
            sortCaptitalAccountingLines(capitalAccountingLines);

            CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) tdoc;
            caldb.setCapitalAccountingLines(capitalAccountingLines);
            //checks capital accounting lines for capital assets and if so checks the select box
            checkSelectForCapitalAccountingLines(capitalAccountingLinesFormBase);

            checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
            calculatePercentsForSelectedCapitalAccountingLines(capitalAccountingLinesFormBase);

            //setup the initial next sequence number column..
            setupIntialNextCapitalAssetLineNumber(kualiDocumentFormBase);

            KualiForm kualiForm = kualiDocumentFormBase;
            //based on the records in capital accounting lines, capital asset information lists
            //set the tabs to open if lists not empty else set to close
            setTabStatesForCapitalAssets(kualiForm);
        }
    }

    /**
     * checks if the document has any capital accounting lines associated with it and sets
     * CapitalAccountingLinesDocumentBase.capitalAccountingLinesExist if necessary
     *
     * @param tdoc
     */
    protected void initializeCapitalAccountingLinesExist(AccountingDocument tdoc) {
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) tdoc;

        List<SourceAccountingLine> sourceAccountLines = tdoc.getSourceAccountingLines();
        for (SourceAccountingLine line : sourceAccountLines) {
            if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(line)) {
                caldb.setCapitalAccountingLinesExist(true);
                break;
            }
        }

        // If it isn't true already, check the target lines
        if (!caldb.isCapitalAccountingLinesExist()) {
            List<TargetAccountingLine> targetAccountLines = tdoc.getTargetAccountingLines();
            for (TargetAccountingLine line : targetAccountLines) {
                if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(line)) {
                    caldb.setCapitalAccountingLinesExist(true);
                    break;
                }
            }
        }
    }

    /**
     * When user adds an accounting line to the either source or target, if the object code on
     * that line has capital object type code group then a capital accounting line is created.
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertAccountingLine(boolean, org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase kualiDocumentFormBase, AccountingLine line) {
        AccountingDocument tdoc = (AccountingDocument) kualiDocumentFormBase.getDocument();
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) tdoc;

        if(capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(line) && caldb.getCapitalAccountingLines().size() > 0) {
            if (line.isSourceAccountingLine()) {
                GlobalVariables.getMessageMap().putError(KFSConstants.NEW_SOURCE_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CANT_ADD_CAPITALIZATION_ACCOUNTING_LINES);
            } else {
                GlobalVariables.getMessageMap().putError(KFSConstants.NEW_TARGET_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_CANT_ADD_CAPITALIZATION_ACCOUNTING_LINES);
            }
        } else {
            super.insertAccountingLine(isSource, kualiDocumentFormBase, line);

            initializeCapitalAccountingLinesExist(tdoc);

            setTabStatesForCapitalAssets(kualiDocumentFormBase);
        }
    }

    /**
     * creates the capital accounting lines looking at source and/or target accounting lines.
     *
     * @param capitalAccountingLines
     * @param tdoc
     * @param distributionAmountCode distribution amount code for the line
     */
    protected void createCapitalAccountingLines(List<CapitalAccountingLines> capitalAccountingLines, AccountingDocument tdoc, String distributionAmountCode) {
        List<SourceAccountingLine> sourceAccountLines = tdoc.getSourceAccountingLines();

        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) tdoc;

        for (SourceAccountingLine line : sourceAccountLines) {
            //create source capital accounting line
            createCapitalAccountingLine(capitalAccountingLines, line, distributionAmountCode);
        }

        List<TargetAccountingLine> targetAccountLines = tdoc.getTargetAccountingLines();

        for (TargetAccountingLine line : targetAccountLines) {
            // create target capital accounting line
            createCapitalAccountingLine(capitalAccountingLines, line, distributionAmountCode);
        }

        //sort the capital accounting lines collection
        sortCaptitalAccountingLines(capitalAccountingLines);
    }

    /**
     * Checks if the accounting line has an object code that belongs to object sub type group codes and
     * if so, creates a capital accounting line that will be displayed on the jsp.
     *
     * @param capitalAccountingLines
     * @param line
     * @param distributionAmountCode
     * @return List of capitalAccountingLines
     */
    protected List<CapitalAccountingLines> createCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, AccountingLine line, String distributionAmountCode) {
        Integer sequenceNumber = capitalAccountingLines.size() + 1;

        if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(line)) {
            //capital object code so we need to build the capital accounting line...
            CapitalAccountingLines cal = addCapitalAccountingLine(capitalAccountingLines, line);
            cal.setDistributionAmountCode(distributionAmountCode);
            capitalAccountingLines.add(cal);
        }

        return capitalAccountingLines;
    }

    /**
     * convenience method to add a new capital accounting line to the collection of capital
     * accounting lines.
     *
     * @param capitalAccountingLines
     * @param line
     * @return cal newly created capital accounting line
     */
    protected CapitalAccountingLines addCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, AccountingLine line) {
        CapitalAccountingLines cal = new CapitalAccountingLines();

        if (line instanceof SourceAccountingLine) {
            cal.setLineType(KFSConstants.SOURCE);
        }
        else {
            cal.setLineType(KFSConstants.TARGET);
        }
        cal.setSequenceNumber(line.getSequenceNumber());
        cal.setChartOfAccountsCode(line.getChartOfAccountsCode());
        cal.setAccountNumber(line.getAccountNumber());
        cal.setSubAccountNumber(line.getSubAccountNumber());
        cal.setFinancialObjectCode(line.getFinancialObjectCode());
        cal.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
        cal.setProjectCode(line.getProjectCode());
        cal.setOrganizationReferenceId(line.getOrganizationReferenceId());
        cal.setFinancialDocumentLineDescription(line.getFinancialDocumentLineDescription());
        cal.setAmount(line.getAmount());
        cal.setAccountLinePercent(null);
        cal.setSelectLine(false);

        return cal;
    }

    /**
     * sort the capital accounting lines collection based on financial object code and account number.
     * @param capitalAccountingLines List of capital accounting lines
     */
    protected void sortCaptitalAccountingLines(List<CapitalAccountingLines> capitalAccountingLines) {
        CapitalAccountingLinesComparator calComparator = new CapitalAccountingLinesComparator();

        //sort the collection based on financial object code and account number
        Collections.sort(capitalAccountingLines, calComparator);
    }

    /**
     * Supports the generate button on the UI. It generates the capital accounting lines
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward generateAccountingLinesForCapitalization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) form;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();

        CapitalAssetAccountingLineUniquenessEnforcementValidation uniquenessValidation = new CapitalAssetAccountingLineUniquenessEnforcementValidation();
        uniquenessValidation.setAccountingDocumentForValidation(caldb);
        if (uniquenessValidation.validate(new AttributedRouteDocumentEvent(caldb))
                && getRuleService().applyRules(new SaveDocumentEvent(caldb))) {
            String distributionAmountCode = capitalAccountingLinesFormBase.getCapitalAccountingLine().getDistributionCode();

            List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();
            AccountingDocument tdoc = (AccountingDocument) kualiAccountingDocumentFormBase.getDocument();

            createCapitalAccountingLines(capitalAccountingLines, tdoc, distributionAmountCode);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Supports the delete button for capital accounting lines on the UI
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteAccountingLinesForCapitalization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) form;
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) capitalAccountingLinesFormBase.getFinancialDocument();

        if (capitalAccountingLinesFormBase.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT)) {
            caldb.setCapitalAccountingLines(new ArrayList<CapitalAccountingLines>());
            caldb.setCapitalAssetInformation(new ArrayList());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Action "create" creates assets for the selected capital
     * accounting lines.  An error message is shown if no capital accounting lines
     * are selected for processing.  The action checks if the selected
     * capital accounting lines object sub type cross based on the system paramter
     * values for the object subtypes and if so prompts the user to confirm to continue
     * to create the assets for the select capital accounting lines.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
        String distributionAmountCode = calfb.getCapitalAccountingLine().getDistributionCode();
        if (KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE.equals(distributionAmountCode)) {
           calfb.setDistributeEqualAmount(true);
        }
        else {
            calfb.setDistributeEqualAmount(false);
        }

        boolean createAction = calfb.getCapitalAccountingLine().isCanCreateAsset();
        calfb.setEditCreateOrModify(false);

        GlobalVariables.getMessageMap().clearErrorMessages();
        if (!capitalAccountingLinesSelected(calfb)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATAION_REQUIRED_CREATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            calfb.setEditCreateOrModify(false);
        }

        Document document = calfb.getFinancialDocument();

        //if same object subtypes then continue creating capital assets....
        if (checkObjecSubTypeCrossingCapitalAccountingLines(document)) {
            //question the user if to continue....
            ActionForward forward = performQuestionPrompt(mapping, form, request, response, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR, distributionAmountCode);
            if (forward != null) {
                return forward;
            }
        }
        else {
            createCapitalAssetsForSelectedAccountingLines(form , calfb, KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR, distributionAmountCode);
        }

        //restore the tab states....
        setTabStatesForCapitalAssets(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Action "modify" creates assets for the selected capital
     * accounting lines.  An error message is shown if no capital accounting lines
     * are selected for processing.  The action checks if the selected
     * capital accounting lines object sub type cross based on the system paramter
     * values for the object subtypes and if so prompts the user to confirm to continue
     * to create the assets for the select capital accounting lines.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward modifyAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
        String distributionAmountCode = calfb.getCapitalAccountingLine().getDistributionCode();

        if (KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE.equals(distributionAmountCode)) {
            calfb.setDistributeEqualAmount(true);
         }
        else {
            calfb.setDistributeEqualAmount(false);
        }

        GlobalVariables.getMessageMap().clearErrorMessages();
        if (!capitalAccountingLinesSelected(calfb)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATAION_REQUIRED_MODIFY);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            calfb.setEditCreateOrModify(false);
        }

        Document document = calfb.getFinancialDocument();

        //if same object subtypes then continue creating capital assets....
        if (checkObjecSubTypeCrossingCapitalAccountingLines(document)) {
            //question the user if to continue....
            ActionForward forward = performQuestionPrompt(mapping, form, request, response, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR, distributionAmountCode);
            if (forward != null) {
                return forward;
            }
        }
        else {
            createCapitalAssetsForSelectedAccountingLines(form , calfb, KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR, distributionAmountCode);
        }

        //restore the tab states....
        setTabStatesForCapitalAssets(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Helper method to first calculate the percents for the selected capital accounting lines as
     * the percent is required if the user is distributing the amounts by individual amount
     * method.  It then populates created or modified assets with asset accounting lines.
     *
     * @param form
     * @param calfb
     * @param actionTypeIndicator indicates whether creating an asset for "create" or "modify" actions.
     * @param distributionAmountCode amount distribution code
     */
    protected void createCapitalAssetsForSelectedAccountingLines(ActionForm form, CapitalAccountingLinesFormBase calfb, String actionTypeIndicator, String distributionAmountCode) {
        calculatePercentsForSelectedCapitalAccountingLines(calfb);
        createCapitalAssetForGroupAccountingLines(calfb, actionTypeIndicator, distributionAmountCode);
        checkCapitalAccountingLinesSelected(calfb);

        KualiForm kualiForm = (KualiForm) form;
        setTabStatesForCapitalAssets(kualiForm);
    }


    /**
     * checks the capital accounting lines if any of the lines have been selected for
     * further processing.
     *
     * @param calfb
     * @return selected return true if lines have been selected else return false
     */
    protected boolean capitalAccountingLinesSelected(CapitalAccountingLinesFormBase calfb) {
        boolean selected = false;

        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) calfb.getFinancialDocument();
        List<CapitalAccountingLines> capitalAccountingLines = caldb.getCapitalAccountingLines();

        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.isSelectLine()) {
                selected = true;
                break;
            }
        }

        return selected;
    }

   /**
    * runs the validation to check if object subtypes crosses groups on
    * selected capital accounting lines.
    *
    * @param form
    * @return true if rule passed else false
    */
    protected boolean checkObjecSubTypeCrossingCapitalAccountingLines(Document document) {
        boolean differentObjecSubtypes = true;
        differentObjecSubtypes &= getRuleService().applyRules(new CapitalAccountingLinesSameObjectCodeSubTypeEvent(document));

        return differentObjecSubtypes;
    }

    /**
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @param distributionAmountCode
     * @return An ActionForward
     */
    protected ActionForward performQuestionPrompt(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String actionTypeCode, String distributionAmountCode) throws Exception {
        ActionForward forward = null;
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;

        if (question == null) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.WARNING_NOT_SAME_OBJECT_SUB_TYPES);
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.OBJECT_SUB_TYPES_DIFFERENT_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            // If the user replies 'Yes' the question, proceed..
            if (KFSConstants.OBJECT_SUB_TYPES_DIFFERENT_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                createCapitalAssetsForSelectedAccountingLines(form , calfb, actionTypeCode, distributionAmountCode);

                KualiForm kualiForm = (KualiForm) form;
                setTabStatesForCapitalAssets(kualiForm);

                return mapping.findForward(KFSConstants.MAPPING_BASIC);

            }
            // If the user replies 'No' to either of the questions
            else {
                uncheckCapitalAccountingLinesSelected(calfb);
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * Get the rule service
     *
     * @return ruleService
     */
    protected KualiRuleService getRuleService() {
        return SpringContext.getBean(KualiRuleService.class);
    }
}
