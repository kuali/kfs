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
package org.kuali.kfs.module.cg.identity;

import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class CgKimAttributes extends KfsKimAttributes {
    public static final String RESEARCH_RISK_TYPE_CODE = "researchRiskTypeCode";

    protected String researchRiskTypeCode;

    protected ResearchRiskType researchRiskType;
    
    
    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }
    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
    }
   
    public ResearchRiskType getResearchRiskType() {
        return researchRiskType;
    }
    public void setResearchRiskType(ResearchRiskType researchRiskType) {
        this.researchRiskType = researchRiskType;
    }
}
