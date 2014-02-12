/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.rice.krad.bo.BusinessObject;

public class ContractsAndGrantsModuleRetrieveServiceImpl implements ContractsAndGrantsModuleRetrieveService {

    private AwardServiceImpl awardService;

    @Override
    public List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return new ArrayList<Award>(getAwardService().findMatching(fieldValues));
    }

    @Override
    public boolean isContractsGrantsBillingEnhancementsActive() {
        return false;
    }

    @Override
    public List<String> hasValidContractControlAccounts(Long proposalNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public AwardServiceImpl getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardServiceImpl awardService) {
        this.awardService = awardService;
    }
}
