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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAssetDetail;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;

public class GlLineAction extends KualiAction {
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        ActionForward forward = mapping.findForward("basic");
        String glIdStr = request.getParameter("generalLedgerAccountIdentifier");
        GeneralLedgerEntry generalLedgerEntry = null;

        if (glIdStr != null) {
            generalLedgerEntry = findGeneralLedgerEntry(Long.valueOf(glIdStr));
        }
        // if asset is already assigned or added...
        if (ObjectUtils.isNotNull(generalLedgerEntry) && !generalLedgerEntry.getGeneralLedgerEntryAssets().isEmpty() && !generalLedgerEntry.getGeneralLedgerEntryAssets().get(0).getGeneralLedgerEntryAssetDetails().isEmpty()) {
            boolean newAssetIndicator = generalLedgerEntry.getGeneralLedgerEntryAssets().get(0).getGeneralLedgerEntryAssetDetails().get(0).isNewAssetIndicator();
            glLineForm.setGeneralLedgerEntry(generalLedgerEntry);
            if (newAssetIndicator) {
                glLineForm.setNewAssetIndicator(true);
                forward = mapping.findForward("newGlAssets");
            }
            else {
                glLineForm.setCapitalAssetNumber(generalLedgerEntry.getGeneralLedgerEntryAssets().get(0).getGeneralLedgerEntryAssetDetails().get(0).getCapitalAssetNumber().toString());
                glLineForm.setAsset(findAsset(Long.valueOf(glLineForm.getCapitalAssetNumber())));
                glLineForm.setNewAssetIndicator(false);
                forward = mapping.findForward("oldGlAssets");
            }
        }
        else {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

            if (question == null && ObjectUtils.isNotNull(generalLedgerEntry)) {
                GlobalVariables.getUserSession().addObject("generalLedgerAccountIdentifier", Long.valueOf(glIdStr));
                if (generalLedgerEntry.computePayment().isGreaterEqual(new KualiDecimal(5000))) {
                    forward = this.performQuestionWithoutInput(mapping, form, request, response, "CabNewAssetOrOldAsset", "Do you want to create new assets or assign to existing assets? Click YES to create new assets.", KFSConstants.CONFIRMATION_QUESTION, "start", "");
                }
                else {
                    forward = mapping.findForward("oldGlAssets");
                }
            }
            else if (question != null) {
                Long glId = (Long) GlobalVariables.getUserSession().retrieveObject("generalLedgerAccountIdentifier");
                generalLedgerEntry = findGeneralLedgerEntry(glId);
                glLineForm.setGeneralLedgerEntry(generalLedgerEntry);
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ("CabNewAssetOrOldAsset".equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    glLineForm.setNewAssetIndicator(true);
                    forward = mapping.findForward("newGlAssets");
                }
                else {
                    glLineForm.setNewAssetIndicator(false);
                    forward = mapping.findForward("oldGlAssets");
                }
            }
        }
        return forward;
    }

    private GeneralLedgerEntry findGeneralLedgerEntry(Long glId) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Long> key = new HashMap<String, Long>();
        key.put("generalLedgerAccountIdentifier", glId);
        GeneralLedgerEntry entry = (GeneralLedgerEntry) boService.findByPrimaryKey(GeneralLedgerEntry.class, key);
        if (ObjectUtils.isNull(entry) || !entry.isActive()) {
            GlobalVariables.getErrorMap().putError("GeneralLedgerEntry", "invalid.generalledger.account.id", glId.toString());
        }
        return entry;
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.refresh(mapping, form, request, response);
        forward = redirectToAssets(mapping, form);
        return forward;
    }

    private ActionForward redirectToAssets(ActionMapping mapping, ActionForm form) {
        GlLineForm glLineForm = (GlLineForm) form;
        ActionForward forward;
        if (glLineForm.isNewAssetIndicator()) {
            forward = mapping.findForward("newGlAssets");
        }
        else {
            forward = mapping.findForward("oldGlAssets");
        }
        return forward;
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = null;
        GlLineForm glLineForm = (GlLineForm) form;
        if (glLineForm.isNewAssetIndicator()) {

        }
        else {
            DictionaryValidationService validationService = SpringContext.getBean(DictionaryValidationService.class);
            validationService.validateAttributeFormat("org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAssetDetail", "capitalAssetNumber", glLineForm.getCapitalAssetNumber(), "capitalAssetNumber");

            Asset asset = null;
            try {
                Long capitalAssetNumber = Long.valueOf(glLineForm.getCapitalAssetNumber());
                asset = findAsset(capitalAssetNumber);
            }
            catch (Exception e) {
                asset = null;
            }
            if (ObjectUtils.isNotNull(asset)) {
                saveAssignAssetsData(glLineForm, asset.getCapitalAssetNumber());
            }
        }
        forward = redirectToAssets(mapping, form);
        return forward;
    }

    // TODO - this should be moved to service implementation
    private void saveAssignAssetsData(GlLineForm glLineForm, Long capitalAssetNumber) {
        GeneralLedgerEntry entry = findGeneralLedgerEntry(glLineForm.getGeneralLedgerEntry().getGeneralLedgerAccountIdentifier());
        GeneralLedgerEntryAsset entryAsset = null;
        if (entry.getGeneralLedgerEntryAssets().isEmpty()) {
            entryAsset = new GeneralLedgerEntryAsset();
            entryAsset.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
            entryAsset.setCapitalAssetBuilderLineNumber(1);
            entryAsset.setVersionNumber(0L);
            entryAsset.setCapitalAssetBuilderQuantity(1L);
            entry.getGeneralLedgerEntryAssets().add(entryAsset);
        }
        else {
            entryAsset = entry.getGeneralLedgerEntryAssets().get(0);
        }
        GeneralLedgerEntryAssetDetail assetDetail = null;
        if (entryAsset.getGeneralLedgerEntryAssetDetails().isEmpty()) {
            assetDetail = new GeneralLedgerEntryAssetDetail();
            assetDetail.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
            assetDetail.setCapitalAssetBuilderLineNumber(1);
            assetDetail.setVersionNumber(0L);
            entryAsset.getGeneralLedgerEntryAssetDetails().add(assetDetail);
        }
        else {
            assetDetail = entryAsset.getGeneralLedgerEntryAssetDetails().get(0);
            SpringContext.getBean(BusinessObjectService.class).delete(assetDetail);
        }
        assetDetail.setCapitalAssetNumber(capitalAssetNumber);
        assetDetail.setNewAssetIndicator(false);
        SpringContext.getBean(BusinessObjectService.class).save(entry);
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KNSConstants.MAPPING_PORTAL);
    }

    @Override
    public ActionForward hideAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.hideAllTabs(mapping, form, request, response);
        return redirectToAssets(mapping, form);
    }

    @Override
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.showAllTabs(mapping, form, request, response);
        return redirectToAssets(mapping, form);
    }

    public ActionForward addAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        GeneralLedgerEntry generalLedgerEntry = glLineForm.getGeneralLedgerEntry();
        if (generalLedgerEntry.getGeneralLedgerEntryAssets().isEmpty()) {
            generalLedgerEntry.getGeneralLedgerEntryAssets().add(glLineForm.getNewGeneralLedgerEntryAsset());
        }
        generalLedgerEntry.getGeneralLedgerEntryAssets().get(0).getGeneralLedgerEntryAssetDetails().add(glLineForm.getNewGeneralLedgerEntryAssetDetail());
        glLineForm.setNewGeneralLedgerEntryAssetDetail(new GeneralLedgerEntryAssetDetail());
        return redirectToAssets(mapping, form);
    }

    public ActionForward assignAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        String capitalAssetNumberString = glLineForm.getCapitalAssetNumber();
        DictionaryValidationService validationService = SpringContext.getBean(DictionaryValidationService.class);
        validationService.validateAttributeFormat("org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAssetDetail", "capitalAssetNumber", glLineForm.getCapitalAssetNumber(), "capitalAssetNumber");
        if (GlobalVariables.getErrorMap().isEmpty()) {
            glLineForm.setAsset(findAsset(Long.valueOf(capitalAssetNumberString)));
        }
        return redirectToAssets(mapping, form);
    }

    private Asset findAsset(Long capitalAssetNumber) {
        if (capitalAssetNumber != null) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            Map<String, Long> key = new HashMap<String, Long>();
            key.put("capitalAssetNumber", capitalAssetNumber);
            Asset assetVal = (Asset) boService.findByPrimaryKey(Asset.class, key);
            if (ObjectUtils.isNotNull(assetVal)) {
                return assetVal;
            }
            else {
                GlobalVariables.getErrorMap().putError("capitalAssetNumber", "gl.assign.asset.invalid.capital.asset.number", capitalAssetNumber.toString());
            }
        }
        return null;
    }

    public ActionForward deleteAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        GeneralLedgerEntry generalLedgerEntry = glLineForm.getGeneralLedgerEntry();
        generalLedgerEntry.getGeneralLedgerEntryAssets().get(0).getGeneralLedgerEntryAssetDetails().remove(getSelectedLine(request));
        return redirectToAssets(mapping, form);
    }

    protected int getSelectedLine(HttpServletRequest request) {
        int selectedLine = -1;
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".line", ".");
            selectedLine = Integer.parseInt(lineNumber);
        }

        return selectedLine;
    }
}
