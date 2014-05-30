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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class AccountsReceivablesDocumentHeaderLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        // we only want invoices, so let's add that to the fieldValues
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.INV_DOCUMENT_TYPE);

        List<AccountsReceivableDocumentHeader> searchResults = (List<AccountsReceivableDocumentHeader>)getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);

        return searchResults;
    }
}
