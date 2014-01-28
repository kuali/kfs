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
package org.kuali.kfs.integration.cg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * No-Op for ContractsAndGrantsModuleRetrieveService
 */
public class ContractsAndGrantsModuleRetrieveServiceNoOp implements ContractsAndGrantsModuleRetrieveService {

    private Logger LOG = Logger.getLogger(getClass());


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList();
    }

    @Override
    public boolean isContractsGrantsBillingEnhancementsActive() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public List<String> hasValidContractControlAccounts(Long proposalNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<String>();
    }
}
