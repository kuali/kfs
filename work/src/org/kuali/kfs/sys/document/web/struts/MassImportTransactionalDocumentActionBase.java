package org.kuali.kfs.sys.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedMassImportLineEventBase;
import org.kuali.kfs.sys.exception.MassImportFileParserException;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Mass import action base class
 */
public class MassImportTransactionalDocumentActionBase extends FinancialSystemTransactionalDocumentActionBase {
    private static final Logger LOG = Logger.getLogger(MassImportTransactionalDocumentActionBase.class);

    /**
     * Remove all imported lines from collection
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MassImportTransactionalDocumentFormBase importForm = (MassImportTransactionalDocumentFormBase) form;
        MassImportDocument importDocument = (MassImportDocument) importForm.getDocument();
        List<MassImportLineBase> importedLineList = importDocument.getImportDetailCollection();

        if (importedLineList != null && !importedLineList.isEmpty()) {
            importedLineList.clear();
        }
        importDocument.setNextItemLineNumber(1);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Import sub-accounts the document from a spreadsheet.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward importLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("Start importing lines");
        boolean allPassed = true;
        MassImportTransactionalDocumentFormBase importForm = (MassImportTransactionalDocumentFormBase) form;
        MassImportDocument importDocument = (MassImportDocument) importForm.getDocument();
        FormFile importFile = importForm.getImportFile();
        Class<MassImportLineBase> importClass = importDocument.getImportLineClass();
        List<MassImportLineBase> importedLines = null;
        String errorPathPrefix = importDocument.getFullErrorPathPrefix();

        try {
            checkUploadFile(importFile);
            importedLines = importForm.getMassImportFileParser().importLines(importFile.getFileName(), importFile.getFileData(), importClass, importDocument, errorPathPrefix);
            importDocument.customizeImportedLines(importedLines);

            if (importedLines != null && !importedLines.isEmpty()) {
                allPassed = validateImportedLines(importDocument, importedLines, importDocument);
            }
        }
        catch (MassImportFileParserException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add line to list for those lines which were successfully imported
        if (importedLines != null && allPassed) {
            for (MassImportLineBase importLineDetail : importedLines) {
                importLineDetail.setSequenceNumber(importDocument.getNextItemLineNumber());
                importDocument.getImportDetailCollection().add(importLineDetail);
                importDocument.setNextItemLineNumber(new Integer(importDocument.getNextItemLineNumber().intValue() + 1));
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * validate imported lines
     *
     * @param importDocument
     * @param importedLines
     * @param documentNumber
     * @return
     */
    protected boolean validateImportedLines(MassImportDocument importDocument, List<MassImportLineBase> importedLines, MassImportDocument document) {
        boolean allPassed = true;
        allPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedMassImportLineEventBase("importing item to document " + document.getDocumentNumber(), "", importDocument, importedLines));
        return allPassed;
    }

    protected void checkUploadFile(FormFile file) {
        if (file == null) {
            throw new MassImportFileParserException("invalid (null) upload file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
    }
}
