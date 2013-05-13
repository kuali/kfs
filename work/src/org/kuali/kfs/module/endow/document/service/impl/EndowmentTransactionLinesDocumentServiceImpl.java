/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;

public class EndowmentTransactionLinesDocumentServiceImpl extends EndowmentTransactionDocumentServiceImpl implements EndowmentTransactionLinesDocumentService {

    private KEMIDService kemidService;
    private EndowmentTransactionCodeService endowmentTransactionCodeService;
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService#getCorpusIndicatorValueforAnEndowmentTransactionLine(java.lang.String, java.lang.String, java.lang.String)
     * 
     * Get the value of CorpusIndicator based on the following rule:
     * If the KEMID is a true endowment (the END_KEMID_T: TYP_CD has an END_TYP_T: PRIN_RESTR_CD where the END_TYP_RESTR_CD_T: PERM is equal to Yes,)
     * AND if the ETRAN code record has the END_ETRAN_CD_T: CORPUS_IND set to Yes, 
     * then transaction lines where the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P will affect the corpus value for the KEMID.  
     * Upon adding the transaction line, the END_TRAN_LN_T: CORPUS_IND for the transaction line will be set to Yes.
     */
    public boolean getCorpusIndicatorValueforAnEndowmentTransactionLine(String kemid, String etranCode, String ipIndicator) {
        boolean corpusIndicator = false;
        EndowmentTransactionCode theEtranCodeObj = null;
        if (ipIndicator.equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)){
            if (kemidService.isTrueEndowment(kemid)){
                theEtranCodeObj = endowmentTransactionCodeService.getByPrimaryKey(etranCode);
                if (theEtranCodeObj.isCorpusIndicator()){
                    corpusIndicator = true;
                }
            }
        }        
        return corpusIndicator;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService#canKEMIDHaveAPrincipalActivity(java.lang.String, java.lang.String)
     * 
     * IF the END _TRAN_LN_T: TRAN_IP_IND_CD for the transaction line is equal to P, then the KEMID must have a type code (END_KEMID_T: TYP_CD) 
     * that does not have the END_TYPE_T: TYP_PRIN_RESTR_CD equal to NA which implies that the KEMID cannot have any activity in Principal. 
     * This would guarantee that the KEMID has an active general ledger account with the Income/Principal indicator equal to "P".
     */
    public boolean canKEMIDHaveAPrincipalActivity (String kemid, String ipIndicator){
        boolean canHaveAPrincipalActivity = true;
        KEMID theKemidObj = null;
        if (ipIndicator.equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)){
            theKemidObj = kemidService.getByPrimaryKey(kemid);
            if (theKemidObj.getPrincipalRestrictionCode().equalsIgnoreCase(EndowConstants.TypeRestrictionPresetValueCodes.NOT_APPLICABLE_TYPE_RESTRICTION_CODE)){
                canHaveAPrincipalActivity = false;
            }
        }
        return canHaveAPrincipalActivity;
    }

    
    /**
     * Gets the kemidService.
     * 
     * @return kemidService
     */
    public KEMIDService getKemidService(){
        return kemidService;
    }
    
    /**
     * Sets the kemidService.
     * 
     * @param kemidService
     */
    public void setKemidService (KEMIDService kemidService){
        this.kemidService = kemidService; 
    }
    
    /**
     * Gets the endowmentTransactionCodeService.
     * 
     * @return endowmentTransactionCodeService
     */
    public EndowmentTransactionCodeService getEndowmentTransactionCodeService(){
        return endowmentTransactionCodeService;
    }
    
    /**
     * Sets the endowmentTransactionCodeService.
     * 
     * @param endowmentTransactionCodeService
     */
    public void setEndowmentTransactionCodeService (EndowmentTransactionCodeService endowmentTransactionCodeService){
        this.endowmentTransactionCodeService = endowmentTransactionCodeService; 
    }




}
