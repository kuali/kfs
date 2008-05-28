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
package org.kuali.module.financial.service;

/**
 * Utility methods used by the transfer of funds document.
 */
public interface TransferOfFundsService {
    /**
     * This method determines whether an object sub-type code is a mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a manadatory transfer, false otherwise.
     */
    public abstract boolean isMandatoryTransfersSubType(String objectSubTypeCode);
    
    /**
     * This method determines whether an object sub-type code is a non-mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a non-mandatory transfer, false otherwise.
     */
    public abstract boolean isNonMandatoryTransfersSubType(String objectSubTypeCode);
}
