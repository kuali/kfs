/*
 * Copyright 2006 The Kuali Foundation
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

/**
 * This interface defines the batch job that would to summarize the available spendable
 * funds for every KEMID that is not closed 
 */
public interface AvailableCashUpdateService {

    /**
     * This process will generate records at the end of each processing cycle to summarize the 
     * available spendable funds for every KEMID that is not closed.  
     */
    public boolean summarizeAvailableSpendableFunds();
}
