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
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * 
 * Action class for the asset barcode inventory error document 
 */
public class BarcodeInventoryErrorAction extends FinancialSystemTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorAction.class);

    private static final KualiRuleService kualiRuleService = SpringContext.getBean(KualiRuleService.class);
    private static final DocumentService documentService = SpringContext.getBean(DocumentService.class);
    private static final AssetBarcodeInventoryLoadService assetBarcodeInventoryLoadService = SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
    private static final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private static final DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    /**
     * Adds handling for cash control detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm apForm = (BarcodeInventoryErrorForm) form;
        String command = ((BarcodeInventoryErrorForm) form).getCommand();
        String docID = ((BarcodeInventoryErrorForm) form).getDocId();

        LOG.info("***BarcodeInventoryErrorAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID);
        return super.execute(mapping, form, request, response);
    }*/

    /**
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();        
        
        this.invokeRules(document,false);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }



    /**
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) kualiDocumentFormBase;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();

        //Validating records.
        this.invokeRules(document,false);
    }


    /**
     * 
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

        String currentUserID = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        BarcodeInventoryErrorDetailPredicate predicatedClosure = new BarcodeInventoryErrorDetailPredicate(barcodeInventoryErrorForm);

        //searches and replaces
        CollectionUtils.forAllDo(barcodeInventoryErrorDetails, predicatedClosure);

        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
        barcodeInventoryErrorForm.setDocument(document);

        //Validating.
        this.invokeRules(document,false);

        //Checking whether or not an BCIE row was actually corrected
//        barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
//        for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
//
//            //if corrected set user id that corrected and  date. 
//            if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED)) {
//                detail.setInventoryCorrectionTimestamp(dateTimeService.getCurrentTimestamp());
//                detail.setCorrectorUniversalIdentifier(currentUserID);
//
//                //Updating asset table 
//                assetBarcodeInventoryLoadService.updateAssetInformation(detail);
//            }
//        }
//
//
//        if (this.isFullyProcessed(document)) {                    
//            //If the same person that uploaded the bcie is the one processing it, then....
//            if (document.getUploaderUniversalIdentifier().equals(currentUserID)) {
//                //Approving and storing data
//                this.blanketApprove(mapping, form, request, response);
//            }
//        }
//        else {
//            //Storing data.
//            businessObjectService.save(document.getBarcodeInventoryErrorDetail());
//        }
        
        //Search fields initialization.
        barcodeInventoryErrorForm.resetSearchFields();

        //Re-loading document with changes.
//        this.loadDocument((KualiDocumentFormBase) form);


        //Displaying JSP
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
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

        String currentUserID = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        int selectedCheckboxes[] = barcodeInventoryErrorForm.getRowCheckbox();

        // If rows were selected.....
        if (selectedCheckboxes != null) {

            //Validate
            this.invokeRules(document,true);

            barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
            for (int i = 0; i < selectedCheckboxes.length; i++) {
                for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
                    if (detail.getUploadRowNumber().compareTo(new Long(selectedCheckboxes[i])) == 0) {
                        if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED)) {
                            detail.setInventoryCorrectionTimestamp(dateTimeService.getCurrentTimestamp());
                            detail.setCorrectorUniversalIdentifier(currentUserID);

                            assetBarcodeInventoryLoadService.updateAssetInformation(detail);
                        }
                    }
                }
            }

            if (this.isFullyProcessed(document)) {                    
                //If the same person that uploaded the bcie is the one processing it, then....
                if (document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                    this.blanketApprove(mapping, form, request, response);
                }
            }
            else {
                businessObjectService.save(document.getBarcodeInventoryErrorDetail());
            }
        }

        //Loading changes on page
        this.loadDocument((KualiDocumentFormBase) form);

        //resetting the checkboxes
        barcodeInventoryErrorForm.resetCheckBoxes();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * 
     * Deletes selected lines from the document
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

        String currentUserID = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        int selectedCheckboxes[] = barcodeInventoryErrorForm.getRowCheckbox();

        //If rows were selected
        if (!(selectedCheckboxes == null)) {
            for (int i = 0; i < selectedCheckboxes.length; i++) {
                for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
                    if (detail.getUploadRowNumber().compareTo(new Long(selectedCheckboxes[i])) == 0) {
                        detail.setErrorCorrectionStatusCode(CamsConstants.BarcodeInventoryError.STATUS_CODE_DELETED);
                        detail.setInventoryCorrectionTimestamp(dateTimeService.getCurrentTimestamp());
                        detail.setCorrectorUniversalIdentifier(currentUserID);
                    }
                }
            }

            if (this.isFullyProcessed(document)) {
                //If the same person that uploaded the bcie is the one processing it, then....
                if (document.getUploaderUniversalIdentifier().equals(currentUserID)) {
                    this.blanketApprove(mapping, barcodeInventoryErrorForm, request, response);
                }
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
     * 
     * Invokes the method that validates the bar code inventory error records and that resides in 
     * the rule class BarcodeInventoryErrorDocumentRule
     * 
     * @param document
     */
    private void invokeRules(BarcodeInventoryErrorDocument document, boolean updateStatus) {
        kualiRuleService.applyRules(new ValidateBarcodeInventoryEvent("", document,updateStatus));
    }


    /**
     * Determines whether or not the BCIE document has all its records corrected or deleted
     * 
     * @param document
     * @return boolean
     */
    private boolean isFullyProcessed(BarcodeInventoryErrorDocument document) {
        boolean result = true;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;

        for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
            if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR)) {
                result = false;
                break;
            }
        }
        return result;
    }
}