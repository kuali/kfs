/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
