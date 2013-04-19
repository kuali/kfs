package org.kuali.kfs.sys.document.web.struts;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.web.MassImportFileParser;

/**
 * Mass upload sub-account, sub-object, project. This class is the Struts specific form object that works in conjunction with the
 * pojo utilities to build the UI.
 */
public abstract class MassImportTransactionalDocumentFormBase extends FinancialSystemTransactionalDocumentFormBase {

    private static final Logger Log = Logger.getLogger(MassImportTransactionalDocumentFormBase.class);
    protected FormFile importFile;

    public FormFile getImportFile() {
        return importFile;
    }

    public void setImportFile(FormFile importFile) {
        this.importFile = importFile;
    }

    public abstract MassImportFileParser getMassImportFileParser();
}
