/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import org.kuali.core.bo.DocumentType;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.util.CachingLookup;

public interface OriginEntryLookupService {
    /**
     * Retrieve chart for the given origin entry
     * @param entry
     * @return
     */
    public Chart getChart(OriginEntry entry);

    /**
     * Retrieve account for given origin entry
     * @param entry
     * @return
     */
    public Account getAccount(OriginEntry entry);

    /**
     * Retrieve financial object for given origin entry
     * @param entry
     * @return
     */
    public ObjectCode getFinancialObject(OriginEntry entry);

    /**
     * Retrieve balance type, or, evidently, balance typ, for given origin entry
     * @param entry
     * @return
     */
    public BalanceTyp getBalanceType(OriginEntry entry);

    /**
     * Retrieve option for given origin entry
     * @param entry
     * @return
     */
    public Options getOption(OriginEntry entry);

    /**
     * Get object type for given origin entry
     * @param entry
     * @return
     */
    public ObjectType getObjectType(OriginEntry entry);
    
    /**
     * Get sub account for given origin entry
     * @param entry
     * @return
     */
    public SubAccount getSubAccount(OriginEntry entry);
    
    /**
     * Get A21SubAccount for given origin entryable
     * @param entry
     * @return
     */
    public A21SubAccount getA21SubAccount(OriginEntry entry);
    
    /**
     * Get financial sub object for given origin entryable
     * @param entry
     * @return
     */
    public SubObjCd getFinancialSubObject(OriginEntry entry);
    
    /**
     * Get document type for given origin entryable
     * @param entry
     * @return
     */
    public DocumentType getDocumentType(OriginEntry entry);
    
    /**
     * Get the reference document type for the given origin entryable
     * @param entry origin entryable to lookup the reference document type for
     * @return a document type
     */
    public DocumentType getReferenceDocumentType(OriginEntry entry);

    /**
     * Retrieves the project code for the given origin entryable
     * @param entry
     * @return
     */
    public ProjectCode getProjectCode(OriginEntry entry);
    
    /**
     * Retrieves the accounting period for the given origin entryable
     * @param entry
     * @return
     */
    public AccountingPeriod getAccountingPeriod(OriginEntry entry);
    
    /**
     * Retrieves the origination code for the given origin entryable
     * @param entry
     * @return
     */
    public OriginationCode getOriginationCode(OriginEntry entry);
    
    /**
     * Set the caching lookup for this lookup service
     * @param lookupService
     */
    public void setLookupService(CachingLookup lookupService);
}
