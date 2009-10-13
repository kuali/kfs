/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.batch.service;

/**
 * The interface defines the methods that creates effort certification documents from the temporary build documents and routes them
 * for approval.
 */
public interface EffortCertificationCreateService {

    /**
     * create effort certification documents and submit it for approval. The reporting period can be determined by both of fiscal
     * year and report number declared as system parameters.
     */
    public void create();

    /**
     * create effort certification documents and submit it for approval. The reporting period can be determined by both of fiscal
     * year and report number.
     * 
     * @param fiscalYear the given fiscal year with which Labor ledgers can be extracted.
     * @param reportNumber the given report number to run.
     */
    public void create(Integer fiscalYear, String reportNumber);
}
