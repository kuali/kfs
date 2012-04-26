/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableDocument extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer purapDocumentIdentifier;
    private Integer purchaseOrderIdentifier;
    private String documentTypeCode;
    private String activityStatusCode;

    // References
    private DocumentTypeEBO financialSystemDocumentTypeCode;
    private FinancialSystemDocumentHeader documentHeader;
    private List<PurchasingAccountsPayableItemAsset> purchasingAccountsPayableItemAssets;

    // non-persistent
    private boolean active;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;
    private String statusDescription;
    private String capitalAssetSystemTypeCodeFromPurAp;

    public PurchasingAccountsPayableDocument() {
        this.purchasingAccountsPayableItemAssets = new ArrayList<PurchasingAccountsPayableItemAsset>();
    }


    /**
     * Gets the capitalAssetSystemTypeCodeFromPurAp attribute.
     * @return Returns the capitalAssetSystemTypeCodeFromPurAp.
     */
    public String getCapitalAssetSystemTypeCodeFromPurAp() {
        return capitalAssetSystemTypeCodeFromPurAp;
    }


    /**
     * Sets the capitalAssetSystemTypeCodeFromPurAp attribute value.
     * @param capitalAssetSystemTypeCodeFromPurAp The capitalAssetSystemTypeCodeFromPurAp to set.
     */
    public void setCapitalAssetSystemTypeCodeFromPurAp(String capitalAssetSystemTypeCodeFromPurAp) {
        this.capitalAssetSystemTypeCodeFromPurAp = capitalAssetSystemTypeCodeFromPurAp;
    }


    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the purapDocumentIdentifier attribute.
     *
     * @return Returns the purapDocumentIdentifier.
     */
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }


    /**
     * Sets the purapDocumentIdentifier attribute value.
     *
     * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
     */
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }


    /**
     * Gets the purchaseOrderIdentifier attribute.
     *
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }


    /**
     * Sets the purchaseOrderIdentifier attribute value.
     *
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }


    /**
     * Gets the documentTypeCode attribute.
     *
     * @return Returns the documentTypeCode.
     */
    public String getDocumentTypeCode() {
        return documentTypeCode;
    }


    /**
     * Sets the documentTypeCode attribute value.
     *
     * @param documentTypeCode The documentTypeCode to set.
     */
    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return CabConstants.ActivityStatusCode.NEW.equalsIgnoreCase(this.getActivityStatusCode()) || CabConstants.ActivityStatusCode.MODIFIED.equalsIgnoreCase(this.getActivityStatusCode());
    }


    /**
     * Gets the activityStatusCode attribute.
     *
     * @return Returns the activityStatusCode.
     */
    public String getActivityStatusCode() {
        return activityStatusCode;
    }


    /**
     * Sets the activityStatusCode attribute value.
     *
     * @param activityStatusCode The activityStatusCode to set.
     */
    public void setActivityStatusCode(String activityStatusCode) {
        this.activityStatusCode = activityStatusCode;
    }


    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     *
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialSystemDocumentTypeCode.getName(), documentTypeCode) ) {
            financialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(documentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeCode);
                if ( docType != null ) {
                    financialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

    /**
     * Gets the documentHeader attribute.
     *
     * @return Returns the documentHeader.
     */
    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }


    /**
     * Sets the documentHeader attribute value.
     *
     * @param documentHeader The documentHeader to set.
     */
    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }


    /**
     * Gets the purchasingAccountsPayableItemAssets attribute.
     *
     * @return Returns the purchasingAccountsPayableItemAssets.
     */
    public List<PurchasingAccountsPayableItemAsset> getPurchasingAccountsPayableItemAssets() {
        return purchasingAccountsPayableItemAssets;
    }


    /**
     * Sets the purchasingAccountsPayableItemAssets attribute value.
     *
     * @param purchasingAccountsPayableItemAssets The purchasingAccountsPayableItemAssets to set.
     */
    public void setPurchasingAccountsPayableItemAssets(List<PurchasingAccountsPayableItemAsset> purchasingAccountsPayableItemAssets) {
        this.purchasingAccountsPayableItemAssets = purchasingAccountsPayableItemAssets;
    }


    /**
     * Gets the purApContactEmailAddress attribute.
     *
     * @return Returns the purApContactEmailAddress.
     */
    public String getPurApContactEmailAddress() {
        return purApContactEmailAddress;
    }


    /**
     * Sets the purApContactEmailAddress attribute value.
     *
     * @param purApContactEmailAddress The purApContactEmailAddress to set.
     */
    public void setPurApContactEmailAddress(String purApContactEmailAddress) {
        this.purApContactEmailAddress = purApContactEmailAddress;
    }


    /**
     * Gets the purApContactPhoneNumber attribute.
     *
     * @return Returns the purApContactPhoneNumber.
     */
    public String getPurApContactPhoneNumber() {
        return purApContactPhoneNumber;
    }


    /**
     * Sets the purApContactPhoneNumber attribute value.
     *
     * @param purApContactPhoneNumber The purApContactPhoneNumber to set.
     */
    public void setPurApContactPhoneNumber(String purApContactPhoneNumber) {
        this.purApContactPhoneNumber = purApContactPhoneNumber;
    }


    /**
     * Gets the statusDescription attribute.
     *
     * @return Returns the statusDescription.
     */
    public String getStatusDescription() {
        String statusCode;

        if (StringUtils.isNotBlank(this.statusDescription)) {
            return this.statusDescription;
        }
        else {
            Map objectKeys = new HashMap();
            objectKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURAP_DOCUMENT_IDENTIFIER, this.getPurapDocumentIdentifier());

            if (CabConstants.PREQ.equals(this.documentTypeCode)) {

                PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentRequestDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(paymentRequestDocument)) {
                    statusDescription = paymentRequestDocument.getApplicationDocumentStatus();
                }
            }
            else {
                VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(VendorCreditMemoDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(vendorCreditMemoDocument)) {
                    statusDescription = vendorCreditMemoDocument.getApplicationDocumentStatus();
                }
            }
        }

        return statusDescription;
    }


    /**
     * Sets the statusDescription attribute value.
     *
     * @param statusDescription The statusDescription to set.
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }


    /**
     * Need to override this method, so we can save item assets, the framework can delete the allocated item assets.
     *
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */

    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = new ArrayList<List>();

        managedLists.add(getPurchasingAccountsPayableItemAssets());
        return managedLists;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public PurchasingAccountsPayableItemAsset getPurchasingAccountsPayableItemAsset(int index) {
        int size = getPurchasingAccountsPayableItemAssets().size();
        while (size <= index || getPurchasingAccountsPayableItemAssets().get(index) == null) {
            getPurchasingAccountsPayableItemAssets().add(size++, new PurchasingAccountsPayableItemAsset());
        }
        return getPurchasingAccountsPayableItemAssets().get(index);

    }

}
