/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.gl.dataaccess;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentTypeCode;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;


  //TODO is this a good name?????  probably not - should the DAO even be doing the caching?
public interface CachingDao {
        /**
         * Retrieve a chart for the given origin entry
         * 
         * @param entry the origin entry to get the chart for
         * @return the related Chart record, or null if not found
         */
        public Chart getChart(OriginEntry entry);
        public Chart getChart(String chartOfAccountsCode);

        /**
         * Retrieve account for given origin entry
         * 
         * @param entry the origin entry to retrieve the account of
         * @return the related account record, or null if not found
         */
        public Account getAccount(OriginEntry entry);
        public Account getAccount(String chartCode, String accountNumber);

        /**
         * Retrieve financial object for given origin entry
         * 
         * @param entry the origin entry to retrieve the financial object of
         * @return the related financial object record, or null if not found
         */
        public ObjectCode getFinancialObject(OriginEntry entry);
        public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

        /**
         * Retrieve balance type, or, evidently, balance typ, for given origin entry
         * 
         * @param entry the origin entry to retrieve the balance type of
         * @return the related balance typ record, or null if not found
         */
        public BalanceType getBalanceType(OriginEntry entry);
        public BalanceType getBalanceType(String financialBalanceTypeCode);

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
        public ObjectType getObjectType(String financialObjectTypeCode);

        /**
         * Get sub account for given origin entry
         * 
         * @param entry the origin entry to retrieve the sub account of
         * @return the related SubAccount record, or null if not found
         */
        public SubAccount getSubAccount(OriginEntry entry);
        public SubAccount getSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

        /**
         * Get A21SubAccount for given origin entryable
         * 
         * @param entry the origin entry to retrieve the A21 sub account of
         * @return the related A21 SubAccount record, or null if not found
         */
        public A21SubAccount getA21SubAccount(OriginEntry entry);
        public A21SubAccount getA21SubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

        /**
         * Get financial sub object for given origin entryable
         * 
         * @param entry the origin entry to retrieve the financial sub object of
         * @return the related financial sub object record, or null if not found
         */
        public SubObjectCode getFinancialSubObject(OriginEntry entry);
        public SubObjectCode getFinancialSubObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode);

        /**
         * Get document type for given origin entryable
         * 
         * @param entry the origin entry to retrieve the document type of
         * @return the related document type record, or null if not found
         */
        public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCode(OriginEntry entry);
        public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCode(String financialDocumentTypeCode);

        /**
         * Get the reference document type for the given origin entryable
         * 
         * @param entry origin entryable to lookup the reference document type for
         * @return the related reference DocumentType record, or null if not found
         */
        public FinancialSystemDocumentTypeCode getReferenceFinancialSystemDocumentTypeCode(OriginEntry entry);

        /**
         * Retrieves the project code for the given origin entryable
         * 
         * @param entry the origin entry to retrieve the project code of
         * @return the related ProjectCode record, or null if not found
         */
        public ProjectCode getProjectCode(OriginEntry entry);
        public ProjectCode getProjectCode(String projectCode);

        /**
         * Retrieves the accounting period for the given origin entryable
         * 
         * @param entry the origin entry to retrieve the accounting period of
         * @return the related AccountingPeriod record, or null if not found
         */
        public AccountingPeriod getAccountingPeriod(OriginEntry entry);
        public AccountingPeriod getAccountingPeriod(Integer fiscalYear, String fiscalPeriodCode);

        /**
         * Retrieves the origination code for the given origin entryable
         * 
         * @param entry the origin entry to retrieve the origin code of
         * @return the related OriginationCode record, or null if not found
         */
        public OriginationCode getOriginationCode(OriginEntry entry);
        public OriginationCode getOriginationCode(String financialSystemOriginationCode);

        //TODO is it right to put these here?  They weren't in BusinessObjectDao.
        public SubFundGroup getSubFundGroup(String subFundGroupCode);
        public UniversityDate getUniversityDate(Date date);
        public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode);
        public Organization getOrg(String chartOfAccountsCode, String organizationCode);
        public ObjectLevel getObjectLevel(String chartOfAccountsCode, String financialObjectLevelCode);
        public String getAccountChart(String accountNumber); //TODO this is IU-specific, shouldn't be needed in kuali
        public void insertReversal(Reversal reversal);
        public int getMaxSequenceNumber(Transaction t);
        public void insertEntry(Entry entry);
        
        public IndirectCostRecoveryType getIndirectCostRecoveryType(String accountIcrTypeCode);
        
    }
