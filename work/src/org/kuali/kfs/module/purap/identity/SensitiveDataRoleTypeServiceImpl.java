/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.identity;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SensitiveDataRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        return Arrays.asList(qualification.get(PurapKimAttributes.SENSITIVE_DATA_CODE).split(";")).contains(roleQualifier.get(PurapKimAttributes.SENSITIVE_DATA_CODE));
    }
}
