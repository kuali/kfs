/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo.codes;

import java.util.List;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.KualiCodeBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionType;

/**
 * This class...
 */
public class ICRTypeCode extends KualiCodeBase implements Inactivateable {
    
    private List indirectCostRecoveryExclusionTypeCollection;

    public ICRTypeCode () {
        indirectCostRecoveryExclusionTypeCollection = new TypedArrayList(IndirectCostRecoveryExclusionType.class);
    }

    public List getIndirectCostRecoveryExclusionTypeCollection() {
        return indirectCostRecoveryExclusionTypeCollection;
    }

    public void setIndirectCostRecoveryExclusionTypeCollection(List indirectCostRecoveryExclusionTypeCollection) {
        this.indirectCostRecoveryExclusionTypeCollection = indirectCostRecoveryExclusionTypeCollection;
    }
    
}
