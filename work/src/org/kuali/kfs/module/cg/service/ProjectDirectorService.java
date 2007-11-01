/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
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
package org.kuali.module.cg.service;

import org.kuali.module.cg.bo.ProjectDirector;

/**
 * Services for ProjectDirectors
 */
public interface ProjectDirectorService {

    /**
     * Finds a ProjectDirector by username. That's a secondary key to UniversalUser, used to get the ProjectDirector's primary key,
     * the universal user ID number.
     * 
     * @param username the person user identifier of the ProjectDirector to get
     * @return the corresponding ProjectDirector, or null if none
     */
    public ProjectDirector getByPersonUserIdentifier(String username);

    /**
     * Finds a ProjectDirector by universal user ID number. That's the primary key to ProjectDirector, and coincidentally to
     * UniversalUser too.
     * 
     * @param universalIdentifier the universal user ID number of the ProjectDirector to get
     * @return the corresponding ProjectDirector, or null if none
     */
    public ProjectDirector getByPrimaryId(String universalIdentifier);

    /**
     * Checks for the existence of a ProjectDirector by universal user ID number. That's the primary key to ProjectDirector, and
     * coincidentally to UniversalUser too.
     * 
     * @param universalIdentifier the universal user ID number of the ProjectDirector to get
     * @return whether the corresponding ProjectDirector exists
     */
    public boolean primaryIdExists(String universalIdentifier);
}
