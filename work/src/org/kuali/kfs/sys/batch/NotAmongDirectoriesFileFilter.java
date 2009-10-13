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
