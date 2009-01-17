/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.rice.kns.bo.ModuleConfiguration;

/**
 * Slim subclass to enforce class hierarchy not enforced by the parent class' contract.
 */
public class FinancialSystemModuleConfiguration extends ModuleConfiguration {
    protected List<FiscalYearMaker> fiscalYearMakers;

    /**
     * Constructs a FinancialSystemModuleConfiguration.java.
     */
    public FinancialSystemModuleConfiguration() {
        super();
        
        fiscalYearMakers = new ArrayList<FiscalYearMaker>();
    }

    /**
     * Gets the fiscalYearMakers attribute.
     * 
     * @return Returns the fiscalYearMakers.
     */
    public List<FiscalYearMaker> getFiscalYearMakers() {
        return fiscalYearMakers;
    }

    /**
     * Sets the fiscalYearMakers attribute value.
     * 
     * @param fiscalYearMakers The fiscalYearMakers to set.
     */
    public void setFiscalYearMakers(List<FiscalYearMaker> fiscalYearMakers) {
        this.fiscalYearMakers = fiscalYearMakers;
    }

}
