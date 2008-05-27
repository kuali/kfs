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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.web.struts.form.EquipmentLoanOrReturnForm;


public class EquipmentLoanOrReturnAction extends KualiTransactionalDocumentActionBase {
    /**
     * public EquipmentLoanOrReturnAction() { super(); // TODO Auto-generated constructor stub }
     */
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnAction.class);

    /**
     * This method had to override because asset information has to be refreshed before display
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);
        EquipmentLoanOrReturnForm equipmentLoanOrReturnForm = (EquipmentLoanOrReturnForm) form;
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) equipmentLoanOrReturnForm.getDocument();
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);

        AssetHeader assetHeader = equipmentLoanOrReturnDocument.getAssetHeader();
        Asset asset = equipmentLoanOrReturnDocument.getAsset();
        asset = handleRequestFromLookup(request, equipmentLoanOrReturnForm, equipmentLoanOrReturnDocument, service, asset);

        if (equipmentLoanOrReturnDocument.getAsset() != null && (equipmentLoanOrReturnDocument.getAssetHeader() == null || assetHeader.getDocumentNumber() == null)) {
            assetHeader = new AssetHeader();
            assetHeader.setDocumentNumber(equipmentLoanOrReturnDocument.getDocumentNumber());
            assetHeader.setCapitalAssetNumber(equipmentLoanOrReturnDocument.getAsset().getCapitalAssetNumber());
            equipmentLoanOrReturnDocument.setAssetHeader(assetHeader);
            equipmentLoanOrReturnDocument.setCampusTagNumber(asset.getCampusTagNumber());
            equipmentLoanOrReturnDocument.setOrganizationTagNumber(asset.getOrganizationTagNumber());
        }

        asset = handleRequestFromWorkflow(equipmentLoanOrReturnForm, equipmentLoanOrReturnDocument, service, assetHeader);
        asset = equipmentLoanOrReturnDocument.getAsset();

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
    private Asset handleRequestFromWorkflow(EquipmentLoanOrReturnForm equipmentLoanOrReturnForm, EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument, BusinessObjectService businessObjectService, AssetHeader assetHeader) {
        Asset newAsset = new Asset();
        if (equipmentLoanOrReturnForm.getDocId() != null && assetHeader != null) {
            newAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
            newAsset = (Asset) businessObjectService.retrieve(newAsset);
            equipmentLoanOrReturnDocument.setAsset(newAsset);
        }
        return newAsset;
    }

    /**
     * This method handles the request coming from asset lookup screen
     * 
     * @param request Request
     * @param equipmentLoanOrReturnForm Current form
     * @param equipmentLoanOrReturnDocument Document
     * @param service Business Object Service
     * @param asset Asset
     * @return Asset
     */
    private Asset handleRequestFromLookup(HttpServletRequest request, EquipmentLoanOrReturnForm equipmentLoanOrReturnForm, EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument, BusinessObjectService businessObjectService, Asset asset) {
        Asset newAsset = asset;
        if (equipmentLoanOrReturnForm.getDocId() == null && asset == null) {
            newAsset = new Asset();
            HashMap<String, Object> keys = new HashMap<String, Object>();
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            keys.put(CAPITAL_ASSET_NUMBER, capitalAssetNumber);
            newAsset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, keys);


            if (newAsset != null) {
                if (!request.getParameter(CamsConstants.AssetActions.LOAN_TYPE).equals(CamsConstants.AssetActions.LOAN)) {
                    populateEquipmentLoanOrReturnDocument(equipmentLoanOrReturnDocument, newAsset);
                }
                if (request.getParameter(CamsConstants.AssetActions.LOAN_TYPE).equals(CamsConstants.AssetActions.LOAN_RETURN)) {
                    equipmentLoanOrReturnDocument.setLoanReturnDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
                }
                if (request.getParameter(CamsConstants.AssetActions.LOAN_TYPE).equals(CamsConstants.AssetActions.LOAN_RENEW)) {
                    equipmentLoanOrReturnDocument.setLoanDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
                    equipmentLoanOrReturnDocument.setExpectedReturnDate(null);
                }
                equipmentLoanOrReturnDocument.setAsset(newAsset);
            }
        }
        return newAsset;
    }

    private void populateEquipmentLoanOrReturnDocument(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument, Asset newAsset) {
        equipmentLoanOrReturnDocument.setLoanDate(newAsset.getLoanDate());
        equipmentLoanOrReturnDocument.setLoanReturnDate(newAsset.getLoanReturnDate());
        equipmentLoanOrReturnDocument.setExpectedReturnDate(newAsset.getExpectedReturnDate());
        equipmentLoanOrReturnDocument.setSignatureCode(newAsset.isSignatureCode());

        AssetLocation borrowerLocation = new AssetLocation();
        borrowerLocation.setCapitalAssetNumber(newAsset.getCapitalAssetNumber());
        borrowerLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER);
        borrowerLocation = (AssetLocation) SpringContext.getBean(BusinessObjectService.class).retrieve(borrowerLocation);
        if (borrowerLocation != null) {
            equipmentLoanOrReturnDocument.setBorrowerUniversalIdentifier(borrowerLocation.getAssetLocationContactIdentifier());
            equipmentLoanOrReturnDocument.setBorrowerAddress(borrowerLocation.getAssetLocationStreetAddress());
            equipmentLoanOrReturnDocument.setBorrowerCityName(borrowerLocation.getAssetLocationCityName());
            equipmentLoanOrReturnDocument.setBorrowerStateCode(borrowerLocation.getAssetLocationStateCode());
            equipmentLoanOrReturnDocument.setBorrowerZipCode(borrowerLocation.getAssetLocationZipCode());
            equipmentLoanOrReturnDocument.setBorrowerCountryCode(borrowerLocation.getAssetLocationCountryCode());
            equipmentLoanOrReturnDocument.setBorrowerPhoneNumber(borrowerLocation.getAssetLocationPhoneNumber());
        }

        AssetLocation storeAtLocation = new AssetLocation();
        storeAtLocation.setCapitalAssetNumber(newAsset.getCapitalAssetNumber());
        storeAtLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE);
        storeAtLocation = (AssetLocation) SpringContext.getBean(BusinessObjectService.class).retrieve(storeAtLocation);
        if (storeAtLocation != null) {
            equipmentLoanOrReturnDocument.setBorrowerStorageAddress(storeAtLocation.getAssetLocationStreetAddress());
            equipmentLoanOrReturnDocument.setBorrowerStorageCityName(storeAtLocation.getAssetLocationCityName());
            equipmentLoanOrReturnDocument.setBorrowerStorageStateCode(storeAtLocation.getAssetLocationStateCode());
            equipmentLoanOrReturnDocument.setBorrowerStorageZipCode(storeAtLocation.getAssetLocationZipCode());
            equipmentLoanOrReturnDocument.setBorrowerStorageCountryCode(storeAtLocation.getAssetLocationCountryCode());
            equipmentLoanOrReturnDocument.setBorrowerStoragePhoneNumber(storeAtLocation.getAssetLocationPhoneNumber());
        }
    }

}
