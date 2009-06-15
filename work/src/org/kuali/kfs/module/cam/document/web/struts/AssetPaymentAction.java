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
package org.kuali.kfs.module.cam.document.web.struts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.validation.event.AssetPaymentAddAssetEvent;
import org.kuali.kfs.module.cam.document.validation.event.AssetPaymentManuallyAddAccountingLineEvent;
import org.kuali.kfs.module.cam.document.validation.event.AssetPaymentPrepareRouteEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

public class AssetPaymentAction extends KualiAccountingDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentAction.class);


    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.docHandler(mapping, form, request, response);
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        String command = assetPaymentForm.getCommand();

        // If a asset was selected from the asset payment lookup page then insert asset into the document.
        if (KEWConstants.INITIATE_COMMAND.equals(command) && ((assetPaymentForm.getCapitalAssetNumber() != null) && !assetPaymentForm.getCapitalAssetNumber().trim().equals(""))) {
            List<AssetPaymentAssetDetail> assetPaymentAssetDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail();

            AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
            assetPaymentAssetDetail.setDocumentNumber(assetPaymentForm.getAssetPaymentDocument().getDocumentNumber());
            assetPaymentAssetDetail.setCapitalAssetNumber(new Long(assetPaymentForm.getCapitalAssetNumber()));
            assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
            assetPaymentAssetDetail.setPreviousTotalCostAmount(assetPaymentAssetDetail.getAsset().getTotalCostAmount());

            assetPaymentAssetDetails.add(assetPaymentAssetDetail);
            assetPaymentForm.getAssetPaymentDocument().setAssetPaymentAssetDetail(assetPaymentAssetDetails);
            assetPaymentForm.setCapitalAssetNumber("");
        }
        return actionForward;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        Collection<PersistableBusinessObject> rawValues = null;
        Map<String, Set<String>> segmentedSelection = new HashMap<String, Set<String>>();

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, assetPaymentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = assetPaymentForm.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
                for (String selectedId : selectedIds) {
                    String selectedObjId = StringUtils.substringBefore(selectedId, ".");
                    String selectedMonthData = StringUtils.substringAfter(selectedId, ".");

                    if (!segmentedSelection.containsKey(selectedObjId)) {
                        segmentedSelection.put(selectedObjId, new HashSet<String>());
                    }
                    segmentedSelection.get(selectedObjId).add(selectedMonthData);
                }
                // Retrieving selected data from table.
                LOG.debug("Asking segmentation service for object ids " + segmentedSelection.keySet());
                rawValues = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, segmentedSelection.keySet(), Asset.class, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            }

            List<AssetPaymentAssetDetail> assetPaymentAssetDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail();
            if (rawValues != null) {
                for (PersistableBusinessObject bo : rawValues) {
                    Asset asset = (Asset) bo;

                    boolean addIt = true;
                    for (AssetPaymentAssetDetail detail : assetPaymentAssetDetails) {
                        if (detail.getCapitalAssetNumber().compareTo(asset.getCapitalAssetNumber()) == 0) {
                            addIt = false;
                            break;
                        }
                    }

                    // If it doesn't already exist in the list add it.
                    if (addIt) {
                        AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
                        assetPaymentAssetDetail.setDocumentNumber(assetPaymentForm.getAssetPaymentDocument().getDocumentNumber());
                        assetPaymentAssetDetail.setCapitalAssetNumber(asset.getCapitalAssetNumber());
                        assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
                        assetPaymentAssetDetail.setPreviousTotalCostAmount(assetPaymentAssetDetail.getAsset().getTotalCostAmount());

                        assetPaymentAssetDetails.add(assetPaymentAssetDetail);
                    }
                }
                assetPaymentForm.getAssetPaymentDocument().setAssetPaymentAssetDetail(assetPaymentAssetDetails);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#uploadAccountingLines(boolean,
     *      org.apache.struts.action.ActionForm)
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        super.uploadAccountingLines(isSource, assetPaymentForm);
        List<AssetPaymentDetail> assetPaymentDetails = assetPaymentForm.getAssetPaymentDocument().getSourceAccountingLines();
        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            getAssetPaymentService().extractPostedDatePeriod(assetPaymentDetail);
        }
    }


    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        SourceAccountingLine line = assetPaymentForm.getNewSourceLine();
        boolean rulePassed = true;

        // Check any business rules. We separate general accounting line validation into AssetPaymentManuallyAddAccountingLineEvent,
        // and trigger it from this action, also document save.
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AssetPaymentManuallyAddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, assetPaymentForm.getDocument(), line));
        if (rulePassed) {
            rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, assetPaymentForm.getDocument(), line));
        }
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, assetPaymentForm, line);

            // clear the used new source line
            assetPaymentForm.setNewSourceLine(new AssetPaymentDetail());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Inserts a new asset into the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertAssetPaymentAssetDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        AssetPaymentDocument assetPaymentDocument = assetPaymentForm.getAssetPaymentDocument();

        List<AssetPaymentAssetDetail> assetPaymentDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail();

        AssetPaymentAssetDetail newAssetPaymentAssetDetail = new AssetPaymentAssetDetail();
        String sCapitalAssetNumber = assetPaymentForm.getCapitalAssetNumber();

        String errorPath = CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

        // Validating the asset code is numeric
        Long capitalAssetNumber = null;
        try {
            capitalAssetNumber = Long.parseLong(sCapitalAssetNumber);
        }
        catch (NumberFormatException e) {
            // Validating the asset number field is not empty
            if (ObjectUtils.isNull(sCapitalAssetNumber) || StringUtils.isBlank(sCapitalAssetNumber)) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentAssetDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(errorPath, KFSKeyConstants.ERROR_REQUIRED, label);
            }
            else {
                // it is not empty but has an invalid value.
                GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, sCapitalAssetNumber);
            }
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        boolean rulePassed = true;

        newAssetPaymentAssetDetail.setDocumentNumber(assetPaymentDocument.getDocumentNumber());
        newAssetPaymentAssetDetail.setCapitalAssetNumber(capitalAssetNumber);
        newAssetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
        // Validating the new asset
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AssetPaymentAddAssetEvent(errorPath, assetPaymentForm.getDocument(), newAssetPaymentAssetDetail));
        if (rulePassed) {
            // Storing the current asset cost.
            newAssetPaymentAssetDetail.setPreviousTotalCostAmount(newAssetPaymentAssetDetail.getAsset().getTotalCostAmount());

            assetPaymentForm.getAssetPaymentDocument().addAssetPaymentAssetDetail(newAssetPaymentAssetDetail);
            assetPaymentForm.setCapitalAssetNumber("");
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes an asset from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAssetPaymentAssetDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        int deleteIndex = getLineToDelete(request);

        // Getting the asset number that is going to be deleted from document
        Long capitalAssetNumber = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail().get(deleteIndex).getCapitalAssetNumber();

        // Deleting the asset from document
        assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentDocument assetPaymentDocument = ((AssetPaymentForm) form).getAssetPaymentDocument();
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        // run all validation first
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AssetPaymentPrepareRouteEvent(errorPath, assetPaymentDocument));
        if (rulePassed) {
            // this super method call could trigger the warning message of object sub type code from payment lines not matching the
            // one from assets.
            return super.route(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentDocument assetPaymentDocument = ((AssetPaymentForm) form).getAssetPaymentDocument();
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        // run all validation first
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AssetPaymentPrepareRouteEvent(errorPath, assetPaymentDocument));
        if (rulePassed) {
            return super.blanketApprove(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    private AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

}