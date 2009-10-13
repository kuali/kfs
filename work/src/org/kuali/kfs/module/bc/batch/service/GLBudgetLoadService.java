/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.batch.service;

public interface GLBudgetLoadService {
    
//
//  loads pending budget construction GL (replacing any corresponding GL rows which already exist)
//  overloaded methods are provided so that one can load only pending GL rows for a specific fiscal year key
//  (if there happens to be more than one fiscal year key in the pending budget construction GL table) or
//  for the coming fiscal year.    
//
//
//  load pending budget construction GL for a specific fiscal year
    public void loadPendingBudgetConstructionGeneralLedger(Integer FiscalYear);
//
//  load for the fiscal year following the fiscal year of the current date
    public void loadPendingBudgetConstructionGeneralLedger();

}
