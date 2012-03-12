/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
