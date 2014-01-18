package org.kuali.kra.external.sponsor;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.SponsorCriteriaDto;
import org.kuali.kfs.module.external.kc.dto.SponsorDTO;

@WebService(name = "SponsorWebService", targetNamespace = KcConstants.KC_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface SponsorWebService {

	public SponsorDTO getSponsor(@WebParam(name= "sponsorCode") String sponsorCode);

	public List<SponsorDTO> getMatchingSponsors(@WebParam(name= "searchCriteria") SponsorCriteriaDto searchCriteria);
}
