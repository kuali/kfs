/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.AbstractKualiDecimal;
import org.kuali.rice.kns.util.GlobalVariables;

public class EndowmentTransactionLinesDocumentBaseRules extends EndowmentTransactionalDocumentBaseRule implements AddTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, DeleteTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule#processDeleteTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public boolean processDeleteTransactionLineRules(EndowmentTransactionLinesDocument EndowmentTransactionLinesDocument, EndowmentTransactionLine EndowmentTransactionLine) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLine, EndowmentTransactionLine line) 
    {
        boolean isValid = true; 
        isValid &= GlobalVariables.getMessageMap().hasErrors(); 
        
        if(!isValid)
        {
            //Validate Kemid
            if(!validateKemId(line))
                return false;
         
            //Active Kemid
            isValid &= isActiveKemId(line);
            
            //Validate no restriction transaction restriction
            isValid &= validateNoTransactionRestriction(line);
            
            //Validate Greater then Zero(thus positive) value
            isValid &= validateTransactionAmountGreaterThanZero(line);
            
            //Validate ETran code
            if(!validateEndowmentTransactionCode(line))
                return false;

            //Refresh all references for the given KemId
            //line.getKemidObj().refreshNonUpdateableReferences();

            //Validate ETran code as E or I
            isValid &= validateEndowmentTransactionTypeCode(line);
        }
        
        return isValid;
    }

    /**
     * This method validate the KemId code and tries to create a KEMID object from the code.
     * 
     * @param line
     * @return
     */
    private boolean validateKemId(EndowmentTransactionLine line)
    {
        boolean success = true;
        
        if( StringUtils.isEmpty(line.getKemid()) )
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_REQUIRED);
            success = false;
        }
        else 
        {
            KEMID kemId = (KEMID) SpringContext.getBean(KEMIDService.class).getByPrimaryKey(line.getKemid());
            line.setKemidObj(kemId);
            if( null == kemId )
            {
                putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INVALID);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method determines if the KEMID is active. 
     * 
     * @param line
     * @return
     */
    private boolean isActiveKemId(EndowmentTransactionLine line)
    {
        if(line.getKemidObj().isActive())
        {
            return true;
        }
        else
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INACTIVE);
            return false;
        }
    }
    
    /**
     * This method checks if the KEMID restriction code is "NTRAN"
     * 
     * @param line
     * @return
     */
    private boolean validateNoTransactionRestriction(EndowmentTransactionLine line)
    {
        if(line.getKemidObj().getTransactionRestrictionCode().equalsIgnoreCase(EndowConstants.TransactionRestrictionCode.TRAN_RESTR_CD_NTRAN))
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_NO_TRAN_CODE);
            return false;
        }
        else
        {
            return true;
        }
            
    }
    
    /**
     * This method checks is the Transaction amount entered is greater than Zero. 
     * 
     * @param line
     * @return
     */
    private boolean validateTransactionAmountGreaterThanZero(EndowmentTransactionLine line)
    {
        if(line.getTransactionAmount().isGreaterThan(AbstractKualiDecimal.ZERO))
            return true;
        else
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_ZERO);
            return false;
        }
            
    }
    
    /**
     * This method validate the ETRAN code and tries to create a EndowmentTransactionCode object from the code.
     * 
     * @param line
     * @return
     */
    private boolean validateEndowmentTransactionCode(EndowmentTransactionLine line)
    {
        boolean success = true;
        
        if( StringUtils.isEmpty(line.getEtranCode()) )
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_REQUIRED);
            success = false;
        }
        else 
        {
            EndowmentTransactionCode etran = (EndowmentTransactionCode) SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(line.getEtranCode());
            line.setEtranCodeObj(etran);
            if( null == etran )
            {
                putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_INVALID);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method validate the Security code and tries to create a Security object from the code. 
     * 
     * @param tranSecurity
     * @return
     */
    private boolean validateSecurityCode(EndowmentTransactionSecurity tranSecurity)
    {
        boolean success = true;
        
        if( StringUtils.isEmpty(tranSecurity.getSecurityID()) )
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_REQUIRED);
            success = false;
        }
        else 
        {
            Security security= (Security) SpringContext.getBean(SecurityService.class).getByPrimaryKey(tranSecurity.getSecurityID());
            tranSecurity.setSecurity(security);
            if( null == security )
            {
                putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_INVALID);
                success = false;
            }
        }
        return success;
    }

    /**
     * This method checks if the ETRAN code has a type code of E or I. 
     * 
     * @param line
     * @return
     */
    private boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLine line)
    {
        if(line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE) )
            return true;
        else
        {
            putFieldError(KFSConstants.ITEM_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY);
            return false;
        }
    }
    
    private boolean templateMethod(EndowmentTransactionLine line)
    {
        boolean success = true;
        
        return success;
    }
    
}
