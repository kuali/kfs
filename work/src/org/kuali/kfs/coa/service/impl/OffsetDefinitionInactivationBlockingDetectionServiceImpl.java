/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.service.impl.InactivationBlockingDetectionServiceImpl;

/**
 * This class is used when the offset definition represents the object that is blocking other records from being inactivated.
 * 
 * Normally, only active BO's with the proper primary key values are allowed to inactivate other business objects.  However, 
 * OffsetDefinitions do not have an active indicator.  An offset definition that references another BO is allowed to block the inactivation
 * of that BO, without regard to active status, because the OD bo does not have an active status on it.
 */
public class OffsetDefinitionInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OffsetDefinitionInactivationBlockingDetectionServiceImpl.class);
    /**
     * Overriding to let blocking records through
     * @see org.kuali.rice.krad.service.impl.InactivationBlockingDetectionServiceImpl#listAllBlockerRecords(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)
     */
    public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Collection<BusinessObject> blockingRecords = new ArrayList<BusinessObject>();

        Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking for blocker records for object: " + blockedBo);
            LOG.debug("    With Metadata: " + inactivationBlockingMetadata);
            LOG.debug("    Resulting Query Map: " + queryMap);
        }

        if (queryMap != null) {
            Collection<? extends BusinessObject> potentialBlockingRecords = businessObjectService.findMatching(
                    inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
            for (Iterator<? extends BusinessObject> iterator = potentialBlockingRecords.iterator(); iterator.hasNext();) {
                MutableInactivatable businessObject = (MutableInactivatable) iterator.next();
                blockingRecords.add((BusinessObject) businessObject);
            }
        }

        return blockingRecords;
    }
    
    /**
     * Overriding to say that any record of the same PK is blocking..
     * @see org.kuali.rice.krad.service.impl.InactivationBlockingDetectionServiceImpl#hasABlockingRecord(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata)
     */
    public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        boolean hasBlockingRecord = false;

        Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
        if (queryMap != null) {
            Collection<? extends BusinessObject> potentialBlockingRecords = businessObjectService.findMatching(
                    inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
            return !potentialBlockingRecords.isEmpty();
        }

        // if queryMap were null, means that we couldn't perform a query, and hence, need to return false
        return hasBlockingRecord;
    }
}
