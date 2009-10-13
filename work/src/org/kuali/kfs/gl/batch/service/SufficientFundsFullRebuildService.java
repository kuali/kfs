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
package org.kuali.kfs.gl.batch.service;

/**
 * An interface declaring methods needed to run the sufficient funds sync service.  It should be noted, though,
 * that this service is only run under unusual circumstances; most of the time, simply saving object codes and
 * accounts will save the records that this service would, in general, generate
 */
public interface SufficientFundsFullRebuildService {
    /**
     * Makes certain that sufficient fund balances will be rebuilt for all accounts and object codes
     */
    public void syncSufficientFunds();
}
