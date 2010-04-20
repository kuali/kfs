/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.rice.kns.util.TypedArrayList;


public abstract class EndowmentSecurityDetailsDocumentBase extends EndowmentTransactionLinesDocumentBase implements EndowmentSecurityDetailsDocument 
{
    private List<EndowmentTransactionSecurity> sourceTransactionSecurities;
    private List<EndowmentTransactionSecurity> targetTransactionSecurities;
    
    private EndowmentSourceTransactionSecurity sourceTransactionSecurity;
    private EndowmentTargetTransactionSecurity targetTransactionSecurity;
    
    public EndowmentSecurityDetailsDocumentBase()
    {
        super();
        sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        sourceTransactionSecurities = new TypedArrayList(EndowmentSourceTransactionSecurity.class);
        targetTransactionSecurities = new TypedArrayList(EndowmentTargetTransactionSecurity.class);
    }

    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
        //Set the fdoc # for transaction securities
        getSourceTransactionSecurity().setDocumentNumber(getDocumentHeader().getDocumentNumber());
        getTargetTransactionSecurity().setDocumentNumber(getDocumentHeader().getDocumentNumber());
        
        //A Hack to insert transaction securities in the securities collection.
        if(getSourceTransactionSecurity().getSecurityID() != null)
            setSourceTransactionSecurity(getSourceTransactionSecurity());

        //A Hack to insert transaction securities in the securities collection.
        if(getTargetTransactionSecurity().getSecurityID() != null)
            setTargetTransactionSecurity(getTargetTransactionSecurity());

    }
    
    public List<EndowmentTransactionSecurity> getSourceTransactionSecurities() {
        return sourceTransactionSecurities;
    }
    public void setSourceTransactionSecurities(List<EndowmentTransactionSecurity> sourceTransactionSecurities) {
        this.sourceTransactionSecurities = sourceTransactionSecurities;
    }
    public List<EndowmentTransactionSecurity> getTargetTransactionSecurities() {
        return targetTransactionSecurities;
    }
    public void setTargetTransactionSecurities(List<EndowmentTransactionSecurity> targetTransactionSecurities) {
        this.targetTransactionSecurities = targetTransactionSecurities;
    }
    
    public EndowmentTransactionSecurity getSourceTransactionSecurity() 
    {
        return sourceTransactionSecurity;
    }

    public EndowmentTransactionSecurity getTargetTransactionSecurity() 
    {
        return targetTransactionSecurity;
    }
    
    public void setSourceTransactionSecurity(EndowmentTransactionSecurity sourceTransactionSecurity) 
    {
        this.sourceTransactionSecurity = (EndowmentSourceTransactionSecurity)sourceTransactionSecurity;
        this.sourceTransactionSecurities.set(0, sourceTransactionSecurity) ;
    }
    
    public void setTargetTransactionSecurity(EndowmentTransactionSecurity targetTransactionSecurity) 
    {
        this.targetTransactionSecurity = (EndowmentTargetTransactionSecurity)targetTransactionSecurity;
        this.targetTransactionSecurities.set(0, targetTransactionSecurity) ;
    }
}
