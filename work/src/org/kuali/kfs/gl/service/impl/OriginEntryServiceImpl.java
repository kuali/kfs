/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryServiceImpl.java,v 1.11 2006-04-28 16:16:16 larevans Exp $
 */
public class OriginEntryServiceImpl implements OriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;

    /**
     * 
     * @param originEntryDao
     */
    public void setOriginEntryDao(OriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }

    /**
     * 
     * @param originEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * 
     */
    public OriginEntryServiceImpl() {
        super();
    }

    /**
     * @param originEntryGroup
     */
    public Iterator getEntriesByGroup(OriginEntryGroup originEntryGroup) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put("group.id", originEntryGroup.getId());
        return originEntryDao.getMatchingEntries(criteria);
    }

    /**
     * @param originEntryGroup
     * @param documentNumber
     * @param documentTypeCode
     * @param originCode
     */
    public Iterator getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode,
            String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put("entryGroupId", originEntryGroup.getId());
        criteria.put("financialDocumentNumber", documentNumber);
        criteria.put("financialDocumentTypeCode", documentTypeCode);
        criteria.put("financialSystemOriginationCode", originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    /**
     * @param transaction
     * @param originEntryGroup
     */
    public void createEntry(Transaction transaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        OriginEntry e = new OriginEntry(transaction);
        e.setGroup(originEntryGroup);

        originEntryDao.saveOriginEntry(e);
    }

    /**
     * @param filename
     * @param groupSourceCode
     * @param isValid
     * @param isProcessed
     * @param isScrub
     */
    public void loadFlatFile(String filename, String groupSourceCode, boolean isValid, boolean isProcessed, boolean isScrub) {
        LOG.debug("loadFlatFile() started");

        java.sql.Date groupDate = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newGroup = originEntryGroupService.createGroup(groupDate, groupSourceCode, isValid, isProcessed, isScrub);

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = input.readLine()) != null) {
                OriginEntry entry = new OriginEntry(line);
                createEntry(entry, newGroup);
            }
        } catch (Exception ex) {
            LOG.error("performStep() Error reading file", ex);
            throw new IllegalArgumentException("Error reading file");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                LOG.error("loadFlatFile() error closing file.", ex);
            }
        }
    }

    /**
     * @param validGroup
     * @param errorGroup
     * @param expiredGroup
     * @param documentNumber
     * @param documentTypeCode
     * @param originCode
     */
    public void removeScrubberDocumentEntries(OriginEntryGroup validGroup, OriginEntryGroup errorGroup,
            OriginEntryGroup expiredGroup, String documentNumber, String documentTypeCode, String originCode) {

        Map criteria = new HashMap();

        criteria.put("financialDocumentNumber", documentNumber);
        criteria.put("financialDocumentTypeCode", documentTypeCode);
        criteria.put("financialSystemOriginationCode", originCode);
        criteria.put("entryGroupId", validGroup.getId());
        originEntryDao.deleteMatchingEntries(criteria);

        criteria = new HashMap();
        criteria.put("financialDocumentNumber", documentNumber);
        criteria.put("financialDocumentTypeCode", documentTypeCode);
        criteria.put("financialSystemOriginationCode", originCode);
        criteria.put("entryGroupId", errorGroup.getId());
        originEntryDao.deleteMatchingEntries(criteria);

        criteria = new HashMap();
        criteria.put("financialDocumentNumber", documentNumber);
        criteria.put("financialDocumentTypeCode", documentTypeCode);
        criteria.put("financialSystemOriginationCode", originCode);
        criteria.put("entryGroupId", expiredGroup.getId());
        originEntryDao.deleteMatchingEntries(criteria);

    }

}
