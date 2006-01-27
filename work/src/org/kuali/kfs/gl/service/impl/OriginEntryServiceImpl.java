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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.ojb.OriginEntryDaoOjb;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * @author jsissom
 *
 */
public class OriginEntryServiceImpl implements OriginEntryService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

  private OriginEntryDao originEntryDao;

  /**
   * 
   */
  public OriginEntryServiceImpl() {
    super();
    originEntryDao = new OriginEntryDaoOjb();
  }

  public Collection getGroupsToPost(Date postDate) {
    LOG.debug("getGroupsToPost() started");
    
    return originEntryDao.getPosterGroups(postDate,OriginEntrySource.SCRUBBER_VALID);
  }

  public Collection getIcrGroupsToPost(Date postDate) {
    LOG.debug("getIcrGroupsToPost() started");

    return originEntryDao.getPosterGroups(postDate,OriginEntrySource.ICR_POSTER_VALID);
  }

  public Collection getGroupsToScrub(Date scrubDate) {
    LOG.debug("getGroupsToScrub() started");

    return originEntryDao.getScrubberGroups(scrubDate);
  }

  public Iterator getEntriesByGroup(OriginEntryGroup oeg) {
    LOG.debug("getEntriesByGroup() started");

    Map criteria = new HashMap();
    criteria.put("group.id",oeg.getId());
    return originEntryDao.getMatchingEntries(criteria);
  }

  public OriginEntryGroup createGroup(java.util.Date date,String sourceCode, boolean valid, boolean processed, boolean scrub) {
    LOG.debug("createGroup() started");

    OriginEntryGroup oeg = new OriginEntryGroup();
    oeg.setDate(new java.sql.Date(date.getTime()));
    oeg.setProcess(new Boolean(processed));
    oeg.setScrub(new Boolean(scrub));
    oeg.setSourceCode(sourceCode);
    oeg.setValid(new Boolean(valid));

    originEntryDao.saveGroup(oeg);

    return oeg;
  }

  public void createEntry(Transaction tran,OriginEntryGroup group) {
    LOG.debug("createEntry() started");

    OriginEntry e = new OriginEntry(tran);
    e.setGroup(group);

    originEntryDao.saveOriginEntry(e);
  }

  public void setOriginEntryDao(OriginEntryDao oed) {
    originEntryDao = oed;
  }

  public void loadFlatFile(String filename,String groupSourceCode,boolean valid,boolean processed,boolean scrub) {
    LOG.debug("loadFlatFile() started");

    java.util.Date groupDate = new java.util.Date();
    OriginEntryGroup newGroup = createGroup(groupDate,groupSourceCode,valid,processed,scrub);

    BufferedReader input = null;
    try {
      input = new BufferedReader( new FileReader(filename) );
      String line = null;
      while (( line = input.readLine()) != null) {
        Entry entry = new Entry(line);
        createEntry(entry,newGroup);
      }
    } catch (Exception ex) {
      LOG.error("performStep() Error reading file", ex);
      throw new IllegalArgumentException("Error reading file");
    } finally {
      try {
        if ( input != null ) {
          input.close();
        }
      } catch (IOException ex) { }
    }
  }
}
