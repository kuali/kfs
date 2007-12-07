/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.module.labor.bo.LaborObject;

/**
 * This is the data access object for Labor Object
 * 
 * @see org.kuali.module.labor.bo.LaborObject
 */
public interface LaborObjectDao {
    
    /**
     * find all labor object codes in the given position groups
     * @param fieldValues the given search search criteria
     * @param positionGroupCodes the given position group codes
     * @return all labor object codes in the given position groups
     */
    public Collection<LaborObject> findAllLaborObjectInPositionGroups(Map<String, Object> fieldValues, List<String> positionGroupCodes);
}
