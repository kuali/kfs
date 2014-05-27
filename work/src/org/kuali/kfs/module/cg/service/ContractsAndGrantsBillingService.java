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
