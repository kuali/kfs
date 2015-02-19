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
package org.kuali.kfs.sys.context;


import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * A file filter which only returns any directory or any files with the given extension
 */
public class FileExtensionFileFilter implements IOFileFilter {
    private String suffix;
    
    /**
     * Constructs a FileExtensionFileFilter, using the extension to filter the files
     */
    public FileExtensionFileFilter(String extension) {
    	this.suffix = extension;
    }
    
    /**
     * Accepts any directory and any file with the given extension
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        return (file.getName().endsWith(suffix));
    }

    /**
     * Accepts any directory, and any file with the given extension
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File directory, String fileName) {
        final File file = new File(directory.getName()+File.separator+fileName);
        if (file.isDirectory()) return true;
        return (file.getName().endsWith(suffix));
    }

}
