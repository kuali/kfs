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

import org.apache.struts.action.ActionForm;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.web.struts.form.AssetPaymentForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class AssetPaymentAction extends KualiAccountingDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentAction.class);

    /**
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
    //TODO remove this method.
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm apForm = (AssetPaymentForm) form;
        String command = ((AssetPaymentForm) form).getCommand();
        String docID = ((AssetPaymentForm) form).getDocId();
        String capitalAssetNumber = ((AssetPaymentForm) form).getCapitalAssetNumber();
        LOG.info("***AssetPaymentAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID + " - Capital Asset Number:" + capitalAssetNumber);
        return super.execute(mapping, form, request, response);
    }*/
    
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

        //TODO change it because assetHeader table is needed anymore.        
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
