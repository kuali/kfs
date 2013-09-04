/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.defaultvalue;

import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.sys.businessobject.defaultvalue.SequenceValueFinder;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public class NextAgencyStagingDataIdFinder extends SequenceValueFinder {
    public static final String AGENCY_STAGING_DATA_SEQUENCE_NAME = "TEM_AGENCY_STAGING_ID_SEQ";

    @Override
    public String getSequenceName() {
        return AGENCY_STAGING_DATA_SEQUENCE_NAME;
    }

    @Override
    public Class<? extends PersistableBusinessObject> getAssociatedClass() {
        return AgencyStagingData.class;
    }

}
