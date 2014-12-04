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
