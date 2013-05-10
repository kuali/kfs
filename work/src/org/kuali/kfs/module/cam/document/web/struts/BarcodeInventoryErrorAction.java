/*
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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.event.ValidateBarcodeInventoryEvent;
import org.kuali.kfs.module.cam.util.BarcodeInventoryErrorDetailPredicate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Action class for the asset barcode inventory error document
 */
public class BarcodeInventoryErrorAction extends FinancialSystemTransactionalDocumentActionBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#sendAdHocRequests(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward sendAdHocRequests(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();

        // Saving data
        getBusinessObjectService().save(document.getBarcodeInventoryErrorDetail());

        return super.sendAdHocRequests(mapping, bcieForm, request, response);
    }

    /** why does this just send false to rules.....
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();
        //prevent approval before save if barcode inventory item list not validated
        if(!document.isDocumentCorrected()){
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.DOCUMENT_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_VALIDATE_ITEMS_BEFORE_APPROVE, document.getDocumentNumber());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        getBusinessObjectService().save(document.getBarcodeInventoryErrorDetail());

        if (this.getAssetBarcodeInventoryLoadService().isCurrentUserInitiator(document)) {
            this.invokeRules(document, false);
        }

        return super.approve(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();

        this.invokeRules(document, false);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) kualiDocumentFormBase;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();

        // Validating records.
        this.invokeRules(document, false);
    }

    /**
     * Searches and replaces BCIE document data on the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward searchAndReplace(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();

        // Validating search criteria
        if (validateGlobalReplaceFields(document)) {
            BarcodeInventoryErrorDetailPredicate predicatedClosure = new BarcodeInventoryErrorDetailPredicate(barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument());

            // searches and replaces
            CollectionUtils.forAllDo(barcodeInventoryErrorDetails, predicatedClosure);

            document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
            barcodeInventoryErrorForm.setDocument(document);
        }
        // Validating.
        this.invokeRules(document, false);

        // Search fields initialization.
        barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument().resetSearchFields();

        // Reseting the checkboxes
        barcodeInventoryErrorForm.resetCheckBoxes();

        // Displaying JSP
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Validates all the selected records and saves them
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();

        String currentUserID = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        List selectedRows = new ArrayList();
        int selectedCheckboxes[] = barcodeInventoryErrorForm.getRowCheckbox();

        // If rows were selected.....
        if (selectedCheckboxes != null) {
            for(int i=0;i < selectedCheckboxes.length;i++) {
                selectedRows.add(selectedCheckboxes[i]);
            }

            // Validate
            this.invokeRules(document, true);

            barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
            for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
                int uploadRowNumber = detail.getUploadRowNumber().intValue();
                if (selectedRows.contains(uploadRowNumber)) {
                    if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_CORRECTED)) {
                        detail.setInventoryCorrectionTimestamp(getDateTimeService().getCurrentTimestamp());
                        detail.setCorrectorUniversalIdentifier(currentUserID);

                        getAssetBarcodeInventoryLoadService().updateAssetInformation(detail, false);
                    }
                } else {
                    //Reseting the status back to error for those rows that were not selected by the user to be validated.
                    //detail.getCorrectorUniversalIdentifier() = null means record has not been saved as corrected
                    // if (detail.getCorrectorUniversalIdentifier() == null) {
                    // detail.getInventoryCorrectionTimestamp() used to determine error_status instead of detail.getCorrectorUniversalIdentifier()
                     if (detail.getInventoryCorrectionTimestamp() == null) {
                        detail.setErrorCorrectionStatusCode(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR);
                    }
                }
            }

        }
        else {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID, CamsKeyConstants.BarcodeInventory.ERROR_CHECKBOX_MUST_BE_CHECKED);
        }

        // Saving data
        getBusinessObjectService().save(document.getBarcodeInventoryErrorDetail());

        // Loading changes on page
        this.loadDocument((KualiDocumentFormBase) form);

        // resetting the checkboxes
        barcodeInventoryErrorForm.resetCheckBoxes();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Deletes selected lines from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;

        String currentUserID = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        int selectedCheckboxes[] = barcodeInventoryErrorForm.getRowCheckbox();

        // If rows were selected
        if (!(selectedCheckboxes == null)) {
            for (int i = 0; i < selectedCheckboxes.length; i++) {
                for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
                    if (detail.getUploadRowNumber().compareTo(new Long(selectedCheckboxes[i])) == 0) {
                        detail.setErrorCorrectionStatusCode(CamsConstants.BarCodeInventoryError.STATUS_CODE_DELETED);
                        detail.setInventoryCorrectionTimestamp(getDateTimeService().getCurrentTimestamp());
                        detail.setCorrectorUniversalIdentifier(currentUserID);
                    }
                }
            }
        }
        else {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID, CamsKeyConstants.BarcodeInventory.ERROR_CHECKBOX_MUST_BE_CHECKED);
        }

        // Saving data
        getBusinessObjectService().save(document.getBarcodeInventoryErrorDetail());

        // Clearing the checkboxes
        barcodeInventoryErrorForm.resetCheckBoxes();

        this.loadDocument((KualiDocumentFormBase) form);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    protected boolean validateGlobalReplaceFields(BarcodeInventoryErrorDocument document) {
        if (StringUtils.isBlank(document.getCurrentScanCode()) && StringUtils.isBlank(document.getCurrentCampusCode()) && StringUtils.isBlank(document.getCurrentBuildingNumber()) && StringUtils.isBlank(document.getCurrentRoom()) && StringUtils.isBlank(document.getCurrentSubroom()) && StringUtils.isBlank(document.getCurrentConditionCode()) && StringUtils.isBlank(document.getCurrentTagNumber())) {

            GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.BCIE_GLOBAL_REPLACE_ERROR_SECTION_ID, CamsKeyConstants.BarcodeInventory.ERROR_GLOBAL_REPLACE_SEARCH_CRITERIA);
            return false;
        }
        return true;
    }

    /**
     * Invokes the method that validates the bar code inventory error records and that resides in the rule class
     * BarcodeInventoryErrorDocumentRule
     * 
     * @param document
     */
    protected void invokeRules(BarcodeInventoryErrorDocument document, boolean updateStatus) {
        getKualiRuleService().applyRules(new ValidateBarcodeInventoryEvent("", document, updateStatus));
    }

    protected AssetBarcodeInventoryLoadService getAssetBarcodeInventoryLoadService() {
        return SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
    }

    protected DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }
}
