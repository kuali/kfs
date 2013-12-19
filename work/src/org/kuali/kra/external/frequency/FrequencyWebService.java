package org.kuali.kra.external.frequency;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.FrequencyDto;

@WebService(name = KcConstants.Frequency.SOAP_SERVICE_NAME, targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface FrequencyWebService {

	public FrequencyDto getFrequency(@WebParam(name="frequencyCode") String frequencyCode);

	public List<FrequencyDto> findMatching(@WebParam(name="frequencyCode") String frequencyCode, @WebParam(name="description") String description);

	public List<FrequencyDto> findAll();
}
