package edu.arizona.kfs.module.ec.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.springframework.transaction.annotation.Transactional;
import edu.arizona.kfs.sys.KFSPropertyConstants;

@Transactional
public class EffortCertificationDocumentServiceImpl extends org.kuali.kfs.module.ec.service.impl.EffortCertificationDocumentServiceImpl {

    @Override
    public boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        super.populateEffortCertificationDocument(effortCertificationDocument, effortCertificationDocumentBuild);

        // populate the document header of the document
        FinancialSystemDocumentHeader documentHeader = effortCertificationDocument.getFinancialSystemDocumentHeader();
        String description = this.buildDocumentDescription(effortCertificationDocument, effortCertificationDocumentBuild);
        documentHeader.setDocumentDescription(description);

        return true;
    }

    protected String buildDocumentDescription(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        StringBuilder description = new StringBuilder();

        String employeeName = effortCertificationDocumentBuild.getEmployee().getName();
        description.append(employeeName).append(", ");

        EffortCertificationReportDefinition reportDefinition = effortCertificationDocumentBuild.getEffortCertificationReportDefinition();
        if (reportDefinition == null) {  // get report title from the document's report definition object
            reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();
        }

        String reportPeriodTitle = reportDefinition.getEffortCertificationReportPeriodTitle();
        description.append(reportPeriodTitle);

        int maxLengthOfDocumentDescription = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(FinancialSystemDocumentHeader.class, KFSPropertyConstants.DOCUMENT_DESCRIPTION);
        return StringUtils.left(description.toString(), maxLengthOfDocumentDescription);
    }
}