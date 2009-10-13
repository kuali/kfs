/*
 * Copyright 2007-2008 The Kuali Foundation
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

package org.kuali.kfs.module.purap.document.service;

import java.util.Collection;

import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;

public interface ReceivingAddressService {

    /**
     * Finds all of the active receiving addresses with the specified chart/org code.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - collection of receiving addresses found.
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode);

    /**
     * Finds all of the active default receiving addresses with the specified chart/org code.
     * When the database is not in consistent state, there could be more than one active default address per chart/org.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - collection of receiving addresses found.
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode);

    /**
     * Finds the unique active default receiving addresses with the specified chart/org code.
     * When the database is in consistent state, there shall be no more than one active default address per chart/org.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - receiving addresses found.
     */
    public ReceivingAddress findUniqueDefaultByChartOrg(String chartCode, String orgCode);

    /**
     * Counts the number of the active receiving addresses with the specified chart/org code.
     * 
     * @param chartCode - chart of accounts code.
     * @param orgCode - organization code.
     * @return - number of receiving addresses found.
     */
    public int countActiveByChartOrg(String chartCode, String orgCode);

}

