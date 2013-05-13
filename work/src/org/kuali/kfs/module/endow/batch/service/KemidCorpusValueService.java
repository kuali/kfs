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
package org.kuali.kfs.module.endow.batch.service;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface KemidCorpusValueService {

    /**
     * determines if there is a record in END_KEMDID_CORUPS_VAL_T table for the given kemid.
     * @param kemid, corpusPctTolerance
     * @return true if the amounts updated else return false
     */
    public boolean canFeeBeChargedToKemid(String kemid, KualiDecimal corpusPctTolerance);
}
