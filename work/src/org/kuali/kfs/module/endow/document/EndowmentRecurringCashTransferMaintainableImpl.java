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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class EndowmentRecurringCashTransferMaintainableImpl extends FinancialSystemMaintainable {
    
    private static final String KEMID_TARGET = "kemidTarget";
    private static final String GL_TARGET = "glTarget";
    
    @Override
    public void addNewLineToCollection(String collectionName){
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) this.getBusinessObject();
        
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransfer.getTransactionType())){
            if (checkTransactionTypeAndSetSequenceNumber(endowmentRecurringCashTransfer.getTransactionType(), collectionName, endowmentRecurringCashTransfer)){
                super.addNewLineToCollection(collectionName);
            }
        } else {
            GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_REQUIRED, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE);
            GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
        }
    }

    private boolean checkTransactionTypeAndSetSequenceNumber(String transactionType, String collectionName, EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        
        if (transactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
            if (!collectionName.equals(KEMID_TARGET)){
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                return false;
            } 
            EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget = (EndowmentRecurringCashTransferKEMIDTarget) newCollectionLines.get(collectionName);
            endowmentRecurringCashTransferKEMIDTarget.setTargetSequenceNumber(endowmentRecurringCashTransfer.incrementTargetKemidNextSeqNumber().toString());
        } else {
            if (!collectionName.equals(GL_TARGET)){
                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE);
                return false;
            } 
            
            EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget = (EndowmentRecurringCashTransferGLTarget) newCollectionLines.get(collectionName);
            endowmentRecurringCashTransferGLTarget.setTargetSequenceNumber(endowmentRecurringCashTransfer.incrementTargetGlNextSeqNumber().toString());

        }
        return true;
    }
}