/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.context;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.config.BaseModuleConfigurer;
import org.kuali.rice.core.lifecycle.Lifecycle;

public class KFSConfigurer extends BaseModuleConfigurer {

    public KFSConfigurer() {
        super( "KFS" );
        VALID_RUN_MODES.remove( EMBEDDED_RUN_MODE );
        VALID_RUN_MODES.remove( REMOTE_RUN_MODE );
    }
    
    /**
     * Prevents the loading of an OJB file
     * @see org.kuali.rice.core.config.BaseModuleConfigurer#loadLifecycles()
     */
    @Override
    protected List<Lifecycle> loadLifecycles() throws Exception {
        List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
        return lifecycles;
    }
}
