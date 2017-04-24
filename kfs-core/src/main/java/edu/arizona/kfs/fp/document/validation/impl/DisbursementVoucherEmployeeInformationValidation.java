package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Collection;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherEmployeeInformationValidation extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherEmployeeInformationValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherEmployeeInformationValidation.class);

    DataDictionaryService dataDictionaryService;
    ParameterService parameterService;
    PersonService personService;

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) getAccountingDocumentForValidation();
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        boolean isNotRouted = workflowDocument.isInitiated() || workflowDocument.isSaved();

        if (!payeeDetail.isEmployee() || payeeDetail.isVendor() || !isNotRouted) {
            return true;
        }

        String employeeId = payeeDetail.getDisbVchrPayeeIdNumber();
        Person employee = personService.getPersonByEmployeeId(employeeId);

        // check existence of employee
        if (employee == null) { // If employee is not found, report existence error
            String label = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER);
            GlobalVariables.getMessageMap().putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        String statusCode = employee.getEmployeeStatusCode();
        Collection<String> employeeStatusCodes = parameterService.getParameterValuesAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.FpParameterConstants.ACTIVE_EMPLOYEE_STATUS_CODES_PARAMETER_NAME);
        boolean isActiveEmpoyee = ObjectUtils.isNotNull(employeeStatusCodes) && employeeStatusCodes.contains(statusCode);

        if (!isActiveEmpoyee) {
            String label = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER);
            GlobalVariables.getMessageMap().putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

}
