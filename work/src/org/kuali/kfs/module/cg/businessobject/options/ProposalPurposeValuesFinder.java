/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/options/ProposalPurposeValuesFinder.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License")
;
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
package org.kuali.module.cg.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.cg.bo.ProposalPurpose;


public class ProposalPurposeValuesFinder extends KeyValuesBase {
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        Collection codes = boService.findAll(ProposalPurpose.class);

        List sortList = (List) codes;

//        // calling comparator.
//        AccountTypeCodeComparator accTypeCodeComparator = new AccountTypeCodeComparator();
//
//        // sort using comparator.
//        Collections.sort(sortList, accTypeCodeComparator);
//

        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));


        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            ProposalPurpose proposalPurpose = (ProposalPurpose) iter.next();

            labels.add(new KeyLabelPair(proposalPurpose.getProposalPurposeCode(), proposalPurpose.getProposalPurposeCode()+"-"+proposalPurpose.getProposalPurposeDescription()));
        }

        return labels;
    }



}
