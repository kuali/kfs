package org.kuali.kra.external.awardtype;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.AwardTypeDTO;

@WebService(name = KcConstants.Award.SOAP_SERVICE_NAME, targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AwardTypeWebService {

    public AwardTypeDTO getAwardType(@WebParam(name="awardTypeCode") Integer awardTypeCode);

    public List<AwardTypeDTO> findMatching(@WebParam(name="searchCriteria") List<HashMapElement> searchCriteria);
}
