/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.web.struts.action;

import static org.kuali.module.cams.CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.web.struts.form.AssetPaymentForm;
import org.kuali.module.cams.web.struts.form.AssetTransferForm;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.financial.web.struts.form.JournalVoucherForm;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

public class AssetPaymentAction extends KualiAccountingDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentAction.class);

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm apForm = (AssetPaymentForm) form;
        String command = ((AssetPaymentForm) form).getCommand();
        String docID = ((AssetPaymentForm) form).getDocId();
        String capitalAssetNumber = ((AssetPaymentForm) form).getCapitalAssetNumber();
        LOG.info("***AssetPaymentAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID + " - Capital Asset Number:" + capitalAssetNumber);
        return super.execute(mapping, form, request, response);
    }
    
    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) kualiDocumentFormBase;
        AssetPaymentDocument assetPaymentDocument = assetPaymentForm.getAssetPaymentDocument();

        String capitalAssetNumber = assetPaymentForm.getCapitalAssetNumber();

        //Retrieving the asset data object for the selected asset. 
        Asset asset = assetPaymentDocument.getAsset();
        
        //asset = handleRequestFromLookup(capitalAssetNumber, assetPaymentForm, assetPaymentDocument, asset);
        asset = handleRequestFromLookup(capitalAssetNumber, assetPaymentDocument);

        //Populating the hidden fields in the assetPayment.jsp
        assetPaymentDocument.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        assetPaymentDocument.setOrganizationOwnerAccountNumber(asset.getOrganizationOwnerAccountNumber());
        assetPaymentDocument.setOrganizationOwnerChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());
        assetPaymentDocument.setCampusCode(asset.getCampusCode());
        assetPaymentDocument.setAgencyNumber(asset.getAgencyNumber());
        assetPaymentDocument.setBuildingCode(asset.getBuildingCode());
        assetPaymentDocument.setRepresentativeUniversalIdentifier(asset.getRepresentativeUniversalIdentifier());

        //Adding the changes made in the document in the ActionForm.
        assetPaymentForm.setDocument(assetPaymentDocument);
    }

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override    
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward;
        AssetPaymentForm apForm     = (AssetPaymentForm) form;
        AssetPaymentDocument apDoc  = apForm.getAssetPaymentDocument();

        apDoc.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);        
        if (apDoc.hasDifferentObjectSubTypes()) {
            //Verifying that the asset payments have the same object sub types. If it isn't the case then we need to 
            //confirmation from the user before submitting the payment.
            returnForward = processDifferentObjectSubTypeConfirmationQuestion(mapping, form, request, response);
    
            // if not null, then the question component either has control of the flow and needs to ask its questions
            // or the person chose the "cancel" or "no" button. Otherwise we have control.
            if (returnForward != null) {
                return returnForward;
            }
        } 
        return super.route(mapping, form, request, response);
    }
    
    /**
     * 
     * Displays a confirmation question to let the user know there are payments for a particular asset that have
     * different object sub type codes
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    protected ActionForward processDifferentObjectSubTypeConfirmationQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm apForm     = (AssetPaymentForm) form;
        AssetPaymentDocument apDoc  = apForm.getAssetPaymentDocument();

        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        if (question == null) { // question hasn't been asked
            // now transfer control over to the question component
            String warningMessage=kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.WARNING_NOT_SAME_OBJECT_SUB_TYPES);
            return this.performQuestionWithoutInput(mapping, form, request, response, CamsConstants.ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION,warningMessage, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.SAVE_METHOD, "");
        }
        else {
            String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            
            if ((CamsConstants.ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION.equals(question)) && 
                 ConfirmationQuestion.NO.equals(buttonClicked)) {
                GlobalVariables.getMessageList().add(CamsKeyConstants.Payment.MESSAGE_PAYMENT_WAS_NOT_SUBMITTED);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        return null;
    }
    
    /**
     * 
     * This method retrieves the asset records for a selected asset number
     * @param capitalAssetNumber
     * @param assetPaymentForm
     * @param assetPaymentDocument
     * @param asset
     * @return Asset 
     */
    private Asset handleRequestFromLookup(String capitalAssetNumber, AssetPaymentDocument assetPaymentDocument) {
        Asset newAsset = new Asset();
        HashMap<String, Object> keys = new HashMap<String, Object>();
        keys.put(CAPITAL_ASSET_NUMBER, capitalAssetNumber.toString());

        newAsset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, keys);
        if (newAsset != null) {
            assetPaymentDocument.setAsset(newAsset);
        }
        return newAsset;
    }

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#uploadAccountingLines(boolean, org.apache.struts.action.ActionForm)
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        
        super.uploadAccountingLines(isSource, assetPaymentForm);
    }
}
