/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.service;

public interface OrganizationHierarchyService {
    /**
     * This method traverses the hierarchy to see if the organization represented by the potentialChildChartCode and potentialChildOrganizationCode 
     * reports to the organization represented by the potentialParentChartCode and potentialParentOrganizationCode
     * 
     * @param potentialChildChartCode
     * @param potentialChildOrganizationCode
     * @param potentialParentChartCode
     * @param potentialParentOrganizationCode
     * @return boolean indicating whether the organization represented by the first two parameters reports to one represented by the last two parameters
     */
    public boolean isParentOrganization(String potentialChildChartCode, String potentialChildOrganizationCode, String potentialParentChartCode, String potentialParentOrganizationCode);
}
