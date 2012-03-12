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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.batch.service.impl.LaborPosterServiceImpl;
import org.kuali.kfs.module.ld.service.LaborTransactionDescriptionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;

public class LaborTransactionDescriptionServiceImpl implements LaborTransactionDescriptionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPosterServiceImpl.class);

    private Map<String, String> transactionDescriptionMap;
    
    /**
     * @see org.kuali.kfs.module.ld.service.LaborTransactionDescriptionService#getTransactionDescription(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public String getTransactionDescription(Transaction transaction) {
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();
        String description = this.getTransactionDescription(documentTypeCode);
        description = StringUtils.isNotEmpty(description) ? description : transaction.getTransactionLedgerEntryDescription();

        // make sure the length of the description cannot excess the specified maximum
        int transactionDescriptionMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(transaction.getClass(), KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        if (StringUtils.isNotEmpty(description) && description.length() > transactionDescriptionMaxLength) {
            description = StringUtils.left(description, transactionDescriptionMaxLength);
        }

        return description;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborTransactionDescriptionService#getTransactionDescriptionByDocumentType(java.lang.String)
     */
    public String getTransactionDescription(String descriptionKey) {       
        if(transactionDescriptionMap.containsKey(descriptionKey)) {
            return transactionDescriptionMap.get(descriptionKey);
        }
        else {        
            LOG.warn("Cannot find a description for the given key: " + descriptionKey);
        }
        
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Sets the transactionDescriptionMap attribute value.
     * @param transactionDescriptionMap The transactionDescriptionMap to set.
     */
    public void setTransactionDescriptionMap(Map<String, String> transactionDescriptionMap) {
        this.transactionDescriptionMap = transactionDescriptionMap;
    }
}
