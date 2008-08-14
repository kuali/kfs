/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.document.authorization.FinancialSystemUserDocumentAuthorizer;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class FinancialSystemUserLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemUserLookupableHelperServiceImpl.class);
    
    protected FinancialSystemUserDocumentAuthorizer documentAuthorizer;
    protected boolean searchUsingOnlyPrimaryKeyValues = false;
    
    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     * 
     * @return true if bo has maint doc that allows new or copy actions
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        
        if ( getDocumentAuthorizer() != null && getDocumentAuthorizer().canEditUniversalUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser()) ) {
            return super.allowsMaintenanceNewOrCopyAction();
        }
        return false;
    }

    @Override
    public String getActionUrls(BusinessObject businessObject) {
        // allow edit if KFS data can be maintained
        if ( getDocumentAuthorizer() != null && getDocumentAuthorizer().canEditFinancialSystemUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser()) ) {
            return super.getActionUrls(businessObject);
        // allow edit if UU is editable
        } else if ( getDocumentAuthorizer() != null && getDocumentAuthorizer().canEditUniversalUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser()) ) {
            return super.getActionUrls(businessObject);
        }
        return "";
    }
    
    /**
     * 
     * This method does the actual search, with the parameters specified, and returns the result.
     * 
     * NOTE that it will not do any upper-casing based on the DD forceUppercase. That is handled through an external call to
     * LookupUtils.forceUppercase().
     * 
     * @param fieldValues A Map of the fieldNames and fieldValues to be searched on.
     * @param unbounded Whether the results should be bounded or not to a certain max size.
     * @return A List of search results.
     * 
     */
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields( getBusinessObjectClass(), fieldValues );

        searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
        List searchResults;
        if (getUniversalUserService().hasUniversalUserProperty(getBusinessObjectClass(), fieldValues)) {
            // TODO WARNING: this does not support nested joins, because i don't have a test case
            searchResults = (List) getUniversalUserService().findWithUniversalUserJoin(getBusinessObjectClass(), fieldValues, unbounded);
        }
        else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }
    
    /**
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return searchUsingOnlyPrimaryKeyValues;
    }

    public FinancialSystemUserDocumentAuthorizer getDocumentAuthorizer() {
        if ( documentAuthorizer == null ) {
            
            DocumentAuthorizer tempDocAuth = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(getMaintenanceDocumentDictionaryService().getDocumentTypeName(FinancialSystemUser.class));
            if ( tempDocAuth instanceof FinancialSystemUserDocumentAuthorizer ) {
                documentAuthorizer = (FinancialSystemUserDocumentAuthorizer)tempDocAuth;
            }
        }
        return documentAuthorizer;
    }
    
    
}
