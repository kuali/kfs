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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.kim.api.identity.Person;

/**
 * No-op for ContractsAndGrantsModuleService
 */
public class ContractsAndGrantsModuleServiceNoOp implements ContractsAndGrantsModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    public List<Integer> getAllAccountReponsiblityIds() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<Integer>(0);
    }

    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    public Person getProjectDirectorForAccount(Account account) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    public boolean hasValidAccountReponsiblityIdIfNotNull(Account account) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, List<String> federalAgencyTypeCodes) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    // @Override
    public List<String> getParentUnits(String unitNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }


}
