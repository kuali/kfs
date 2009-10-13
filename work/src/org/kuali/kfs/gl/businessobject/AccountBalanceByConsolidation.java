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
package org.kuali.kfs.gl.businessobject;

/**
 * This class is an empty derived class of Balance for solving the conflict in lookup framework.
 */
public class AccountBalanceByConsolidation extends AccountBalance {

    /**
     * Constructs a AccountBalanceByConsolidation.java.
     */
    public AccountBalanceByConsolidation() {
        super();
    }

    /**
     * Constructs a AccountBalanceByConsolidation.java.
     * 
     * @param t
     */
    public AccountBalanceByConsolidation(Transaction t) {
        super(t);
    }
}
