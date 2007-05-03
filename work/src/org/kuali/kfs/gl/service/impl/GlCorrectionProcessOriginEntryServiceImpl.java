/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.kuali.core.bo.LookupResults;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.service.GlCorrectionProcessOriginEntryService;
import org.springframework.transaction.annotation.Transactional;
import sun.font.GlyphLayout.GVData;

/**
 * This implementation of GlCorrectionProcessOriginEntryService uses the database to temporarily store lists of origin entries.
 * While this implementation does not clear out persisted origin entries, the batch job defined using the 
 * org.kuali.kfs.batch.PurgeOldLookupResultsStep class may cause the purging of origin entries persisted with this implementation.
 * 
 * @see GlCorrectionProcessOriginEntryService
 */
@Transactional
public class GlCorrectionProcessOriginEntryServiceImpl implements GlCorrectionProcessOriginEntryService {
    
    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.module.gl.service.GlCorrectionProcessOriginEntryService#persistAllEntries(java.lang.String, java.util.List)
     */
    public void persistAllEntries(String glcpSearchResuiltsSequenceNumber, List<OriginEntry> allEntries) throws Exception {
        String serializedOriginEntries = new String(Base64.encodeBase64(ObjectUtils.toByteArray(allEntries)));
        
        LookupResults lookupResults = retrieveGlcpAllOriginEntries(glcpSearchResuiltsSequenceNumber);
        if (lookupResults == null) {
            lookupResults = new LookupResults();
            lookupResults.setLookupResultsSequenceNumber(glcpSearchResuiltsSequenceNumber);
            lookupResults.setLookupUniversalUserId(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        }
        lookupResults.setLookupDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
        lookupResults.setSerializedLookupResults(serializedOriginEntries);
        businessObjectService.save(lookupResults);
    }

    /**
     * @see org.kuali.module.gl.service.GlCorrectionProcessOriginEntryService#retrieveAllEntries(java.lang.String)
     */
    public List<OriginEntry> retrieveAllEntries(String glcpSearchResuiltsSequenceNumber) throws Exception {
        LookupResults lookupResults = retrieveGlcpAllOriginEntries(glcpSearchResuiltsSequenceNumber);
        if (lookupResults == null) {
            return null;
        }
        List<OriginEntry> allOEs = (List<OriginEntry>) ObjectUtils.fromByteArray(Base64.decodeBase64(lookupResults.getSerializedLookupResults().getBytes()));
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
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
