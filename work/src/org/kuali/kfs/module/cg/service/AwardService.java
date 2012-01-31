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
package org.kuali.kfs.module.cg.service;

import java.util.List;

import org.kuali.kfs.module.cg.businessobject.Award;

/**
 * Services for Award
 */

public interface AwardService {

    /**
     * This method returns a award by primary key
     * 
     * @param proposalNumber
     * @return
     */
    public Award getByPrimaryId(Long proposalNumber);

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     * 
     * @return errorString
     */
    public List<String> hasValidContractControlAccounts(Award award);
}
