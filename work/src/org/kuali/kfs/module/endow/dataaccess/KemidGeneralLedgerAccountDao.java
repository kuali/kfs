/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.Map;

public interface KemidGeneralLedgerAccountDao {

    /**
     * Gets a Map containing CHRT_CD and ACCT_NBR from END_KEMID_GL_LNK_T table
     * 
     * @param kemid
     * @param incomePrincipalIndicator should be passed in upper case if this filed has a forceUpperCase specified in
     *        KemidGeneralLedgerAccount DD file
     * @return a map containing chart and account number
     */
    public Map<String, String> getChartAndAccountNumber(String kemid, String incomePrincipalIndicator);
}
