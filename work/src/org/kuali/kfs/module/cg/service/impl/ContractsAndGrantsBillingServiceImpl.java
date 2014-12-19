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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsBillingService;

/**
 * Service with methods related to the Contracts & Grants Billing (CGB) enhancement.
 */
public class ContractsAndGrantsBillingServiceImpl implements ContractsAndGrantsBillingService {

    @Override
    public List<String> getAgencyContractsGrantsBillingSectionIds() {
        List<String> contractsGrantsSectionIds = new ArrayList<String>();

        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AGENCY_ADDRESS_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AGENCY_ADDRESSES_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AGENCY_COLLECTIONS_MAINTENANCE_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AGENCY_CONTRACTS_AND_GRANTS_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AGENCY_CUSTOMER_SECTION_ID);

        return contractsGrantsSectionIds;
    }

    @Override
    public List<String> getAwardContractsGrantsBillingSectionIds() {
        List<String> contractsGrantsSectionIds = new ArrayList<String>();

        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AWARD_FUND_MANAGERS_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AWARD_INVOICING_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AWARD_MILESTONE_SCHEDULE_SECTION_ID);
        contractsGrantsSectionIds.add(CGPropertyConstants.SectionId.AWARD_PREDETERMINED_BILLING_SCHEDULE_SECTION_ID);

        return contractsGrantsSectionIds;
    }

}
