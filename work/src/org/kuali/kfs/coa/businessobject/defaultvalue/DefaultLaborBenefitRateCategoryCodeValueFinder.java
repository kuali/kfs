/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.coa.businessobject.defaultvalue;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class DefaultLaborBenefitRateCategoryCodeValueFinder implements ValueFinder {

    @Override
    public String getValue() {
        String defaultValue = "";
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        
        //make sure the parameter exists
        if(parameterService.parameterExists(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE)){
            defaultValue = parameterService.getParameterValueAsString(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE);
        }
        
        return defaultValue;
    }

}
