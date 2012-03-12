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

package org.kuali.kfs.sys.service.impl;

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
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.LookupResultsServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.LookupResults;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.SelectedObjectIds;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.ObjectUtils;
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
     * @see org.kuali.rice.kns.lookup.LookupResultsService#persistResultsTable(java.lang.String, java.util.List, java.lang.String)
     */
    public void persistResultsTable(String lookupResultsSequenceNumber, List<ResultRow> resultTable, String personId) throws Exception {
        String resultTableString = new String(Base64.encodeBase64(ObjectUtils.toByteArray(resultTable)));

        Timestamp now = getDateTimeService().getCurrentTimestamp();

        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (lookupResults == null) {
            lookupResults = new LookupResults();
            lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        lookupResults.setLookupPersonId(personId);
        lookupResults.setSerializedLookupResults(resultTableString);
        lookupResults.setLookupDate(now);
        getBusinessObjectService().save(lookupResults);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Wrote resultTable " + resultTable);
        }
    }

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#persistSelectedObjectIds(java.lang.String, java.util.Set, java.lang.String)
     */
    public void persistSelectedObjectIds(String lookupResultsSequenceNumber, Set<String> selectedObjectIds, String personId) throws Exception {
        SelectedObjectIds selectedObjectIdsBO = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (selectedObjectIdsBO == null) {
            selectedObjectIdsBO = new SelectedObjectIds();
            selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        selectedObjectIdsBO.setLookupPersonId(personId);
        selectedObjectIdsBO.setSelectedObjectIds(LookupUtils.convertSetOfObjectIdsToString(selectedObjectIds));
        selectedObjectIdsBO.setLookupDate(getDateTimeService().getCurrentTimestamp());
        getBusinessObjectService().save(selectedObjectIdsBO);
    }

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#retrieveResultsTable(java.lang.String, java.lang.String)
     */
    public List<ResultRow> retrieveResultsTable(String lookupResultsSequenceNumber, String personId) throws Exception {
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (!isAuthorizedToAccessLookupResults(lookupResults, personId)) {
            throw new AuthorizationException(personId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        List<ResultRow> resultTable = (List<ResultRow>) ObjectUtils.fromByteArray(Base64.decodeBase64(lookupResults.getSerializedLookupResults().getBytes()));
        return resultTable;
    }

    /**
     * Returns a list of BOs that were selected. This implementation makes an attempt to retrieve all BOs with the given object IDs,
     * unless they have been deleted or the object ID changed. Since data may have changed since the search, the returned BOs may
     * not match the criteria used to search.
     * 
     * @see org.kuali.rice.kns.lookup.LookupResultsService#retrieveSelectedResultBOs(java.lang.String, java.lang.Class,
     *      java.lang.String)
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Class boClass, String personId) throws Exception {
        Set<String> setOfSelectedObjIds = retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, personId);
        return retrieveSelectedResultBOs(lookupResultsSequenceNumber, setOfSelectedObjIds, boClass, personId);
    }

    /**
     * @param lookupResultsSequenceNumber
     * @param personId
     * @return Set<String>
     */
    public Set<String> retrieveSetOfSelectedObjectIds(String lookupResultsSequenceNumber, String personId) throws Exception {
        SelectedObjectIds selectedObjectIds = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (!isAuthorizedToAccessSelectedObjectIds(selectedObjectIds, personId)) {
            throw new AuthorizationException(personId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        Set<String> retval = LookupUtils.convertStringOfObjectIdsToSet(selectedObjectIds.getSelectedObjectIds());
        return retval;
    }

    /**
     * @param lookupResultsSequenceNumber
     * @param setOfSelectedObjIds
     * @param boClass
     * @param personId
     * @return Collection<PersistableBusinessObject>
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Set<String> setOfSelectedObjIds, Class boClass, String personId) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving results for class " + boClass + " with objectIds " + setOfSelectedObjIds);
        }
        if (setOfSelectedObjIds.isEmpty()) {
            // OJB throws exception if querying on empty set
            return new ArrayList<PersistableBusinessObject>();
        }
        Map<String, Collection<String>> queryCriteria = new HashMap<String, Collection<String>>();
        queryCriteria.put(KFSPropertyConstants.OBJECT_ID, setOfSelectedObjIds);
        return getBusinessObjectService().findMatching(boClass, queryCriteria);
    }

}

