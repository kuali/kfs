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
package org.kuali.kfs.gl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.LookupResults;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implementation of GlCorrectionProcessOriginEntryService uses the database to temporarily store lists of origin entries.
 * While this implementation does not clear out persisted origin entries, the batch job defined using the
 * org.kuali.kfs.sys.batch.PurgeOldLookupResultsStep class may cause the purging of origin entries persisted with this implementation.
 * 
 * @see GlCorrectionProcessOriginEntryService
 */
@Transactional
public class GlCorrectionProcessOriginEntryServiceImpl implements GlCorrectionProcessOriginEntryService {

    private BusinessObjectService businessObjectService;

    /**
     * Persists the origin entries under a given sequence number. If entries are persisted again under the same sequence number,
     * then they will be overridden.
     * 
     * @param glcpSearchResuiltsSequenceNumber a sequence number
     * @param allEntries a list of origin entries
     * @throws Exception thrown if anything goes wrong
     * @see org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService#persistAllEntries(java.lang.String, java.util.List)
     */
    public void persistAllEntries(String glcpSearchResuiltsSequenceNumber, List<OriginEntryFull> allEntries) throws Exception {
        String serializedOriginEntries = new String(Base64.encodeBase64(ObjectUtils.toByteArray(allEntries)));

        LookupResults lookupResults = retrieveGlcpAllOriginEntries(glcpSearchResuiltsSequenceNumber);
        if (lookupResults == null) {
            lookupResults = new LookupResults();
            lookupResults.setLookupResultsSequenceNumber(glcpSearchResuiltsSequenceNumber);
            lookupResults.setLookupPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        lookupResults.setLookupDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        lookupResults.setSerializedLookupResults(serializedOriginEntries);
        businessObjectService.save(lookupResults);
    }

    /**
     * Retrieves the origin entries stored under the given sequence number
     * 
     * @param glcpSearchResuiltsSequenceNumber a sequence number
     * @return a list of origin entries, or null if no results are currently not in the system.
     * @throws Exception thrown if something goes wrong - vague documentation for a vague exception
     * @see org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService#retrieveAllEntries(java.lang.String)
     */
    public List<OriginEntryFull> retrieveAllEntries(String glcpSearchResuiltsSequenceNumber) throws Exception {
        LookupResults lookupResults = retrieveGlcpAllOriginEntries(glcpSearchResuiltsSequenceNumber);
        if (lookupResults == null) {
            return null;
        }
        List<OriginEntryFull> allOEs = (List<OriginEntryFull>) ObjectUtils.fromByteArray(Base64.decodeBase64(lookupResults.getSerializedLookupResults().getBytes()));
        return allOEs;
    }

    protected LookupResults retrieveGlcpAllOriginEntries(String glcpSearchResuiltsSequenceNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, glcpSearchResuiltsSequenceNumber);
        LookupResults gaoe = (LookupResults) getBusinessObjectService().findByPrimaryKey(LookupResults.class, criteria);
        return gaoe;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

