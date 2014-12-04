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
