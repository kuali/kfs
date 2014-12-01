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
package org.kuali.kfs.sys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.rice.krad.bo.ModuleConfiguration;

/**
 * Slim subclass to enforce class hierarchy not enforced by the parent class' contract.
 */
public class FinancialSystemModuleConfiguration extends ModuleConfiguration {
    protected List<FiscalYearMaker> fiscalYearMakers;
    protected List<String> batchFileDirectories;
    
    /**
     * Constructs a FinancialSystemModuleConfiguration.java.
     */
    public FinancialSystemModuleConfiguration() {
        super();
        
        fiscalYearMakers = new ArrayList<FiscalYearMaker>();
        batchFileDirectories = new ArrayList<String>();
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
    
    public List<String> getBatchFileDirectories() {
        return batchFileDirectories;
    }
    
    public void setBatchFileDirectories(List<String> batchFileDirectories) {
        if (batchFileDirectories == null) {
            this.batchFileDirectories = new ArrayList<String>();
        } else {
            this.batchFileDirectories = batchFileDirectories;
            for (String batchFileDirectory : this.batchFileDirectories) {
                File directory = new File(batchFileDirectory);
                if ( !directory.exists() ) {
                    if ( !directory.mkdirs() ) {
                        throw new RuntimeException( batchFileDirectory + " does not exist and the server was unable to create it." );
                    }
                } else {
                    if (!directory.isDirectory()) {
                        throw new RuntimeException(batchFileDirectory + " exists but is not a directory.");
                    }
                }
            }
        }
    }
}
