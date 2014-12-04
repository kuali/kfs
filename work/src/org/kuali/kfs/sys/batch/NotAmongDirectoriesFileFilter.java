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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * A file filter which does not allow any files through which belong to one of the directories by the CustomAgeFileFilter
 */
public class NotAmongDirectoriesFileFilter implements IOFileFilter {
    private Set<String> directoriesToAvoid;
    
    /**
     * Constructs a NotAmongDirectoriesFileFilter which will avoid any of the directories associated with the given custom ages
     * @param customAges the custom ages to avoid directories of
     */
    public NotAmongDirectoriesFileFilter(List<FilePurgeCustomAge> customAges) {
        directoriesToAvoid = new HashSet<String>();
        for (FilePurgeCustomAge customAge : customAges) {
            directoriesToAvoid.add(customAge.getDirectory());
        }
    }

    /**
     * 
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        return !directoriesToAvoid.contains(file.getParent());
    }

    /**
     * 
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File directory, String fileName) {
        File file = new File(directory.getName()+File.separator+fileName);
        if (file.isDirectory()) return true;
        return !directoriesToAvoid.contains(directory.getName());
    }

}
