/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;

public interface PooledFundControlTransactionsDao {

    /**
     * Gets a list of TransactionArchive  
     * @param  List<String> 
     * @return List<TransactionArchive>
     */    
    public List<TransactionArchive> getTransactionArchive(List<String> trnsactionTypeCodes);
    
    /**
     * Gets a list of TransactionArchiveSecurity  
     * @return List<TransactionArchiveSecurity>
     */ 
    public List<TransactionArchiveSecurity> getTransactionArchiveSecurity(List<String> trnsactionTypeCodes);
    
    /**
     * Gets a list of PooledFundControl
     * @return List<PooledFundControl>
     */
    List<PooledFundControl> getPooledFundControlTransactions(List<String> trnsactionTypeCodes);
}
