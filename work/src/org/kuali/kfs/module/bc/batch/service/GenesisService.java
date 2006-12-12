/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/batch/service/GenesisService.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License")
;
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
package org.kuali.module.budget.service;
import java.util.*;
/*
 *   this service intializes/updates the budget construction data used by the
 *   budget module to build a new budget for the coming fiscal year
 */
public interface GenesisService {
   /*
     * build the budget construction GL table from the BALANCE_TYPE_BASE_BUDGET rows in 
     * GL table
    */ 
    public void stepBudgetConstructionGLLoad (Integer universityFiscalYear);
    
    
}
