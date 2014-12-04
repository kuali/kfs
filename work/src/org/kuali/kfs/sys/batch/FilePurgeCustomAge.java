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
package org.kuali.kfs.sys.batch;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * A customized age for a file prefix.
 */
public class FilePurgeCustomAge {

    private String directory;
    private String parameterPrefix;
    
    /**
     * Gets the directory attribute. 
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }
    /**
     * Sets the directory attribute value.
     * @param directory The directory to set.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    /**
     * Gets the parameterPrefix attribute. 
     * @return Returns the parameterPrefix.
     */
    public String getParameterPrefix() {
        return parameterPrefix;
    }
    /**
     * Sets the parameterPrefix attribute value.
     * @param parameterPrefix The parameterPrefix to set.
     */
    public void setParameterPrefix(String parameterPrefix) {
        this.parameterPrefix = parameterPrefix;
    }
    
    /**
     * @return an IOFileFilter which represents the files that should be culled by this FilePurgeCustomAge
     */
    public IOFileFilter getFileFilter() {
        AndFileFilter andFileFilter = new AndFileFilter();
        MaxAgePurgeFileFilter maxAgeFilter = new MaxAgePurgeFileFilter(this);
        DirectoryNameFileFilter directoryNameFilter = new DirectoryNameFileFilter(this);
        andFileFilter.addFileFilter(maxAgeFilter);
        andFileFilter.addFileFilter(directoryNameFilter);
        return andFileFilter;
    }
    
}
