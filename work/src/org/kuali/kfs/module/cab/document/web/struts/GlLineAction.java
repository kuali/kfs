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
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAssetDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;

public class GlLineAction extends KualiAction {
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        ActionForward forward = mapping.findForward("basic");
        // find the GL amount
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        GeneralLedgerEntry generalLedgerEntry = null;
        if (question == null) {
            String glId = request.getParameter("generalLedgerAccountIdentifier");
            GlobalVariables.getUserSession().addObject("generalLedgerAccountIdentifier", Long.valueOf(glId));
            generalLedgerEntry = findGeneralLedgerEntry(Long.valueOf(glId));
            if (generalLedgerEntry.computePayment().isGreaterEqual(new KualiDecimal(5000))) {
                forward = this.performQuestionWithoutInput(mapping, form, request, response, "CabNewAssetOrOldAsset", "Do you want to create new assets or assign to existing assets? Click YES to create new assets.", KFSConstants.CONFIRMATION_QUESTION, "start", "");
            }
            else {
                forward = mapping.findForward("oldGlAssets");
            }
        }
        else {
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
        return forward;
    }

    private GeneralLedgerEntry findGeneralLedgerEntry(Long glId) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Long> key = new HashMap<String, Long>();
        key.put("generalLedgerAccountIdentifier", glId);
        return (GeneralLedgerEntry) boService.findByPrimaryKey(GeneralLedgerEntry.class, key);
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
        forward = redirectToAssets(mapping, form);
        return forward;
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
