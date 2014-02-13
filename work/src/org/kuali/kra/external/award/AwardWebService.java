package org.kuali.kra.external.award;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateDto;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateStatusDto;
import org.kuali.kfs.module.external.kc.dto.AwardDTO;
import org.kuali.kfs.module.external.kc.dto.AwardFieldValuesDto;
import org.kuali.kfs.module.external.kc.dto.AwardSearchCriteriaDto;

@WebService(name = KcConstants.Award.SOAP_SERVICE_NAME, targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AwardWebService {

	public AwardDTO getAward(@WebParam(name="awardId") Long awardId);

    public List<AwardDTO> getMatchingAwards(@WebParam(name= "searchCriteria") AwardFieldValuesDto fieldValues);

    public List<AwardDTO> searchAwards(@WebParam(name="searchDto") AwardSearchCriteriaDto searchDto);

    public AwardBillingUpdateStatusDto updateAwardBillingStatus(@WebParam(name="searchDto") AwardFieldValuesDto searchDto,
            @WebParam(name="billingUpdate") AwardBillingUpdateDto updateDto);
}
