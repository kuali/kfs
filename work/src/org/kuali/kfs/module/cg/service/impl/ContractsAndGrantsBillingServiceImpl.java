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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsBillingService;

/**
 * Service with methods related to the Contracts & Grants Billing (CGB) enhancement.
 */
public class ContractsAndGrantsBillingServiceImpl implements ContractsAndGrantsBillingService {

    @Override
    public List<String> getAgencyContractsGrantsBillingSectionIds() {
        List<String> contractsGrantsSectionIds = new ArrayList<String>();

        contractsGrantsSectionIds.add(CGConstants.SectionId.AGENCY_ADDRESSES_SECTION_ID);
        contractsGrantsSectionIds.add(CGConstants.SectionId.AGENCY_COLLECTIONS_MAINTENANCE_SECTION_ID);
        contractsGrantsSectionIds.add(CGConstants.SectionId.AGENCY_CONTRACTS_AND_GRANTS_SECTION_ID);
        contractsGrantsSectionIds.add(CGConstants.SectionId.AGENCY_CUSTOMER_SECTION_ID);

        return contractsGrantsSectionIds;
    }

    @Override
    public List<String> getAwardContractsGrantsBillingSectionIds() {
        List<String> contractsGrantsSectionIds = new ArrayList<String>();

        contractsGrantsSectionIds.add(CGConstants.SectionId.AWARD_FUND_MANAGERS_SECTION_ID);
        contractsGrantsSectionIds.add(CGConstants.SectionId.AWARD_INVOICE_ACCOUNTS_SECTION_ID);
        contractsGrantsSectionIds.add(CGConstants.SectionId.AWARD_INVOICING_SECTION_ID);

        return contractsGrantsSectionIds;
    }

}
