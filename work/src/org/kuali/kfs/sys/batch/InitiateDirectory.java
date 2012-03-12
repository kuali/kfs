/*
 * Copyright 2011 The Kuali Foundation
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

import java.util.List;

/**
 * Interface to initiate required directories for batch jobs or service impl 
 * 
 */
public interface InitiateDirectory {
    
    /**
     * Prepare the directories needed base on the input directory paths
     *  
     * @param directoryPaths
     */
    public void prepareDirectories(List<String>directoryPaths);
    
    /**
     * Get the required list of directories name (full path)
     * 
     * @return
     */
    public List<String> getRequiredDirectoryNames();
}
