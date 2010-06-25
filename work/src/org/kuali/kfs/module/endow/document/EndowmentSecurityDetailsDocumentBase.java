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

import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.rice.kns.util.TypedArrayList;


public abstract class EndowmentSecurityDetailsDocumentBase extends EndowmentTransactionLinesDocumentBase implements EndowmentSecurityDetailsDocument {
    private List<EndowmentTransactionSecurity> sourceTransactionSecurities;
    private List<EndowmentTransactionSecurity> targetTransactionSecurities;

    private EndowmentSourceTransactionSecurity sourceTransactionSecurity;
    private EndowmentTargetTransactionSecurity targetTransactionSecurity;

    public EndowmentSecurityDetailsDocumentBase() {
        super();
        sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        sourceTransactionSecurities = new TypedArrayList(EndowmentSourceTransactionSecurity.class);
        targetTransactionSecurities = new TypedArrayList(EndowmentTargetTransactionSecurity.class);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();
        sourceTransactionSecurities.clear();
        targetTransactionSecurities.clear();

        // functionality specific to the EndowmentUnitShareAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI allows
        // the user to enter a source security detail by default. This is adjusted before save so that the right security is saved
        // in the DB.
        if (this instanceof EndowmentUnitShareAdjustmentDocument) {

            if (!StringUtils.isEmpty(sourceTransactionSecurity.getSecurityID())) {

                if (this.getSourceTransactionLines() != null && this.getSourceTransactionLines().size() > 0) {
                    getSourceTransactionSecurities().add(0, sourceTransactionSecurity);
                }
                else if (this.getTargetTransactionLines() != null && this.getTargetTransactionLines().size() > 0) {
                    targetTransactionSecurity.setSecurityID(sourceTransactionSecurity.getSecurityID());
                    targetTransactionSecurity.setRegistrationCode(sourceTransactionSecurity.getRegistrationCode());
                    getTargetTransactionSecurities().add(0, targetTransactionSecurity);
                }
            }
        }
        else {

            // A Hack to insert transaction securities in the securities collection.
            if (!StringUtils.isEmpty(sourceTransactionSecurity.getSecurityID())) {
                getSourceTransactionSecurities().add(0, sourceTransactionSecurity);
            }

            // A Hack to insert transaction securities in the securities collection.
            if (!StringUtils.isEmpty(targetTransactionSecurity.getSecurityID())) {
                getTargetTransactionSecurities().add(0, targetTransactionSecurity);
            }
        }

    }

    /**
     * Gets the sourceTransactionSecurities.
     * 
     * @return sourceTransactionSecurities
     */
    public List<EndowmentTransactionSecurity> getSourceTransactionSecurities() {
        return sourceTransactionSecurities;
    }

    /**
     * Sets the sourceTransactionSecurities.
     * 
     * @param sourceTransactionSecurities
     */
    public void setSourceTransactionSecurities(List<EndowmentTransactionSecurity> sourceTransactionSecurities) {
        this.sourceTransactionSecurities = sourceTransactionSecurities;
    }

    /**
     * Gets the targetTransactionSecurities.
     * 
     * @return targetTransactionSecurities
     */
    public List<EndowmentTransactionSecurity> getTargetTransactionSecurities() {
        return targetTransactionSecurities;
    }

    /**
     * Sets the targetTransactionSecurities.
     * 
     * @param targetTransactionSecurities
     */
    public void setTargetTransactionSecurities(List<EndowmentTransactionSecurity> targetTransactionSecurities) {
        this.targetTransactionSecurities = targetTransactionSecurities;
    }

    /**
     * Here when the document is being read from the DB, the security object to be returned must be the object from the DB and not
     * the new object created.
     * 
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#getSourceTransactionSecurity()
     */
    public EndowmentTransactionSecurity getSourceTransactionSecurity() {
        if (this.sourceTransactionSecurities.size() > 0) {
            this.sourceTransactionSecurity = (EndowmentSourceTransactionSecurity) this.sourceTransactionSecurities.get(0);
        }
        // functionality specific to the EndowmentUnitShareAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI
        // display a source security detail by default so this code will return the target security saved to be displayed on the
        // source security on the UI.
        else if (this instanceof EndowmentUnitShareAdjustmentDocument && this.targetTransactionSecurities.size() > 0) {
            this.sourceTransactionSecurity.setSecurityID(this.targetTransactionSecurities.get(0).getSecurityID());

            this.sourceTransactionSecurity.setRegistrationCode(this.targetTransactionSecurities.get(0).getRegistrationCode());
        }
        return this.sourceTransactionSecurity;
    }

    /**
     * Here when the document is being read from the DB, the security object to be returned must be the object from the DB and not
     * the new object created.
     * 
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#getTargetTransactionSecurity()
     */
    public EndowmentTransactionSecurity getTargetTransactionSecurity() {
        if (this.targetTransactionSecurities.size() > 0) {
            this.targetTransactionSecurity = (EndowmentTargetTransactionSecurity) this.targetTransactionSecurities.get(0);
        }

        return this.targetTransactionSecurity;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#setSourceTransactionSecurity(org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity)
     */
    public void setSourceTransactionSecurity(EndowmentTransactionSecurity sourceTransactionSecurity) {
        this.sourceTransactionSecurity = (EndowmentSourceTransactionSecurity) sourceTransactionSecurity;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument#setTargetTransactionSecurity(org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity)
     */
    public void setTargetTransactionSecurity(EndowmentTransactionSecurity targetTransactionSecurity) {
        this.targetTransactionSecurity = (EndowmentTargetTransactionSecurity) targetTransactionSecurity;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        List managedList = super.buildListOfDeletionAwareLists();

        managedList.add(getTargetTransactionSecurities());
        managedList.add(getSourceTransactionSecurities());

        return managedList;
    }
}
