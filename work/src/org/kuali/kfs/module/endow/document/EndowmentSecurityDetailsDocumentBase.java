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

import org.apache.cxf.common.util.StringUtils;
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
        sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();;
        targetTransactionSecurity = new EndowmentTargetTransactionSecurity();;
        sourceTransactionSecurities = new TypedArrayList(EndowmentSourceTransactionSecurity.class);
        targetTransactionSecurities = new TypedArrayList(EndowmentTargetTransactionSecurity.class);
    }

    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
        
        //A Hack to insert transaction securities in the securities collection.
        if( !StringUtils.isEmpty(sourceTransactionSecurity.getSecurityID()) )
        {
            getSourceTransactionSecurities().add(0,sourceTransactionSecurity);
        }

        //A Hack to insert transaction securities in the securities collection.
        if( !StringUtils.isEmpty(targetTransactionSecurity.getSecurityID()) )
        {
            getTargetTransactionSecurities().add(0,targetTransactionSecurity);        
        }

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
    
    /**
     * Here when the document is being read from the DB, the security object to be retruned must be the object from the DB and not the new object created. 
     * 
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#getSourceTransactionSecurity()
     */
    public EndowmentTransactionSecurity getSourceTransactionSecurity() 
    {
        if(this.sourceTransactionSecurities.size() > 0)
        {
            return this.sourceTransactionSecurities.get(0);
        }
        else 
            return this.sourceTransactionSecurity;
   }

    /**
     * Here when the document is being read from the DB, the security object to be retruned must be the object from the DB and not the new object created.
     * 
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#getTargetTransactionSecurity()
     */
    public EndowmentTransactionSecurity getTargetTransactionSecurity() 
    {
        if(this.targetTransactionSecurities.size() > 0)
        {
            return this.targetTransactionSecurities.get(0);
        }
        else 
            return this.targetTransactionSecurity; 
    }
    
    public void setSourceTransactionSecurity(EndowmentTransactionSecurity sourceTransactionSecurity) 
    {
        this.sourceTransactionSecurity = (EndowmentSourceTransactionSecurity)sourceTransactionSecurity;
    }
    
    public void setTargetTransactionSecurity(EndowmentTransactionSecurity targetTransactionSecurity) 
    {
        this.targetTransactionSecurity = (EndowmentTargetTransactionSecurity)targetTransactionSecurity;
    }
}
