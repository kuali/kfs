/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;

public interface KemidBenefittingOrganizationDao {

    /**
     * Gets kemids by the given attribute value strings
     * 
     * @param attributeName
     * @param values
     * @return
     */
    public List<String> getKemidsByAttribute(String attributeName, List<String> values);
    
    /**
     * Gets the attributes values by the given value strings
     * 
     * @param attributeName
     * @param values
     * @return
     */
    public List<String> getAttributeValues(String attributeName, List<String> values);
    
    /**
     * Gets BenefittingOrganizations whose kemids exist in multiple
     * 
     * @param kemids
     * @return
     */
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids);
    
    /**
     * Gets KemidBenefittingOrganizations by kemids
     * 
     * @param kemids
     * @return
     */
    public List<KemidBenefittingOrganization> getBenefittingOrganizations(List<String> kemids); 
    
    /**
     * Get the kemids that are more than one
     * 
     * @param kemids
     * @return
     */
    public List<String> getIdsForMultipleBenefittingOrganizations(List<String> kemids);
    
    /**
     * Gets the campus codes, using the campus codes
     * 
     * @param campusCodes
     * @return
     */
    public List<String> getKemidsByCampusCode(List<String> campusCodes);
    
    /**
     * Gets the campus codes by the given given attribute values
     * 
     * @param attributeName
     * @param values
     * @return
     */
    public List<String> getCampusCodes(String attributeName, List<String> values);
    
    /**
     * Gets the campus code by chart code and organization code
     * 
     * @param charCode
     * @param organziationCode
     * @return
     */
    public String getCampusCode(String charCode, String organziationCode);
}
