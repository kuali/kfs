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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;

/**
 * This service helps in the creation of GLPE's for invoice related documents
 */
public interface CustomerInvoiceGLPEService {
    
    /**
     * This method creates the corresponding receivable GLPE's
     * 
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasOffset
     * @param amount
     */
    public void createReceivableGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount);

    /**
     * This method creates the income related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasOffset
     * @param amount
     */
    public void createIncomeGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount);

    /**
     * This method creates the sales tax GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasOffset
     * @param amount
     */
    public void createSalesTaxGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount);

    /**
     * This method creates the district tax GLPE's
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasOffset
     * @param amount
     */
    public void createDistrictTaxGLPEs(GeneralLedgerPendingEntrySource glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount);
}
