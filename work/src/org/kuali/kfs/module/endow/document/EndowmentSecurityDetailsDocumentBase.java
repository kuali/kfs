/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;


public abstract class EndowmentSecurityDetailsDocumentBase extends EndowmentTransactionLinesDocumentBase implements EndowmentSecurityDetailsDocument {
	protected List<EndowmentSourceTransactionSecurity> sourceTransactionSecurities;
    protected List<EndowmentTargetTransactionSecurity> targetTransactionSecurities;

    protected EndowmentSourceTransactionSecurity sourceTransactionSecurity;
    protected EndowmentTargetTransactionSecurity targetTransactionSecurity;

    public EndowmentSecurityDetailsDocumentBase() {
        super();
        sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        sourceTransactionSecurities = new ArrayList<EndowmentSourceTransactionSecurity>();
        targetTransactionSecurities = new ArrayList<EndowmentTargetTransactionSecurity>();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();

        sourceTransactionSecurities.clear();
        targetTransactionSecurities.clear();

        // A Hack to insert transaction securities in the securities collection.
        if (StringUtils.isNotBlank(sourceTransactionSecurity.getSecurityID())) {
            getSourceTransactionSecurities().add(0, sourceTransactionSecurity);
        }

        // A Hack to insert transaction securities in the securities collection.
        if (StringUtils.isNotBlank(targetTransactionSecurity.getSecurityID())) {
            getTargetTransactionSecurities().add(0, targetTransactionSecurity);
        }

    }


    /**
     * Gets the sourceTransactionSecurities.
     * 
     * @return sourceTransactionSecurities
     */
    public List<EndowmentSourceTransactionSecurity> getSourceTransactionSecurities() {
        return sourceTransactionSecurities;
    }

    /**
     * Sets the sourceTransactionSecurities.
     * 
     * @param sourceTransactionSecurities
     */
    public void setSourceTransactionSecurities(List<EndowmentSourceTransactionSecurity> sourceTransactionSecurities) {
        this.sourceTransactionSecurities = sourceTransactionSecurities;
    }

    /**
     * Gets the targetTransactionSecurities.
     * 
     * @return targetTransactionSecurities
     */
    public List<EndowmentTargetTransactionSecurity> getTargetTransactionSecurities() {
        return targetTransactionSecurities;
    }

    /**
     * Sets the targetTransactionSecurities.
     * 
     * @param targetTransactionSecurities
     */
    public void setTargetTransactionSecurities(List<EndowmentTargetTransactionSecurity> targetTransactionSecurities) {
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
