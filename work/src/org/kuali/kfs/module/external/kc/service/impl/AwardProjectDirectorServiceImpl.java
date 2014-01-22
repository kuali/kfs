/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.external.kc.dto.AwardDTO;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class AwardProjectDirectorServiceImpl extends AwardServiceImpl {
    
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Award award = (Award) super.findByPrimaryKey(primaryKeys);
        if (award == null) {
            return null;
        } else {
            return award.getAwardPrimaryProjectDirector();
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        Collection<Award> awards = super.findMatching(fieldValues);
        List<ContractsAndGrantsProjectDirector> directors = new ArrayList<ContractsAndGrantsProjectDirector>();
        for (Award award : awards) {
            directors.add(award.getAwardPrimaryProjectDirector());
        }
        return directors;
    }
}
