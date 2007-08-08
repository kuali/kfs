/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.cg;

import org.kuali.core.JstlConstants;

/**
 * Constants specific to the Contracts and Grants module.
 */
public class CGConstants extends JstlConstants {

    /**
     * The key for the document error map to grab errors for the close document.
     */
    public static final String CLOSE_DOCUMENT_TAB_ERRORS = "document.close*";
    
    /**
     * The name of the workgroup for Contracts and Grants documents.
     */
    public static final String GROUP_CG_MAINT_EDOCS = "CgMaintenanceEDoc";
}
