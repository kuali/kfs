/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPES;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.vendor.util.VendorUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides services to support the creation of a Credit Memo Document.
 */
@Transactional
public class CreditMemoServiceImpl implements CreditMemoService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    private CreditMemoDao creditMemoDao;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#creditMemoDuplicateMessages(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public String creditMemoDuplicateMessages(CreditMemoDocument cmDocument) {
        String duplicateMessage = null;

        String vendorNumber = cmDocument.getVendorNumber();
        if (StringUtils.isEmpty(vendorNumber)) {
            if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PREQ)) {
                PaymentRequestDocument paymentRequestDocument = SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
                vendorNumber = paymentRequestDocument.getVendorNumber();
            }
            else {
                PurchaseOrderDocument purchaseOrderDocument = (SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier()));
                vendorNumber = purchaseOrderDocument.getVendorNumber();
            }
        }

        if (StringUtils.isNotEmpty(vendorNumber)) {
            // check for existence of another credit memo with the same vendor and vendor credit memo number
            if (creditMemoDao.duplicateExists(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber), cmDocument.getCreditMemoNumber())) {
                duplicateMessage = kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER);
            }

            // check for existence of another credit memo with the same vendor and credit memo date
            if (creditMemoDao.duplicateExists(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber), cmDocument.getCreditMemoDate(), cmDocument.getCreditMemoAmount())) {
                duplicateMessage = kualiConfigurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER_DATE_AMOUNT);
            }
        }

        return duplicateMessage;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getPOInvoicedItems(org.kuali.module.purap.document.PurchaseOrderDocument)
     */
    public List<PurchaseOrderItem> getPOInvoicedItems(PurchaseOrderDocument poDocument) {
        List<PurchaseOrderItem> invoicedItems = new ArrayList<PurchaseOrderItem>();

        for (Iterator iter = poDocument.getItems().iterator(); iter.hasNext();) {
            PurchaseOrderItem poItem = (PurchaseOrderItem) iter.next();

            // only items of type above the line can be considered for being invoiced
            if (!poItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                continue;
            }
            invoicedItems.add(poItem);

            // if (poItem.getItemInvoicedTotalQuantity() != null &&
            // poItem.getItemInvoicedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {
            // invoicedItems.add(poItem);
            // }
            // else {
            // BigDecimal unitPrice = (poItem.getItemUnitPrice() == null ? new BigDecimal(0) : poItem.getItemUnitPrice());
            // if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SERVICE_CODE.equals(poItem.getItemType()) && (unitPrice.doubleValue() >
            // poItem.getItemOutstandingEncumbranceAmount().doubleValue())) {
            // invoicedItems.add(poItem);
            // }
            // }
        }

        return invoicedItems;
    }


    /**
     * @see org.kuali.module.purap.service.CreditMemoService#calculateCreditMemo(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void calculateCreditMemo(CreditMemoDocument cmDocument) {
        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {
            // update unit price for service items
            if (item.getItemType().isItemTypeAboveTheLineIndicator() && !item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.setItemUnitPrice(new BigDecimal(item.getExtendedPrice().toString()));
            }
            // make restocking fee is negative
            else if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                if (item.getItemUnitPrice() != null) {
                    item.setItemUnitPrice(item.getItemUnitPrice().abs().negate());
                }
            }
        }
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#save(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void save(CreditMemoDocument cmDocument) {
        cmDocument.prepareForSave();
        creditMemoDao.save(cmDocument);
    }

    /**
     * Sets the creditMemoDao attribute value.
     * 
     * @param creditMemoDao The creditMemoDao to set.
     */
    public void setCreditMemoDao(CreditMemoDao creditMemoDao) {
        this.creditMemoDao = creditMemoDao;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
