/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.LookupResults;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.SelectedObjectIds;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.lookup.LookupResultsServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.service.SegmentedLookupResultsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Used for segemented lookup results
 */
@Transactional
public class SegmentedLookupResultsServiceImpl extends LookupResultsServiceImpl implements SegmentedLookupResultsService { 

    private static final Log LOG = LogFactory.getLog(SegmentedLookupResultsServiceImpl.class);

    private DateTimeService dateTimeService;
    
    /**
     * Retrieve the Date Time Service
     *
     * @return Date Time Service
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }
    
    /**
     * Assign the Date Time Service
     *
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    /**
     * @see org.kuali.core.lookup.LookupResultsService#persistResultsTable(java.lang.String, java.util.List, java.lang.String)
     */
    public void persistResultsTable(String lookupResultsSequenceNumber, List<ResultRow> resultTable, String universalUserId) throws Exception {
        String resultTableString = new String(Base64.encodeBase64(ObjectUtils.toByteArray(resultTable)));
        
        Timestamp now = getDateTimeService().getCurrentTimestamp();
        
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (lookupResults == null) {
            lookupResults = new LookupResults();
            lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        lookupResults.setLookupUniversalUserId(universalUserId);
        lookupResults.setSerializedLookupResults(resultTableString);
        lookupResults.setLookupDate(now);
        getBusinessObjectService().save(lookupResults);
        LOG.debug("Wrote resultTable " + resultTable);
    }

    /**
     * @see org.kuali.core.lookup.LookupResultsService#persistSelectedObjectIds(java.lang.String, java.util.Set, java.lang.String)
     */
    public void persistSelectedObjectIds(String lookupResultsSequenceNumber, Set<String> selectedObjectIds, String universalUserId) throws Exception {
        SelectedObjectIds selectedObjectIdsBO = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (selectedObjectIdsBO == null) {
            selectedObjectIdsBO = new SelectedObjectIds();
            selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        selectedObjectIdsBO.setLookupUniversalUserId(universalUserId);
        selectedObjectIdsBO.setSelectedObjectIds(LookupUtils.convertSetOfObjectIdsToString(selectedObjectIds));
        selectedObjectIdsBO.setLookupDate(getDateTimeService().getCurrentTimestamp());
        getBusinessObjectService().save(selectedObjectIdsBO);
    }

    /**
     * @see org.kuali.core.lookup.LookupResultsService#retrieveResultsTable(java.lang.String, java.lang.String)
     */
    public List<ResultRow> retrieveResultsTable(String lookupResultsSequenceNumber, String universalUserId) throws Exception {
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (!isAuthorizedToAccessLookupResults(lookupResults, universalUserId)) {
            // TODO: use the other identifier
            throw new AuthorizationException(universalUserId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        List<ResultRow> resultTable = (List<ResultRow>) ObjectUtils.fromByteArray(Base64.decodeBase64(lookupResults.getSerializedLookupResults().getBytes()));
        return resultTable;
    }

    /**
     * Returns a list of BOs that were selected.
     * 
     * This implementation makes an attempt to retrieve all BOs with the given object IDs, unless they have been deleted or the object ID changed.
     * Since data may have changed since the search, the returned BOs may not match the criteria used to search.
     * 
     * @see org.kuali.core.lookup.LookupResultsService#retrieveSelectedResultBOs(java.lang.String, java.lang.Class, java.lang.String)
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Class boClass, String universalUserId) throws Exception {
        Set<String> setOfSelectedObjIds = retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, universalUserId);
        return retrieveSelectedResultBOs(lookupResultsSequenceNumber, setOfSelectedObjIds, boClass, universalUserId);
    }
    
    /**
     * 
     * @param lookupResultsSequenceNumber
     * @param universalUserId
     * @return Set<String> 
     */
    public Set<String> retrieveSetOfSelectedObjectIds(String lookupResultsSequenceNumber, String universalUserId) throws Exception {
        SelectedObjectIds selectedObjectIds = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (!isAuthorizedToAccessSelectedObjectIds(selectedObjectIds, universalUserId)) {
            // TODO: use the other identifier
            throw new AuthorizationException(universalUserId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        Set<String> retval = LookupUtils.convertStringOfObjectIdsToSet(selectedObjectIds.getSelectedObjectIds());
        return retval;
    }

    /**
     *
     * @param lookupResultsSequenceNumber
     * @param setOfSelectedObjIds
     * @param boClass
     * @param universalUserId
     * @return Collection<PersistableBusinessObject>
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, 
                                                                           Set<String> setOfSelectedObjIds,
                                                                           Class boClass, String universalUserId) throws Exception {
        LOG.debug("Retrieving results for class " + boClass + " with objectIds " + setOfSelectedObjIds);
        if (setOfSelectedObjIds.isEmpty()) {
            // OJB throws exception if querying on empty set
            return new ArrayList<PersistableBusinessObject>();
        }
        Map<String, Collection<String>> queryCriteria = new HashMap<String, Collection<String>>();
        queryCriteria.put(KFSPropertyConstants.OBJECT_ID, setOfSelectedObjIds);
        return getBusinessObjectService().findMatching(boClass, queryCriteria);
    }
    
}
