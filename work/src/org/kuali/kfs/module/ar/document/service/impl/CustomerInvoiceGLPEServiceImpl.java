/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CustomerInvoiceGLPEServiceImpl implements CustomerInvoiceGLPEService {

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    
    /**
     * This method creates and adds generic invoice related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasClaimOnCashOffset
     * @param amount
     */
    public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount){
        
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(glpeSource, glpeSourceDetail, sequenceHelper, explicitEntry);
        explicitEntry.setTransactionLedgerEntryAmount(amount.abs());
        explicitEntry.setTransactionDebitCreditCode(isDebit? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        
        //add explicit entry
        glpeSource.addPendingEntry(explicitEntry);
        
        //add claim on cash offset entry
        if( hasClaimOnCashOffset ){
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
            generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(glpeSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
            glpeSource.addPendingEntry(offsetEntry);
        }
    }
    
    /**
     * This method creates and adds generic invoice related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasReceivableClaimOnCashOffset
     * @param writeoffTaxGenerationMethodDisallowFlag
     * @param amount
     */
    public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasReceivableClaimOnCashOffset, boolean writeoffTaxGenerationMethodDisallowFlag, KualiDecimal amount){
        
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(glpeSource, glpeSourceDetail, sequenceHelper, explicitEntry);
        explicitEntry.setTransactionLedgerEntryAmount(amount.abs());
        explicitEntry.setTransactionDebitCreditCode(isDebit? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        
        boolean overrideFinancialObjectCodeFlag = isDebit && writeoffTaxGenerationMethodDisallowFlag  && !hasReceivableClaimOnCashOffset;
        
        if (!overrideFinancialObjectCodeFlag)
            //add explicit entry
            glpeSource.addPendingEntry(explicitEntry);
        
        // do not add claim on cash offset entry if GLPLE_RECEIVABLE_OFFSET_METHOD = 3 && GLPLE_WRITEOFF_TAX_GENERATION_METHOD = D
        if (hasReceivableClaimOnCashOffset && writeoffTaxGenerationMethodDisallowFlag )
            return;
        
        //add claim on cash offset entry
        if( hasReceivableClaimOnCashOffset || writeoffTaxGenerationMethodDisallowFlag ){
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
            generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(glpeSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
            if (overrideFinancialObjectCodeFlag) {
                explicitEntry.setFinancialObjectCode(offsetEntry.getFinancialObjectCode());
                glpeSource.addPendingEntry(explicitEntry);
            }
            // TODO
            // override (here or above???) account and object when hasReceivableClaimOnCashOffset = true and writeoffTaxGenerationMethodDisallowFlag = true
            glpeSource.addPendingEntry(offsetEntry);
        }
    }
       
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }


    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }


    
}
