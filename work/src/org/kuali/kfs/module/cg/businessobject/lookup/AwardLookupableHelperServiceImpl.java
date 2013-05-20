/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Allows custom handling of Awards within the lookup framework.
 */
public class AwardLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final String LOOKUP_USER_ID_FIELD = "lookupPerson.principalName";
    private static final String LOOKUP_UNIVERSAL_USER_ID_FIELD = "awardProjectDirectors.principalId";
    private static final String LOOKUP_FUND_MGR_USER_ID_FIELD = "lookupFundMgrPerson.principalName";
    private static final String LOOKUP_FUND_MGR_UNIVERSAL_USER_ID_FIELD = "awardFundManagers.principalId";

    private PersonService personService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director and fund manager objects first
        if (!StringUtils.isBlank(fieldValues.get(LOOKUP_USER_ID_FIELD))) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(fieldValues.get(LOOKUP_USER_ID_FIELD));

            // if no project directors match, we can return an empty list right now
            if (principal == null) {
                return Collections.EMPTY_LIST;
            }

            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(LOOKUP_UNIVERSAL_USER_ID_FIELD, principal.getPrincipalId());
            fieldValues.remove(LOOKUP_USER_ID_FIELD);
        }

        if (!StringUtils.isBlank(fieldValues.get(LOOKUP_FUND_MGR_USER_ID_FIELD))) {
            Person person = getPersonService().getPersonByPrincipalName(fieldValues.get(LOOKUP_FUND_MGR_USER_ID_FIELD));

            // if no fund managers match, we can return an empty list right now
            if (person == null) {
                return Collections.EMPTY_LIST;
            }

            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(LOOKUP_FUND_MGR_UNIVERSAL_USER_ID_FIELD, person.getPrincipalId());
            fieldValues.remove(LOOKUP_FUND_MGR_USER_ID_FIELD);
        }

        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        if (allowsMaintenanceNewOrCopyAction()) {
            anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }

        AnchorHtmlData invoiceUrl = getInvoicesLookupUrl(businessObject);
        anchorHtmlDataList.add(invoiceUrl);

        return anchorHtmlDataList;
    }

    /**
     * This method adds a link to the look up FOR the invoices associated with a given Award.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getInvoicesLookupUrl(BusinessObject bo) {
        Award award = (Award) bo;
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "false");
        params.put("docTypeFullName", "CGIN");
        params.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber().toString());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DocSearchCriteriaDTO.class.getName());
        String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);
        return new AnchorHtmlData(url, KFSConstants.SEARCH_METHOD, "View Invoices");
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if (personService == null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

    /**
     * This is a intermediate method to call the getSearchResultsHelper() as its protected.
     * 
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public List<? extends BusinessObject> callGetSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return getSearchResultsHelper(fieldValues, unbounded);
    }

}
