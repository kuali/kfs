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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.web.struts.form.AssetTransferForm;

public class AssetTransferAction extends KualiTransactionalDocumentActionBase {
    private static final Logger LOG = Logger.getLogger(AssetTransferAction.class);

    /**
     * This method had to override because asset information has to be refreshed before display
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);
        AssetTransferForm assetTransferForm = (AssetTransferForm) form;
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
        handleRequestFromLookup(request, assetTransferForm, assetTransferDocument);
        handleRequestFromWorkflow(assetTransferForm, assetTransferDocument);
        Asset asset = assetTransferDocument.getAsset();
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
        SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(asset);
        SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(asset);
        return docHandlerForward;
    }

    /**
     * This method handles when request is from a work flow document search
     * 
     * @param assetTransferForm Form
     * @param assetTransferDocument Document
     * @param service BusinessObjectService
     * @param assetHeader Asset header object
     * @return Asset
     */
    private void handleRequestFromWorkflow(AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start- Handle request from workflow");
        if (assetTransferForm.getDocId() != null) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
            UniversalUserService universalUserService = SpringContext.getBean(UniversalUserService.class);
            try {
                UniversalUser universalUser = universalUserService.getUniversalUser(assetTransferDocument.getRepresentativeUniversalIdentifier());
                assetTransferDocument.setAssetRepresentative(universalUser);
            }
            catch (UserNotFoundException e) {
                LOG.error("UniversalUserService returned with UserNotFoundException for uuid " + assetTransferDocument.getRepresentativeUniversalIdentifier());
            }
        }
    }

    /**
     * This method handles the request coming from asset lookup screen
     * 
     * @param request Request
     * @param assetTransferForm Current form
     * @param assetTransferDocument Document
     * @param service Business Object Service
     * @param asset Asset
     * @return Asset
     */
    private void handleRequestFromLookup(HttpServletRequest request, AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start - Handle request from asset lookup screen");
        if (assetTransferForm.getDocId() == null) {
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            assetTransferDocument.setCapitalAssetNumber(Long.valueOf(capitalAssetNumber));
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        }
    }
}
