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
package org.kuali.kfs.coa.service.impl;

import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.service.impl.InactivationBlockingDetectionServiceImpl;

/**
 * This class is used when the offset definition represents the object that is blocking other records from being inactivated.
 * 
 * Normally, only active BO's with the proper primary key values are allowed to inactivate other business objects.  However, 
 * OffsetDefinitions do not have an active indicator.  An offset definition that references another BO is allowed to block the inactivation
 * of that BO, without regard to active status, because the OD bo does not have an active status on it.
 */
public class OffsetDefinitionInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {

    /**
     * @see org.kuali.rice.kns.service.impl.InactivationBlockingDetectionServiceImpl#addBlockableRowProperty(java.util.Map, org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata)
     */
    @Override
    protected void addBlockableRowProperty(Map<String, Object> queryMap, BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        // do nothing, do not use active indicator
    }
}
