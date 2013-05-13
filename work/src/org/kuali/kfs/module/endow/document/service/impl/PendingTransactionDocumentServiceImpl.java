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

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.PendingTransactionDocumentEntry;
import org.kuali.kfs.module.endow.document.service.PendingTransactionDocumentService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PendingTransactionDocumentServiceImpl implements PendingTransactionDocumentService {
    
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
    
    /**
     * Returns all the pending transaction documents.
     * @return Collection of pending transaction document entries.
     */
    public Collection<PendingTransactionDocumentEntry> getPendingDocuments()
    {
        Collection<PendingTransactionDocumentEntry> pendingTransDocEnteries = 
            businessObjectService.findAll(PendingTransactionDocumentEntry.class);
                
        return (pendingTransDocEnteries == null ? new ArrayList<PendingTransactionDocumentEntry>() : pendingTransDocEnteries);
    }
    
    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) 
    {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) 
    {
        this.dataDictionaryService = dataDictionaryService;
    }
}
