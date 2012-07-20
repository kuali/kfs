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
package org.kuali.kfs.module.tem.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;
import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;

public interface CreditCardAgencyService {
    
    /**
     * Get a list of the credit card agency of type Corp Card
     * 
     * @return
     */
    public List<CreditCardAgency> getCorpCreditCardAgencyList();
    
    /**
     * Get a list of the credit card agency of type Corp Card of their codes
     * 
     * @return
     */
    public List<String> getCorpCreditCardAgencyCodeList();


}
