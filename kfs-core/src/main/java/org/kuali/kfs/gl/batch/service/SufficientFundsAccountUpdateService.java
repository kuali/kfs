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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;


/**
 * An interface declaring a method that runs the sufficient funds rebuilder process.
 */
public interface SufficientFundsAccountUpdateService {

    /**
     * Rebuilds all necessary sufficient funds balances.
     */
    public void rebuildSufficientFunds();
    
    /**
     * Given an O SF rebuild type, it will look up all of the matching balances in the table and add each account it finds as an A
     * SF rebuild type.
     * 
     * @param sfrb the sufficient fund rebuild record to convert
     */
    public abstract void convertOtypeToAtypes(SufficientFundRebuild sfrb);
    
    /**
     * Updates sufficient funds balances for the given account
     * 
     * @param sfrb the sufficient fund rebuild record, with a chart and account number
     */
    public abstract void calculateSufficientFundsByAccount(SufficientFundRebuild sfrb);
}
