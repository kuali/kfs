/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ec.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It extends the
 * PositionDataDetailsInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class EffortPositionDataDetailsInquirableImpl extends PositionDataDetailsInquirableImpl {

    /**
     * @see PositionDataDetailsInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.POSITION_NUMBER);
        userDefinedAttributeMap.put(LaborPropertyConstants.EFFECTIVE_DATE, LaborPropertyConstants.EFFECTIVE_DATE);

        return userDefinedAttributeMap;
    }
}
