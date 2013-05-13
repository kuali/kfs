/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;

/**
 * This class...
 */
public interface EndowmentTransactionCodeService {

    /**
     * This method gets an endowment transaction by the primary key: endowment transaction code
     * 
     * @param endowmentTransactionCode
     * @return the endowment transaction
     */
    public EndowmentTransactionCode getByPrimaryKey(String endowmentTransactionCode);

}
