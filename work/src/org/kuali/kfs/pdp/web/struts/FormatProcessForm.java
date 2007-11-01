/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.form.format;

import org.apache.struts.action.ActionForm;

/**
 * @author jsissom
 */
public class FormatProcessForm extends ActionForm {
    private Integer procId;
    private String campusCd;

    public FormatProcessForm() {
        super();
    }

    public String getCampusCd() {
        return campusCd;
    }

    public void setCampusCd(String campusCd) {
        this.campusCd = campusCd;
    }

    public Integer getProcId() {
        return procId;
    }

    public void setProcId(Integer procId) {
        this.procId = procId;
    }
}
