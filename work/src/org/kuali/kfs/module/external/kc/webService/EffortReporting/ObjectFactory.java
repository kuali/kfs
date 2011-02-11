
package org.kuali.kfs.module.external.kc.webService.EffortReporting;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kc package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetProjectDirector_QNAME = new QName("KC", "getProjectDirector");
    private final static QName _GetProjectDirectorResponse_QNAME = new QName("KC", "getProjectDirectorResponse");
    private final static QName _IsFederalSponsorResponse_QNAME = new QName("KC", "isFederalSponsorResponse");
    private final static QName _IsFederalSponsor_QNAME = new QName("KC", "isFederalSponsor");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kc
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IsFederalSponsor }
     * 
     */
    public IsFederalSponsor createIsFederalSponsor() {
        return new IsFederalSponsor();
    }

    /**
     * Create an instance of {@link GetProjectDirector }
     * 
     */
    public GetProjectDirector createGetProjectDirector() {
        return new GetProjectDirector();
    }

    /**
     * Create an instance of {@link GetProjectDirectorResponse }
     * 
     */
    public GetProjectDirectorResponse createGetProjectDirectorResponse() {
        return new GetProjectDirectorResponse();
    }

    /**
     * Create an instance of {@link IsFederalSponsorResponse }
     * 
     */
    public IsFederalSponsorResponse createIsFederalSponsorResponse() {
        return new IsFederalSponsorResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProjectDirector }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "KC", name = "getProjectDirector")
    public JAXBElement<GetProjectDirector> createGetProjectDirector(GetProjectDirector value) {
        return new JAXBElement<GetProjectDirector>(_GetProjectDirector_QNAME, GetProjectDirector.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProjectDirectorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "KC", name = "getProjectDirectorResponse")
    public JAXBElement<GetProjectDirectorResponse> createGetProjectDirectorResponse(GetProjectDirectorResponse value) {
        return new JAXBElement<GetProjectDirectorResponse>(_GetProjectDirectorResponse_QNAME, GetProjectDirectorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsFederalSponsorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "KC", name = "isFederalSponsorResponse")
    public JAXBElement<IsFederalSponsorResponse> createIsFederalSponsorResponse(IsFederalSponsorResponse value) {
        return new JAXBElement<IsFederalSponsorResponse>(_IsFederalSponsorResponse_QNAME, IsFederalSponsorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsFederalSponsor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "KC", name = "isFederalSponsor")
    public JAXBElement<IsFederalSponsor> createIsFederalSponsor(IsFederalSponsor value) {
        return new JAXBElement<IsFederalSponsor>(_IsFederalSponsor_QNAME, IsFederalSponsor.class, null, value);
    }

}
