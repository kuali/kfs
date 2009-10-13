/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.document.web.struts;

import java.io.Serializable;

import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;

/**
 * This represents a Group Holder which contains correction change and criteria objects
 */
public class GroupHolder implements Serializable {
    private CorrectionChange correctionChange = new CorrectionChange();
    private CorrectionCriteria correctionCriteria = new CorrectionCriteria();

    public CorrectionChange getCorrectionChange() {
        return correctionChange;
    }

    public void setCorrectionChange(CorrectionChange correctionChange) {
        this.correctionChange = correctionChange;
    }

    public CorrectionCriteria getCorrectionCriteria() {
        return correctionCriteria;
    }

    public void setCorrectionCriteria(CorrectionCriteria correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }
}
