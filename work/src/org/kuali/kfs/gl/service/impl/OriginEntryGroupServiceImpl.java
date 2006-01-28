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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * @author Laran Evans <lc278@cs.cornell.edu>
 * @version $Id: OriginEntryGroupServiceImpl.java,v 1.6 2006-01-28 16:03:34 jsissom Exp $
 * 
 */
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);
	private OriginEntryGroupDao originEntryGroupDao;

	public OriginEntryGroupServiceImpl() {
		super();
	}

  public void setOriginEntryGroupDao(OriginEntryGroupDao oegd) {
    originEntryGroupDao = oegd;
  }

	/**
	 * 
	 * @return the List of all origin entry groups that have a process indicator of false.
	 *         collection is returned read-only.
	 */
	public Collection getOriginEntryGroupsPendingProcessing() {
		Map criteria = new HashMap();
		criteria.put("process", Boolean.FALSE);
		return Collections.unmodifiableCollection(originEntryGroupDao.getMatchingGroups(criteria));
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public OriginEntryGroup getOriginEntryGroup(String groupId) {
		Map criteria = new HashMap();
		criteria.put("entryGroupId", groupId);
		Collection matches = originEntryGroupDao.getMatchingGroups(criteria);
    Iterator i = matches.iterator();
    if ( i.hasNext() ) {
			return (OriginEntryGroup)i.next();
		}
		return null;
	}


	  public OriginEntryGroup createGroup(java.util.Date date,String sourceCode, boolean valid, boolean process, boolean scrub) {
	    LOG.debug("createGroup() started");

	    OriginEntryGroup oeg = new OriginEntryGroup();
	    oeg.setDate(new java.sql.Date(date.getTime()));
	    oeg.setProcess(new Boolean(process));
	    oeg.setScrub(new Boolean(scrub));
	    oeg.setSourceCode(sourceCode);
	    oeg.setValid(new Boolean(valid));

	    originEntryGroupDao.saveGroup(oeg);

	    return oeg;
	  }

	  public Collection getGroupsToPost(Date postDate) {
	    LOG.debug("getGroupsToPost() started");
	    
	    return originEntryGroupDao.getPosterGroups(postDate,OriginEntrySource.SCRUBBER_VALID);
	  }

	  public Collection getIcrGroupsToPost(Date postDate) {
	    LOG.debug("getIcrGroupsToPost() started");

	    return originEntryGroupDao.getPosterGroups(postDate,OriginEntrySource.ICR_POSTER_VALID);
	  }

	  public Collection getGroupsToScrub(Date scrubDate) {
	    LOG.debug("getGroupsToScrub() started");

	    return originEntryGroupDao.getScrubberGroups(scrubDate);
	  }

    public void save(OriginEntryGroup group) {
      LOG.debug("save() started");

      originEntryGroupDao.saveGroup(group);
    }
}
