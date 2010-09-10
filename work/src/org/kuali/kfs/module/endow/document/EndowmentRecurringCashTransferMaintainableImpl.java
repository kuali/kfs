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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class EndowmentRecurringCashTransferMaintainableImpl extends FinancialSystemMaintainable {
    
    private static final String KEMID_TARGET = "kemidTarget";
    private static final String GL_TARGET = "glTarget";
    
    @Override
    public void addNewLineToCollection(String collectionName){
        String test = "";
        
        // kemidTarget, glTarget
        // ECT, EGLT
        // GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, newAccount.getAccountNumber());
        // TRANSACTION_TYPE
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) this.getBusinessObject();
        
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransfer.getTransactionType())){
            if (checkTransactionType(endowmentRecurringCashTransfer.getTransactionType(), collectionName)){
                super.addNewLineToCollection(collectionName);
            }
        } else {
            GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_REQUIRED, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE);
            GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
        }
    }

    private boolean checkTransactionType(String transactionType, String collectionName) {
        
        if (transactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
            if (!collectionName.equals(KEMID_TARGET)){
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                return false;
            } 
        } else {
            if (!collectionName.equals(GL_TARGET)){
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                return false;
            } 
        }
        return true;
    }
}