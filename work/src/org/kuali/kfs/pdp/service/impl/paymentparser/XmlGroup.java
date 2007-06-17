/*
 * Created on Sep 27, 2004
 *
 */
package org.kuali.module.pdp.xml;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.kuali.module.pdp.bo.PaymentGroup;


/**
 * @author jsissom
 *
 */
public class XmlGroup implements Serializable {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlGroup.class);

  private String payee_name = null;
  private String id_type = null;
  private String payee_id = null;
  private String payee_own_cd = null;
  private String customer_IU_identifier = null;
  private String address1 = null;
  private String address2 = null;
  private String address3 = null;
  private String address4 = null;
  private String city = null;
  private String state = null;
  private String country = null;
  private String zip = null;
  private Boolean campus_address_ind = null;
  private Date payment_date = null;
  private Boolean attachment_ind = null;
  private Boolean special_handling_ind = null;
  private Boolean taxable_ind = null;
  private Boolean nra_ind = null;
  private Boolean immediate_ind = Boolean.FALSE;
  private Boolean combine_ind = null;
  private List detail;

  public XmlGroup() {
    detail = new ArrayList();
  }

  public List getDetail() {
    return detail;
  }
  public void setDetail(List detail) {
    this.detail = detail;
  }
  public void addDetail(XmlDetail d) {
    this.detail.add(d);
  }

  public Boolean getAttachment_ind() {
    return attachment_ind;
  }
  public Boolean getCampus_address_ind() {
    return campus_address_ind;
  }
  public Boolean getNra_ind() {
    return nra_ind;
  }
  public Boolean getImmediate_ind() {
    return immediate_ind;
  }
  public Boolean getSpecial_handling_ind() {
    return special_handling_ind;
  }
  public Boolean getTaxable_ind() {
    return taxable_ind;
  }

  public void setField(String name,String value) {
    // Don't need to set an empty value
    if ( (value == null) || (value.length() == 0) ) {
      return;
    }

    if ( "payee_name".equals(name) ) {
      setPayee_name(value);
    } else if ( "id_type".equals(name) ) {
      setId_type(value);
    } else if ( "payee_id".equals(name) ) {
      setPayee_id(value);
    } else if ( "payee_own_cd".equals(name) ) {
      setPayee_own_cd(value);
    } else if ( "customer_IU_identifier".equals(name) ) {
      setCustomer_IU_identifier(value);
    } else if ( "address1".equals(name) ) {
      setAddress1(value);
    } else if ( "address2".equals(name) ) {
      setAddress2(value);
    } else if ( "address3".equals(name) ) {
      setAddress3(value);
    } else if ( "address4".equals(name) ) {
      setAddress4(value);
    } else if ( "city".equals(name) ) {
      setCity(value);
    } else if ("state".equals(name) ) {
      setState(value);
    } else if ("country".equals(name) ) {
      setCountry(value);
    } else if ("zip".equals(name) ) {
      setZip(value);
    } else if ("campus_address_ind".equals(name) ) {
      setCampus_address_ind(new Boolean(value.equals("Y")));
    } else if ("payment_date".equals(name) ) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setPayment_date(sdf.parse(value));
      } catch (ParseException e) {
        // Don't know what to do
      }
    } else if ( "attachment_ind".equals(name) ) {
      setAttachment_ind(new Boolean(value.equals("Y")));
    } else if ("special_handling_ind".equals(name) ) {
      setSpecial_handling_ind(new Boolean(value.equals("Y")));
    } else if ("taxable_ind".equals(name) ) {
      setTaxable_ind(new Boolean(value.equals("Y")));
    } else if ( "nra_ind".equals(name) ) {
      setNra_ind(new Boolean(value.equals("Y")));
    } else if ( "immediate_ind".equals(name) ) {
      setImmediate_ind(new Boolean(value.equals("Y")));
    } else if ( "combine_group_ind".equals(name) ) {
      setCombine_ind(new Boolean(value.equals("Y")));
    }
  }

  public PaymentGroup getPaymentGroup() {
    PaymentGroup d = new PaymentGroup();

    d.setPayeeName(payee_name);
    d.setPayeeIdTypeCd(id_type);
    d.setPayeeId(payee_id);
    d.setPayeeOwnerCd(payee_own_cd);
    d.setCustomerIuNbr(customer_IU_identifier);
    d.setLine1Address(address1);
    d.setLine2Address(address2);
    d.setLine3Address(address3);
    d.setLine4Address(address4);
    d.setCity(city);
    d.setState(state);
    d.setCountry(country);
    d.setZipCd(zip);
    d.setCampusAddress(campus_address_ind);
    if ( payment_date != null ) {
      d.setPaymentDate(new Timestamp(payment_date.getTime()));
    } else {
      d.setPaymentDate(null);
    }
    d.setPymtAttachment(attachment_ind);
    d.setPymtSpecialHandling(special_handling_ind);
    d.setTaxablePayment(taxable_ind);
    d.setNraPayment(nra_ind);
    d.setProcessImmediate(immediate_ind);
    d.setCombineGroups(combine_ind);
    return d;
  }

  public Boolean getCombine_ind() {
    return combine_ind;
  }

  public void setCombine_ind(Boolean combine_ind) {
    this.combine_ind = combine_ind;
  }

  /**
   * @return Returns the city.
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city The city to set.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * @return Returns the country.
   */
  public String getCountry() {
    return country;
  }

  /**
   * @param country The country to set.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * @return Returns the state.
   */
  public String getState() {
    return state;
  }

  /**
   * @param state The state to set.
   */
  public void setState(String state) {
    this.state = state;
  }

  /**
   * @return Returns the address1.
   */
  public String getAddress1() {
    return address1;
  }

  /**
   * @param address1 The address1 to set.
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  /**
   * @return Returns the address2.
   */
  public String getAddress2() {
    return address2;
  }

  /**
   * @param address2 The address2 to set.
   */
  public void setAddress2(String address2) {
    this.address2 = address2;
  }
  /**
   * @return Returns the address3.
   */
  public String getAddress3() {
    return address3;
  }
  /**
   * @param address3 The address3 to set.
   */
  public void setAddress3(String address3) {
    this.address3 = address3;
  }
  /**
   * @return Returns the address4.
   */
  public String getAddress4() {
    return address4;
  }
  /**
   * @param address4 The address4 to set.
   */
  public void setAddress4(String address4) {
    this.address4 = address4;
  }
  /**
   * @return Returns the attachment_ind.
   */
  public Boolean isAttachment_ind() {
    return attachment_ind;
  }
  /**
   * @param attachment_ind The attachment_ind to set.
   */
  public void setAttachment_ind(Boolean attachment_ind) {
    this.attachment_ind = attachment_ind;
  }
  /**
   * @return Returns the campus_address_ind.
   */
  public Boolean isCampus_address_ind() {
    return campus_address_ind;
  }
  /**
   * @param campus_address_ind The campus_address_ind to set.
   */
  public void setCampus_address_ind(Boolean campus_address_ind) {
    this.campus_address_ind = campus_address_ind;
  }
  /**
   * @return Returns the customer_IU_identifier.
   */
  public String getCustomer_IU_identifier() {
    return customer_IU_identifier;
  }
  /**
   * @param customer_IU_identifier The customer_IU_identifier to set.
   */
  public void setCustomer_IU_identifier(String customer_IU_identifier) {
    this.customer_IU_identifier = customer_IU_identifier;
  }
  /**
   * @return Returns the id_type.
   */
  public String getId_type() {
    return id_type;
  }
  /**
   * @param id_type The id_type to set.
   */
  public void setId_type(String id_type) {
    this.id_type = id_type;
  }
  /**
   * @return Returns the nra_ind.
   */
  public Boolean isNra_ind() {
    return nra_ind;
  }
  /**
   * @param nra_ind The nra_ind to set.
   */
  public void setNra_ind(Boolean nra_ind) {
    this.nra_ind = nra_ind;
  }
  /**
   * @return Returns the immediate_ind.
   */
  public Boolean isImmediate_ind() {
    return immediate_ind;
  }
  /**
   * @param immediate_ind The immediate_ind to set.
   */
  public void setImmediate_ind(Boolean immediate_ind) {
    this.immediate_ind = immediate_ind;
  }
  /**
   * @return Returns the payee_id.
   */
  public String getPayee_id() {
    return payee_id;
  }
  /**
   * @param payee_id The payee_id to set.
   */
  public void setPayee_id(String payee_id) {
    this.payee_id = payee_id;
  }
  /**
   * @return Returns the payee_name.
   */
  public String getPayee_name() {
    return payee_name;
  }
  /**
   * @param payee_name The payee_name to set.
   */
  public void setPayee_name(String payee_name) {
    this.payee_name = payee_name;
  }
  /**
   * @return Returns the payee_own_cd.
   */
  public String getPayee_own_cd() {
    return payee_own_cd;
  }
  /**
   * @param payee_own_cd The payee_own_cd to set.
   */
  public void setPayee_own_cd(String payee_own_cd) {
    this.payee_own_cd = payee_own_cd;
  }
  /**
   * @return Returns the payment_date.
   */
  public Date getPayment_date() {
    return payment_date;
  }
  
  /**
   * @param payment_date The payment_date to set.
   */
  public void setPayment_date(Date payment_date) {
    this.payment_date = payment_date;
  }
  /**
   * @return Returns the special_handling_ind.
   */
  public Boolean isSpecial_handling_ind() {
    return special_handling_ind;
  }
  /**
   * @param special_handling_ind The special_handling_ind to set.
   */
  public void setSpecial_handling_ind(Boolean special_handling_ind) {
    this.special_handling_ind = special_handling_ind;
  }
  /**
   * @return Returns the taxable_ind.
   */
  public Boolean isTaxable_ind() {
    return taxable_ind;
  }
  /**
   * @param taxable_ind The taxable_ind to set.
   */
  public void setTaxable_ind(Boolean taxable_ind) {
    this.taxable_ind = taxable_ind;
  }
  /**
   * @return Returns the zip.
   */
  public String getZip() {
    return zip;
  }
  /**
   * @param zip The zip to set.
   */
  public void setZip(String zip) {
    this.zip = zip;
  }
}
