/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;

public interface PooledFundControlTransactionsDao {

    /**
     * Gets all pooled fund control transaction
     * @return List<PooledFundControl>
     */
    public List<PooledFundControl> getAllPooledFundControlTransaction();
    
    /**
     * Gets all transaction archive security with security id and document names
     * @param securityId
     * @param documentTypeNames
     * @return
     */
    public List<TransactionArchiveSecurity> getTransactionArchiveSecurityWithSecurityId(PooledFundControl pooledFundControl, List<String> documentTypeNames, Date currentDate);
    
    /**
     * Gets all transaction archive with security id and document names
     * @param securityId
     * @param documentTypeNames
     * @return
     */
    public List<TransactionArchive> getTransactionArchiveWithSecurityAndDocNames(PooledFundControl pooledFundControl, List<String> documentTypeNames, Date currentDate);
}
