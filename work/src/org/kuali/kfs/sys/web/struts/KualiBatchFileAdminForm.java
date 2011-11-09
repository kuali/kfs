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
package org.kuali.kfs.sys.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.KRADConstants;

public class KualiBatchFileAdminForm extends KualiForm {
    private String filePath;

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        if (StringUtils.isBlank(getFilePath())&& 
                StringUtils.isNotBlank(request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME)) &&
                StringUtils.isNotBlank(request.getParameter(KRADConstants.QUESTION_CONTEXT))) {
            setFilePath(request.getParameter(KRADConstants.QUESTION_CONTEXT));
        }
        
        if (filePath != null && filePath.matches(".*\\.\\.[/\\\\].*")) {
            throw new RuntimeException("Cannot access parent directory");
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
