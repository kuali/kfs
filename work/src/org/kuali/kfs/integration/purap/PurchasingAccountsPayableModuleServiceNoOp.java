/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration.purap;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class PurchasingAccountsPayableModuleServiceNoOp implements PurchasingAccountsPayableModuleService {

    private Logger LOG = Logger.getLogger(getClass()); 

    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, String authorId, String noteText) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public List<PurchasingAccountsPayableSensitiveData> getAllSensitiveDatas() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    public String getB2BUrlString() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return "";
    }

    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return "";
    }

    public PurchasingAccountsPayableSensitiveData getSensitiveDataByCode(String sensitiveDataCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    public void handlePurchasingBatchCancels(String documentNumber, String financialSystemDocumentTypeCode, boolean primaryCancel, boolean disbursedPayment) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public void handlePurchasingBatchPaids(String documentNumber, String financialSystemDocumentTypeCode, Date processDate) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public boolean isPurchasingBatchDocument(String financialSystemDocumentTypeCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

}
