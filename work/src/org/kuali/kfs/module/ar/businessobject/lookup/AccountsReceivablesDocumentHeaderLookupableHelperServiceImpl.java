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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

public class AccountsReceivablesDocumentHeaderLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivablesDocumentHeaderLookupableHelperServiceImpl.class);
    private DataDictionaryService dataDictionaryService;
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);

        try {
            for (Iterator iter = searchResults.iterator(); iter.hasNext();) {
                AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = (AccountsReceivableDocumentHeader) iter.next();
                Document document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(accountsReceivableDocumentHeader.getDocumentNumber());                
                
                String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                if (!documentTypeName.equals(dataDictionaryService.getDocumentTypeNameByClass(CustomerInvoiceDocument.class))) {
                    iter.remove();
                }
            }
        } catch (WorkflowException wfe) {
            
        }
        
        return searchResults;
    }
    /**
     * Gets dataDictionaryService
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getDataDictionaryService()
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }
    
    /**
     * sets dataDictionaryService sets dataDictionaryService
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setDataDictionaryService(org.kuali.rice.kns.service.DataDictionaryService)
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
