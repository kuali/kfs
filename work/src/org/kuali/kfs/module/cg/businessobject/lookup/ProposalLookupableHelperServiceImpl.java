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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsLookupService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Allows custom handling of Proposals within the lookup framework.
 */
public class ProposalLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected ContractsAndGrantsLookupService contractsAndGrantsLookupService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director object first
        if (contractsAndGrantsLookupService.setupSearchFields(fieldValues, CGPropertyConstants.LOOKUP_USER_ID_FIELD, CGPropertyConstants.PROPOSAL_LOOKUP_UNIVERSAL_USER_ID_FIELD)) {
            return super.getSearchResultsHelper(fieldValues, unbounded);
        }

        return Collections.EMPTY_LIST;
    }

    public ContractsAndGrantsLookupService getContractsAndGrantsLookupService() {
        return contractsAndGrantsLookupService;
    }

    public void setContractsAndGrantsLookupService(ContractsAndGrantsLookupService contractsAndGrantsLookupService) {
        this.contractsAndGrantsLookupService = contractsAndGrantsLookupService;
    }

}