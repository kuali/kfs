/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborObject;

/**
 * This is the data access object for Labor Object
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborObject
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
