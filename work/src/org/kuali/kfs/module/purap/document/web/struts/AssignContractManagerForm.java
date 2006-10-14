/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/web/struts/AssignContractManagerForm.java,v $
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
package org.kuali.module.purap.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.struts.form.KualiForm;

/**
 * This class is the form class for the assign a contract manager.
 * 
 */
public class AssignContractManagerForm extends KualiForm {

    List requisitions;
    
    /**
     * Constructs a AssignContractManagerForm instance 
     */
    public AssignContractManagerForm() {
        super();
        requisitions = new ArrayList();
    }


}