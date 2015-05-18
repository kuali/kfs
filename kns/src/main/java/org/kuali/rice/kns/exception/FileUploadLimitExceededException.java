/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.exception;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.exception.KualiException;

/**
 * This class represents an FileUploadLimitExceededException.
 * 
 * 
 */

public class FileUploadLimitExceededException extends KualiException {

	private ActionForm actionForm;
	private ActionMapping actionMapping;
	
    /**
     * Create an FileUploadLimitExceededException with the given message
     * 
     * @param message
     */
    public FileUploadLimitExceededException(String message, ActionForm actionForm, ActionMapping actionMapping) {
        super(message);
        this.actionForm = actionForm;
        this.actionMapping = actionMapping;
    }

    /**
     * Create an FileUploadLimitExceededException with the given message and cause
     * 
     * @param message
     * @param cause
     */
    public FileUploadLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @return the actionForm
	 */
	public ActionForm getActionForm() {
		return this.actionForm;
	}

	/**
	 * @return the actionMapping
	 */
	public ActionMapping getActionMapping() {
		return this.actionMapping;
	}

}
