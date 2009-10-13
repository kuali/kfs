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

import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class KualiBatchJobModifyForm extends KualiForm {
// TODO temporary hack - all input field should be added to this - see KFSMI-2184
    private BatchJobStatus job;

    public BatchJobStatus getJob() {
        return job;
    }

    public void setJob(BatchJobStatus job) {
        this.job = job;
    }
}
