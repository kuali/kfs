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
package org.kuali.module.gl.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntry;

/**
 * @author jsissom
 * @version $Id: OriginEntryDao.java,v 1.4 2006-02-15 03:29:49 jsissom Exp $
 */
public interface OriginEntryDao {
    public Iterator getMatchingEntries(Map searchCriteria);
    public void deleteMatchingEntries(Map searchCriteria);
	public void saveOriginEntry(OriginEntry entry);

    /**
     * This method should only be used in unit tests.  It loads all the 
     * gl_origin_entry_t rows in memory into a collection.  This won't 
     * sace for production.
     * 
     * @return
     */
    public Collection testingGetAllEntries();
}
