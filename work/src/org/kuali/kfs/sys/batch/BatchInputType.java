/*
 * Copyright 2007 The Kuali Foundation
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


/**
 * Objects implementing this interface provide metadata necessary for the batch upload screen to operate properly
 */
public interface BatchInputType {
    /**
     * Obtain the principal id of the user who created the file
     */
    public String getAuthorPrincipalName(File file);
    
    /**
     * Returns the key for the application constants message to display as the title for the upload screen.
     */
    public String getTitleKey();
}

