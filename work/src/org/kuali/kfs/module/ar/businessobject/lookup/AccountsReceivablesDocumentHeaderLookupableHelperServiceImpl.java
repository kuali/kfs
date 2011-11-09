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
