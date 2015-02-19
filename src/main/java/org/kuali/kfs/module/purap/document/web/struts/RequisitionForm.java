/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.web.struts;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItemCapitalAsset;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action Form for Requisition document.
 */
public class RequisitionForm extends PurchasingFormBase {

    protected String shopUrl;
    
    /**
     * Constructs a RequisitionForm instance and sets up the appropriately casted document.
     */
    public RequisitionForm() {
        super();
    }

    /**
     * Gets the documentType attribute.
     * 
     * @return Returns the documentType
     */
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "REQS";
    }
    
    public RequisitionDocument getRequisitionDocument() {
        return (RequisitionDocument) getDocument();
    }

    public void setRequisitionDocument(RequisitionDocument requisitionDocument) {
        setDocument(requisitionDocument);
    }
    
    /**
    * KRAD Conversion: Performs customization of header fields.
    * 
    * Use of data dictionary for bo RequisitionDocument.
    */
    @Override    
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        
        if (ObjectUtils.isNotNull(this.getRequisitionDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.purapDocumentIdentifier", ((RequisitionDocument) this.getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.purapDocumentIdentifier", PurapConstants.PURAP_APPLICATION_DOCUMENT_ID_NOT_AVAILABLE));
        }
        
        String applicationDocumentStatus = PurapConstants.PURAP_APPLICATION_DOCUMENT_STATUS_NOT_AVAILABLE;
        
        if (ObjectUtils.isNotNull(this.getRequisitionDocument().getApplicationDocumentStatus())) {
            applicationDocumentStatus = workflowDocument.getApplicationDocumentStatus();
        }
        
        getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.applicationDocumentStatus", applicationDocumentStatus));
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && 
           ("displayB2BRequisition".equals(methodToCallParameterValue))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public Class getCapitalAssetLocationClass() {
        return RequisitionCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return RequisitionItemCapitalAsset.class;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new RequisitionItem();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public RequisitionAccount setupNewPurchasingAccountingLine() {
        return new RequisitionAccount();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewAccountDistributionAccountingLine()
     */
    @Override
    public RequisitionAccount setupNewAccountDistributionAccountingLine() {
        RequisitionAccount account = setupNewPurchasingAccountingLine();
        account.setAccountLinePercent(new BigDecimal(100));
        return account;
    }

    @Override
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        return new RequisitionCapitalAssetLocation();
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }
    
    /**
    * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#getExtraButtons()
    * 
    * KRAD Conversion: Performs customization of extra buttons.
    * 
    * No data dictionary is involved.
    */
    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        for (int i = 0; i < extraButtons.size(); i++) {
            ExtraButton extraButton = extraButtons.get(i);
            if ("methodToCall.calculate".equalsIgnoreCase(extraButton.getExtraButtonProperty()) ){ 
                if(canUserCalculate() == false){
                    extraButtons.remove(i);
                    return extraButtons;
                }
            }
        }
        return extraButtons;
    }

    @Override
    public boolean canUserCalculate(){        
        return documentActions != null && documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT) &&        
        !getRequisitionDocument().isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ORG_REVIEW);
    }    
}
