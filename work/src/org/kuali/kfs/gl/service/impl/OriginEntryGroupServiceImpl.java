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
import java.util.HashMap;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.dao.ojb.OriginEntryGroupDaoOjb;
import org.kuali.module.gl.service.OriginEntryGroupService;

public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {

	public OriginEntryGroupServiceImpl() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @return the List of all origin entry groups that have a process indicator of false.
	 *         collection is returned read-only.
	 */
	public Collection getOriginEntryGroupsPendingProcessing() {
		Map criteria = new HashMap();
		criteria.put("processed", Boolean.FALSE);
		OriginEntryGroupDao dao = new OriginEntryGroupDaoOjb();
		return Collections.unmodifiableCollection(dao.getMatchingGroups(criteria));
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public OriginEntryGroup getOriginEntryGroup(String groupId) {
		OriginEntryGroupDao dao = new OriginEntryGroupDaoOjb();
		Map criteria = new HashMap();
		criteria.put("ORIGIN_ENTRY_GRP_ID", groupId);
		Collection matches = dao.getMatchingGroups(criteria);
		if(null != matches) {
			return (OriginEntryGroup) matches.iterator().next();
		}
		return null;
	}

}
