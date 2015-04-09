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
package org.kuali.kfs.module.cg.service;

import java.util.List;

/**
 * Service with methods related to the Contracts & Grants Billing (CGB) enhancement.
 */
public interface ContractsAndGrantsBillingService {

    /**
     * Return list of Agency maintainable sections related to the Contracts & Grants Billing (CGB) enhancement.
     * This sections will be ignored on the maintenance/inquiry screens if CGB is disabled.
     *
     * @return list of CGB Agency section ids
     *
     */
    public List<String> getAgencyContractsGrantsBillingSectionIds();

    /**
     * Return list of Award maintainable sections related to the Contracts & Grants Billing (CGB) enhancement.
     * This sections will be ignored on the maintenance/inquiry screens if CGB is disabled.
     *
     * @return list of CGB Award section ids
     *
     */
    public List<String> getAwardContractsGrantsBillingSectionIds();

}
