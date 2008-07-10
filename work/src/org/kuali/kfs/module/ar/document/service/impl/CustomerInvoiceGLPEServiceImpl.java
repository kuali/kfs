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
package org.kuali.kfs.module.ar.document.service.impl;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;

public class CustomerInvoiceGLPEServiceImpl implements CustomerInvoiceGLPEService {

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService#createIncomeGLPEs(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, boolean, boolean, org.kuali.core.util.KualiDecimal)
     */
    public void createIncomeGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount) {
        createAndAddGenericInvoiceRelatedGLPEs(glpeSource, glpeSourceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, amount);
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService#createReceivableGLPEs(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, boolean, boolean, org.kuali.core.util.KualiDecimal)
     */
    public void createReceivableGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount) {
        createAndAddGenericInvoiceRelatedGLPEs(glpeSource, glpeSourceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, amount);   
    }
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService#createDistrictTaxGLPEs(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, boolean, boolean, org.kuali.core.util.KualiDecimal)
     */
    public void createDistrictTaxGLPEs(GeneralLedgerPendingEntrySource glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount) {
        // TODO Auto-generated method stub
    }
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService#createSalesTaxGLPEs(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, boolean, boolean, org.kuali.core.util.KualiDecimal)
     */
    public void createSalesTaxGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount) {
        // TODO Auto-generated method stub
    }
    
    /**
     * This method creates and adds generic invoice related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasClaimOnCashOffset
     * @param amount
     */
    protected void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount){
        
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(glpeSource, glpeSourceDetail, sequenceHelper, explicitEntry);
        explicitEntry.setTransactionLedgerEntryAmount(amount.abs());
        explicitEntry.setTransactionDebitCreditCode(isDebit? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        
        //add explicit entry
        glpeSource.addPendingEntry(explicitEntry);
        sequenceHelper.increment();
        
        
        //add claim on cash offset entry
        if( hasClaimOnCashOffset ){
            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
            generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(glpeSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
            glpeSource.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
        }
    }
    
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }


    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
    
}
