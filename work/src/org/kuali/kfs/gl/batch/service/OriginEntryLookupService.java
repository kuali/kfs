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

/**
 * An interface of methods that duplicate the relationships that OriginEntryFull has.
 * Why does this exist?  Because OriginEntryLite needs a way to get its related data.
 */
public interface OriginEntryLookupService {
    /**
     * Retrieve a chart for the given origin entry
     * 
     * @param entry the origin entry to get the chart for
     * @return the related Chart record, or null if not found
     */
    public Chart getChart(OriginEntry entry);

    /**
     * Retrieve account for given origin entry
     * 
     * @param entry the origin entry to retrieve the account of
     * @return the related account record, or null if not found
     */
    public Account getAccount(OriginEntry entry);

    /**
     * Retrieve financial object for given origin entry
     * 
     * @param entry the origin entry to retrieve the financial object of
     * @return the related financial object record, or null if not found
     */
    public ObjectCode getFinancialObject(OriginEntry entry);

    /**
     * Retrieve balance type, or, evidently, balance typ, for given origin entry
     * 
     * @param entry the origin entry to retrieve the balance type of
     * @return the related balance typ record, or null if not found
     */
    public BalanceTyp getBalanceType(OriginEntry entry);

    /**
     * Retrieve option for given origin entry
     * 
     * @param entry the origin entry to retrieve the related options record of
     * @return the related Options record, or null if not found
     */
    public Options getOption(OriginEntry entry);

    /**
     * Get object type for given origin entry
     * 
     * @param entry the origin entry to retrieve the object type of
     * @return the related object type record, or null if not found
     */
    public ObjectType getObjectType(OriginEntry entry);

    /**
     * Get sub account for given origin entry
     * 
     * @param entry the origin entry to retrieve the sub account of
     * @return the related SubAccount record, or null if not found
     */
    public SubAccount getSubAccount(OriginEntry entry);

    /**
     * Get A21SubAccount for given origin entryable
     * 
     * @param entry the origin entry to retrieve the A21 sub account of
     * @return the related A21 SubAccount record, or null if not found
     */
    public A21SubAccount getA21SubAccount(OriginEntry entry);

    /**
     * Get financial sub object for given origin entryable
     * 
     * @param entry the origin entry to retrieve the financial sub object of
     * @return the related financial sub object record, or null if not found
     */
    public SubObjCd getFinancialSubObject(OriginEntry entry);

    /**
     * Get document type for given origin entryable
     * 
     * @param entry the origin entry to retrieve the document type of
     * @return the related document type record, or null if not found
     */
    public DocumentType getDocumentType(OriginEntry entry);

    /**
     * Get the reference document type for the given origin entryable
     * 
     * @param entry origin entryable to lookup the reference document type for
     * @return the related reference DocumentType record, or null if not found
     */
    public DocumentType getReferenceDocumentType(OriginEntry entry);

    /**
     * Retrieves the project code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the project code of
     * @return the related ProjectCode record, or null if not found
     */
    public ProjectCode getProjectCode(OriginEntry entry);

    /**
     * Retrieves the accounting period for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the accounting period of
     * @return the related AccountingPeriod record, or null if not found
     */
    public AccountingPeriod getAccountingPeriod(OriginEntry entry);

    /**
     * Retrieves the origination code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the origin code of
     * @return the related OriginationCode record, or null if not found
     */
    public OriginationCode getOriginationCode(OriginEntry entry);

    /**
     * Set the caching lookup for this lookup service
     * 
     * @param lookupService an instance of CachingLookup to use to communicated with the persistence store
     */
    public void setLookupService(CachingLookup lookupService);
}
