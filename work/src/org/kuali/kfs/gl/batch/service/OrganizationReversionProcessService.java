/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;

public interface OrganizationReversionProcessService {
    /**
     * Organization Reversion Year End Process for the end of a fiscal year
     */
    public void organizationReversionProcessEndOfYear(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts);

    /**
     * Organization Reversion Year End Process for the beginning of a fiscal year
     */
    public void organizationReversionProcessBeginningOfYear(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts);

    /**
     * Generates reports for a run of the organization reversion process
     * 
     * @param outputGroup
     * @param jobParameters
     * @param organizationReversionCounts
     */
    public void generateOrganizationReversionProcessReports(OriginEntryGroup outputGroup, Map jobParameters, Map<String, Integer> organizationReversionCounts);

    /**
     * This method returns the parameters for this organization reversion job
     * 
     * @return a Map of standard parameters for the job
     */
    public Map getJobParameters();

    /**
     * creates an origin entry group for this run of the organization reversion process
     * 
     * @return a properly initialized origin entry group
     */
    public OriginEntryGroup createOrganizationReversionProcessOriginEntryGroup();
}
