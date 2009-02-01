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
package org.kuali.kfs.gl.batch.service;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.service.impl.CachingLookup;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentTypeCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.UniversityDate;

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
    public BalanceType getBalanceType(OriginEntry entry);

    /**
     * Retrieve option for given origin entry
     * 
     * @param entry the origin entry to retrieve the related options record of
     * @return the related Options record, or null if not found
     */
    public SystemOptions getSystemOptions(OriginEntry entry);
    
    public SystemOptions getSystemOptions(Integer fiscalYear);

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
    public SubObjectCode getFinancialSubObject(OriginEntry entry);

    /**
     * Get general ledger input type for given origin entryable
     * 
     * @param entry the origin entry to retrieve the GL input type of
     * @return the related GeneralLedgerInputType record, or null if not found
     */
    public FinancialSystemDocumentTypeCode getGeneralLedgerInputType(OriginEntry entry);

    /**
     * Get the reference general ledger input type for the given origin entryable
     * 
     * @param entry origin entryable to lookup the reference GL input type for
     * @return the related reference GeneralLedgerInputType record, or null if not found
     */
    public FinancialSystemDocumentTypeCode getReferenceGeneralLedgerInputType(OriginEntry entry);

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
    
    public OriginationCode getOriginationCode(String financialSystemOriginationCode);

    /**
     * Set the caching lookup for this lookup service
     * 
     * @param lookupService an instance of CachingLookup to use to communicated with the persistence store
     */
    public void setLookupService(CachingLookup lookupService);
    
    //TODO is it right to put these here?  They weren't here before.  Was getAccount already setting this on the account??
    public SubFundGroup getSubFundGroup(String subFundGroupCode);
    public Account getAccount(String chartOfAccountsCode, String accountNumber);
    public UniversityDate getUniversityDate(Date date);
    public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode);
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
    public Organization getOrg(String chartOfAccountsCode, String organizationCode);
    //TODO: need to move labor
    //public LaborObject getLaborObject(OriginEntry originEntry);
}


