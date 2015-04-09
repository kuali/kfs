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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PostProcessorServiceImpl extends org.kuali.rice.krad.service.impl.PostProcessorServiceImpl {

    /**
     * Overrides the baseline method so that the KFS document postprocessor uses
     * the "kfs" user instead of the "kr" user.
     * 
     * @see org.kuali.rice.krad.service.impl.PostProcessorServiceImpl#establishPostProcessorUserSession()
     */
    @Override
    protected UserSession establishPostProcessorUserSession() throws WorkflowException {
        if (GlobalVariables.getUserSession() == null) {
            return new UserSession(KFSConstants.SYSTEM_USER);
        } else {
            return GlobalVariables.getUserSession();
        }
    }
    
}
