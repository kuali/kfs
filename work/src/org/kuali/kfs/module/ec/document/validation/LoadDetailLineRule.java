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
package org.kuali.module.effort.rule;

import org.kuali.core.rule.BusinessRule;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;

public interface LoadDetailLineRule<E extends EffortCertificationDocument> extends BusinessRule {

    /**
     * validate the given effort certification document before a set of detail lines can be added into the given document
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return true if all rules are valid; otherwise, false;
     */
    public boolean processLoadDetailLineRules(E effortCertificationDocument);
}