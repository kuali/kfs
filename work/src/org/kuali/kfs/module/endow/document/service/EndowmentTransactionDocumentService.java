/*
 * Copyright 2009 The Kuali Foundation.
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
