/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module;

import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.kns.authorization.AuthorizationConstants.EditMode;

public class LaborAuthorizationConstants extends JSTLConstants {

    public static class ExpenseTransaferEditMode extends EditMode {
        public static final String LEDGER_BALANCE_IMPORTING = "ledgerBalanceImporting";
    }
}