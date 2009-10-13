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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.pdp.businessobject.ACHBank;

public interface AchBankService {

    /**
     * Read the bank data from a text file and reset table to match this file. The format of the file comes from here:
     * https://www.fededirectory.frb.org/format_ACH.cfm https://www.fededirectory.frb.org/FedACHdir.txt
     * 
     * @param filename
     */
    public boolean reloadTable(String filename);
    
    /**
     * Returns the AchBank for the given routing number
     * 
     * @param bankRoutingNumber pk for AchBank
     * @return AchBank
     */
    public ACHBank getByPrimaryId(String bankRoutingNumber);
}
