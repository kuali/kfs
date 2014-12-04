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
package org.kuali.kfs.module.endow.document.web.struts;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class...
 */
public abstract class EndowmentAccountingLinesDocumentFormBase extends EndowmentTransactionLinesDocumentFormBase {
    protected FormFile sourceFile;
    protected FormFile targetFile;

    protected EndowmentAccountingLine newSourceAccountingLine;
    protected EndowmentAccountingLine newTargetAccountingLine;

    /**
     * Constructs a EndowmentAccountingLinesDocumentFormBase.java.
     */
    public EndowmentAccountingLinesDocumentFormBase() {
        super();
        newSourceAccountingLine = new SourceEndowmentAccountingLine();
        newTargetAccountingLine = new TargetEndowmentAccountingLine();
    }

    /**
     * Gets the EndowmentAccountingLinesDocumentBase.
     * 
     * @return EndowmentAccountingLinesDocumentBase
     */
    public EndowmentAccountingLinesDocumentBase getEndowmentAccountingLinesDocumentBase() {
        return (EndowmentAccountingLinesDocumentBase) getDocument();
    }

    /**
     * Gets the newSourceAccountingLine.
     * 
     * @return newSourceAccountingLine
     */
    public EndowmentAccountingLine getNewSourceAccountingLine() {
        return newSourceAccountingLine;
    }

    /**
     * Sets the newSourceAccountingLine.
     * 
     * @param newSourceAccountingLine
     */
    public void setNewSourceAccountingLine(EndowmentAccountingLine newSourceAccountingLine) {
        this.newSourceAccountingLine = newSourceAccountingLine;
    }

    /**
     * Gets the newSourceAccountingLine.
     * 
     * @return newSourceAccountingLine
     */
    public EndowmentAccountingLine getNewTargetAccountingLine() {
        return newTargetAccountingLine;
    }

    /**
     * Sets the newSourceAccountingLine.
     * 
     * @param newTargetAccountingLine
     */
    public void setNewTargetAccountingLine(EndowmentAccountingLine newTargetAccountingLine) {
        this.newTargetAccountingLine = newTargetAccountingLine;
    }

    /**
     * Gets the sourceFile.
     * 
     * @return sourceFile
     */
    public FormFile getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the sourceFile.
     * 
     * @param sourceFile
     */
    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the targetFile.
     * 
     * @return targetFile
     */
    public FormFile getTargetFile() {
        return targetFile;
    }

    /**
     * Sets the targetFile.
     * 
     * @param targetFile
     */
    public void setTargetFile(FormFile targetFile) {
        this.targetFile = targetFile;
    }
    
    /**
     * @return the URL to the accounting line import instructions
     */
    public String getAccountingLineImportInstructionsUrl() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY) + SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.ENDOWMENT_DOCUMENT.class, EndowParameterKeyConstants.ENDOWMENT_ACCOUNTING_LINE_IMPORT);
    }

}
