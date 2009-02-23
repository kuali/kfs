/*
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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.event.ValidateBarcodeInventoryEvent;
import org.kuali.kfs.module.cam.util.BarcodeInventoryErrorDetailPredicate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * Action class for the asset barcode inventory error document
 */
public class BarcodeInventoryErrorAction extends FinancialSystemTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorAction.class);

    /**
     * Add initiator as adhoc recipient if error exists and current user is not initiator.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (form != null) {
            BarcodeInventoryErrorDocument barcodeErrorDocument = (BarcodeInventoryErrorDocument) ((KualiDocumentFormBase) form).getDocument();
            getAssetBarcodeInventoryLoadService().conditionllyAddInitiatorAdhocRecipient(barcodeErrorDocument);
        }
        return super.approve(mapping, form, request, response);
    }


    /**
     * Adds handling for cash control detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * @Override public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
     *           HttpServletResponse response) throws Exception { BarcodeInventoryErrorForm apForm = (BarcodeInventoryErrorForm)
     *           form; String command = ((BarcodeInventoryErrorForm) form).getCommand(); String docID = ((BarcodeInventoryErrorForm)
     *           form).getDocId(); LOG.info("***BarcodeInventoryErrorAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " -
     *           Command:" + command + " - DocId:" + docID); return super.execute(mapping, form, request, response); }
     */

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

        String currentUserID = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        BarcodeInventoryErrorDetailPredicate predicatedClosure = new BarcodeInventoryErrorDetailPredicate(barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument());

        // searches and replaces
        CollectionUtils.forAllDo(barcodeInventoryErrorDetails, predicatedClosure);

        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
        barcodeInventoryErrorForm.setDocument(document);

        // Validating.
        this.invokeRules(document, false);

        // Search fields initialization.
        barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument().resetSearchFields();

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

        int selectedCheckboxes[] = barcodeInventoryErrorForm.getRowCheckbox();

        // If rows were selected.....
        if (selectedCheckboxes != null) {

            // Validate
            this.invokeRules(document, true);

            barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
            for (int i = 0; i < selectedCheckboxes.length; i++) {
                for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
                    if (detail.getUploadRowNumber().compareTo(new Long(selectedCheckboxes[i])) == 0) {
                        if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_CORRECTED)) {
                            detail.setInventoryCorrectionTimestamp(getDateTimeService().getCurrentTimestamp());
                            detail.setCorrectorUniversalIdentifier(currentUserID);

                            getAssetBarcodeInventoryLoadService().updateAssetInformation(detail);
                        }
                    }
                }
            }

            if (getAssetBarcodeInventoryLoadService().isFullyProcessed(document) && document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                //TODO: review if we still need this blacketapprove feature
                // If the same person that uploaded the bcie is the one processing it, then....
               // if (document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                    this.blanketApprove(mapping, form, request, response);
               // }
            }
            else {
                getBusinessObjectService().save(document.getBarcodeInventoryErrorDetail());
            }
        }

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

            if (getAssetBarcodeInventoryLoadService().isFullyProcessed(document) && document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                //TODO: review if we still need this blacketapprove feature
                // If the same person that uploaded the bcie is the one processing it, then....
                //if (document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                    this.blanketApprove(mapping, barcodeInventoryErrorForm, request, response);
                //}
            }
            else {
                this.save(mapping, barcodeInventoryErrorForm, request, response);
            }
        }
        barcodeInventoryErrorForm.resetCheckBoxes();
        this.loadDocument((KualiDocumentFormBase) form);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invokes the method that validates the bar code inventory error records and that resides in the rule class
     * BarcodeInventoryErrorDocumentRule
     * 
     * @param document
     */
    private void invokeRules(BarcodeInventoryErrorDocument document, boolean updateStatus) {
        getKualiRuleService().applyRules(new ValidateBarcodeInventoryEvent("", document, updateStatus));
    }


    private AssetBarcodeInventoryLoadService getAssetBarcodeInventoryLoadService() {
        return SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
    }

    private DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }
}
