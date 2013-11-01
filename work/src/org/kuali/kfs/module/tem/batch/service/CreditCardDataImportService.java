/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.CreditCardImportData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.rice.krad.util.ErrorMessage;

public interface CreditCardDataImportService {
    public boolean importCreditCardData();

    public boolean importCreditCardDataFile(String dataFileName, BatchInputFileType inputFileType);

    public boolean isDuplicate(CreditCardStagingData creditCardData, List<ErrorMessage> errorMessages);

    public TemProfileAccount findTraveler(CreditCardStagingData creditCardData);

    public List<CreditCardStagingData> validateCreditCardData(CreditCardImportData creditCardList, String dataFileName);

    public boolean validateAndSetCreditCardAgency(CreditCardStagingData creditCardData);

    public boolean moveCreditCardDataToHistoricalExpenseTable();
}
