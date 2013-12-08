package org.kuali.kra.external.awardpayment;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.AwardBasisOfPaymentDTO;
import org.kuali.kfs.module.external.kc.dto.AwardMethodOfPaymentDTO;

@WebService(name = KcConstants.AwardPayment.SOAP_SERVICE_NAME, targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AwardPaymentWebService {

	public AwardBasisOfPaymentDTO getBasisOfPayment(@WebParam(name="basisOfPaymentCode") String basisOfPaymentCode);

	public AwardMethodOfPaymentDTO getMethodOfPayment(@WebParam(name="methodOfPaymentCode") String methodOfPaymentCode);

	public List<AwardBasisOfPaymentDTO> getMatchingBasisOfPayments(@WebParam(name="searchCriteria") AwardBasisOfPaymentDTO searchCriteria);

	public List<AwardMethodOfPaymentDTO> getMatchingMethodOfPayments(@WebParam(name="searchCriteria") AwardMethodOfPaymentDTO searchCriteria);

	public List<AwardMethodOfPaymentDTO> getMatchingMethodOfPaymentsForBasisOfPayment(@WebParam(name="basisOfPaymentCode") String basisOfPaymentCode);
}
