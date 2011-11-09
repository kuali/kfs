/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This interface defines what methods of data retrieval should be allowed for {@link org.kuali.kfs.coa.businessobject.Chart}
 */
public interface ChartDao {
    /**
     * This method retrieves all Chart objects in the system
     * 
     * @return all Chart objects
     */
    public Collection getAll();

    /**
     * This method retrieves the University's Chart object
     * 
     * @param organizationReportsToSelfParameterValue organization Reports To Self Parameter Value
     * @return University's chart object
     */
    public Chart getUniversityChart(String organizationReportsToSelfParameterValue);


    /**
     * This method retrieves a list of Chart objects that a specific User is responsible for
     * 
     * @param kualiUser
     * @return list of Chart objects that this user is responsible for
     */
    public List getChartsThatUserIsResponsibleFor(Person kualiUser);
}
