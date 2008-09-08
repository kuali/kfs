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
package org.kuali.kfs.module.purap.document.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.bo.Parameter;
import org.springframework.dao.DataAccessException;

/**
 * This interface defines the Parameter DAO...
 */

public interface ParameterDao {
    
    /**
     *  Returns a List of Parameters that match the given criteria in the 
     *  fieldValues Map. The criteria also include the "like" criteria 
     *  (users may attempt to pass in a criteria such as : 
     *  parameterName LIKE 'CHARTS_REQUIRING_%_REQUISITION' and this method
     *  would then return the parameters with names that would match the
     *  criteria).
     * 
     * @param  fieldValues a Map containing criteria to be used to retrieve
     *         the matching parameters.
     * @return List of Parameters that match the given criteria.
     */
    public List<Parameter> getParametersGivenLikeCriteria(Map<String, String> fieldValues);

}
