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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntryLite;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryLiteService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OriginEntryLiteServiceImpl implements OriginEntryLiteService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryLiteServiceImpl.class);

    private OriginEntryDao originEntryDao;

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";

    public Iterator<OriginEntryLite> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByDocument() started");

        Map criteria = new HashMap();
        criteria.put(ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.put(FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    public Iterator<OriginEntryLite> getEntriesByGroup(OriginEntryGroup originEntryGroup) {
        LOG.debug("getEntriesByGroup() started");

        return originEntryDao.getEntriesByGroup(originEntryGroup, OriginEntryDao.SORT_DOCUMENT);
    }

    /**
     * Gets the originEntryDao attribute.
     * 
     * @return Returns the originEntryDao.
     */
    public OriginEntryDao getOriginEntryDao() {
        return originEntryDao;
    }

    /**
     * Sets the originEntryDao attribute value.
     * 
     * @param originEntryDao The originEntryDao to set.
     */
    public void setOriginEntryDao(OriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }

    public void save(OriginEntryLite entry) {
        LOG.debug("save() started");
        originEntryDao.saveOriginEntry(entry);
    }

    public void delete(OriginEntryLite entry) {
        LOG.debug("delete() started");
        originEntryDao.deleteEntry(entry);
    }
}
