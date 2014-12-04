/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
