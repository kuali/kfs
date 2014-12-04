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
package org.kuali.kfs.module.endow.document.service;

import org.kuali.kfs.module.endow.businessobject.Security;

/**
 * This class...
 */
public interface EndowmentTransactionDocumentService {

    /**
     * This method gets an endowment transaction by the primary key: endowment transaction code
     * 
     * @param endowmentTransactionCode
     * @return the endowment transaction
     */
    public String[] getSecurity(String securityID);
    
    /**
     * Check if there is a GLLink record in the EndowmentTransactionCode that the chart code in the record matches the chart code in the KemidGeneralLedgerAccount associated with a KEMID  given the IP indicator info.
     * @param kemid
     * @param etranCode
     * @param ipIndicator
     * @return true or false
     */
    public boolean matchChartBetweenKEMIDAndETranCode(String kemid, String etranCode, String ipIndicator);
    
    /**
     * Check if there is a GLLink record in the EndowmentTransactionCode that the chart code in the record matches the chart code in the GeneralLedgerAccount associated with a Security given the IP indicator info.
     * 
     * @param securityID
     * @param etranCode
     * @param ipIndicator
     * @return
     */
    public boolean matchChartBetweenSecurityAndETranCode(Security security, String etranCode, String ipIndicator);

    /**
     * This method gets security by the primary key: securityId
     * 
     * @param securityId
     * @return String[]
     */
    public String[] getSecurityForHoldingHistoryValueAdjustment(String securityId);
    
}
