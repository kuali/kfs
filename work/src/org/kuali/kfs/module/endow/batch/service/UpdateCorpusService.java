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
package org.kuali.kfs.module.endow.batch.service;

public interface UpdateCorpusService {

    /**
     * Updates the corpus records in four main methods:
     * 1) If first day of fiscal year, Copies KEMID Corpus current year values to prior year values.
     * 2) Sums Tran Archive table and adds to Current Endow Corpus, then updates KEMID Corpus
     * 3) Updates every record in Current Endow Corpus for PRIN_MVAL with values from Current balance view.
     * 4) If last day of fiscal year, copy all records from Current Endow Corpus to Endow Corpus.
     * 
     * @return
     */
    public boolean updateCorpusTransactions();
}
