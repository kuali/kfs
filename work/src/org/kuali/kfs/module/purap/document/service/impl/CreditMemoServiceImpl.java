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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.dao.CreditMemoDao;
import org.kuali.module.purap.document.CreditMemoDocument;
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
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemosByPurchaseOrder(java.lang.Integer)
     */
    public List<CreditMemoDocument> getCreditMemosByPurchaseOrder(Integer purchaseOrderIdentifier) {
        return getCreditMemosByPurchaseOrder(purchaseOrderIdentifier, null);
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemosByPurchaseOrder(java.lang.Integer, java.lang.Integer)
     */
    public List<CreditMemoDocument> getCreditMemosByPurchaseOrder(Integer purchaseOrderIdentifier, Integer returnListMax) {
        List<CreditMemoDocument> creditMemos = new ArrayList<CreditMemoDocument>();
        if (returnListMax == null) {
            creditMemos = creditMemoDao.getCreditMemosByPOId(purchaseOrderIdentifier);
        }
        else {
            creditMemos = creditMemoDao.getCreditMemosByPOId(purchaseOrderIdentifier, returnListMax);
        }

        return creditMemos;
    }

    /**
     * @see org.kuali.module.purap.service.CreditMemoService#getCreditMemosByPurchaseOrderAndStatus(java.lang.Integer,
     *      java.util.Collection)
     */
    public List<CreditMemoDocument> getCreditMemosByPurchaseOrderAndStatus(Integer purchaseOrderIdentifier, Collection<String> statusCodes) {
        return creditMemoDao.getAllCMsByPOIdAndStatus(purchaseOrderIdentifier, statusCodes);
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
