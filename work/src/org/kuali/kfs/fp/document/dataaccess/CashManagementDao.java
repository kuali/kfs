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
package org.kuali.kfs.fp.document.dataaccess;

import java.util.List;

import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;

public interface CashManagementDao {

    /**
     * This method returns a list of open items in process for a given campus code
     * 
     * @param campusCode the campus code to use to search open items in process for
     * @return a list of open items in process
     */
    public List<CashieringItemInProcess> findOpenItemsInProcessByCampusCode(String campusCode);

    /**
     * This finds items in process associated with the given campus code closed within the past 30 days.
     * 
     * @param campusCode the campus code that the found items in process should be associated with
     * @return a list of CashieringItemInProcess records
     */
    public List<CashieringItemInProcess> findRecentlyClosedItemsInProcess(String campusCode);

    /**
     * Retrieves all currency detail records with the given document number, document type code, and cashiering record source
     * 
     * @param documentNumber the document number this currency detail was associated with
     * @param documentTypeCode the type code of that document
     * @param cashieringStatus the cashiering status
     * @return a list of currency details matching that criteria
     */
    public CurrencyDetail findCurrencyDetailByCashieringStatus(String documentNumber, String documentTypeCode, String cashieringStatus);

    /**
     * Retrieves all coin detail records with the given document number, document type code, and cashiering record source
     * 
     * @param documentNumber the document the coin details were associated with
     * @param documentTypeCode the type of that document
     * @param cashieringStatus the cashiering status
     * @return a list of coin details meeting those criteria
     */
    public CoinDetail findCoinDetailByCashieringStatus(String documentNumber, String documentTypeCode, String cashieringStatus);

    /**
     * Retrieves from the database any undeposited cashiering transaction checks associated with the given cash management document
     * 
     * @param documentNumber the document number of a cash management document that cashiering transaction checks may be associated
     *        with
     * @return a list of checks associated with the document
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber);

    /**
     * Retrieves from the database all cashiering transaction checks deposited for a given deposit
     * 
     * @param documentNumber the document number of a cash management document that cashiering transaction checks have been
     *        deposited for
     * @param depositLineNumber the line number of the deposit to find checks deposited for
     * @return a list of checks associated with the given deposit
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber);

    /**
     * Retrieves all deposited cashiering checks from the database
     * 
     * @param documentNumber the document to get checks associated with
     * @return a list of deposited checks
     */
    public List<Check> selectDepositedCashieringChecks(String documentNumber);

    /**
     * This method retrieves all currency details associated with a cash management document
     * 
     * @param documentNumber the document number of the cash management document to get currency details for
     * @return a list of currency details
     */
    public List<CurrencyDetail> getAllCurrencyDetails(String documentNumber);

    /**
     * This method gets all coin details for a particular document number, irregardless of cashiering record source
     * 
     * @param documentNumber the document number to find cash details for
     * @return hopefully, a bunch of coin details
     */
    public List<CoinDetail> getAllCoinDetails(String documentNumber);

    /**
     * Select the next available check line number for the given cash management document
     * 
     * @param documentNumber the document number of a cash management document
     * @return the next available check line number for cashiering checks
     */
    public Integer selectNextAvailableCheckLineNumber(String documentNumber);
}
