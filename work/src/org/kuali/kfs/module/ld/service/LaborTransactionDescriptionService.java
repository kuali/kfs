/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * This class...
 */
public interface LaborTransactionDescriptionService {

    /**
     * get the transaction description from the description map for the given transaction, where the description map can be defined
     * and injected.
     * 
     * @param transaction the given transaction
     * @return the transaction description indexed by the document type of the given transaction
     */
    public String getTransactionDescription(Transaction transaction);

    /**
     * get the transaction description from the description map with the given key
     * 
     * @param descriptionKey the given key that indexes a description in the description map, where the description map can be
     *        defined and injected.
     * @return the transaction description indexed by the given key
     */
    public String getTransactionDescription(String descriptionKey);
}
