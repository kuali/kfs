/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.vnd.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.batch.service.VendorExcludeService;
import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class VendorExclusionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl{
    VendorExcludeService vendorExcludeService;
    VendorService vendorService;
    ConfigurationService kualiConfigurationService;

    private String confirmStatusFieldValue="";

    /**
     * Custom action urls for Asset.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(bo, pkNames);
        anchorHtmlDataList.add(getInquiryUrl(bo));
        anchorHtmlDataList.add(getEditUrl(bo));
        anchorHtmlDataList.add(getConfirmUrl(bo));
        anchorHtmlDataList.add(getDenyUrl(bo));
        return anchorHtmlDataList;
    }

    protected String getVendorInquiryUrl(DebarredVendorMatch match) {
        Properties properties = new Properties();
        properties.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.START_METHOD);
        properties.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorDetail.class.getName());
        properties.put("vendorDetailAssignedIdentifier", new Integer(match.getVendorDetailAssignedIdentifier()).toString());
        properties.put("vendorHeaderGeneratedIdentifier", new Integer(match.getVendorHeaderGeneratedIdentifier()).toString());

        return UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, properties);
    }

    protected HtmlData getInquiryUrl(BusinessObject bo){
        Properties properties = new Properties();
        properties.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.START_METHOD);
        properties.put("debarredVendorId", new Integer(((DebarredVendorMatch)bo).getDebarredVendorId()).toString());
        properties.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DebarredVendorMatch.class.getName());

        String href = UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, properties);
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KRADConstants.START_METHOD, "Inquiry");
        anchorHtmlData.setTarget(KFSConstants.NEW_WINDOW_URL_TARGET);
        return anchorHtmlData;
    }

    protected HtmlData getEditUrl(BusinessObject bo){
        String label = "edit";
        Integer vendorDetailAssignedIdentifier = ((DebarredVendorMatch)bo).getVendorDetailAssignedIdentifier();
        Integer vendorHeaderGeneratedIdentifier = ((DebarredVendorMatch)bo).getVendorHeaderGeneratedIdentifier();
        VendorDetail vendor = vendorService.getVendorDetail(vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifier);
        VendorDetail parent;
        if (!vendor.isVendorParentIndicator()) {
            label = "edit parent";
            parent = vendorService.getParentVendor(vendorHeaderGeneratedIdentifier);
            vendorDetailAssignedIdentifier = parent.getVendorDetailAssignedIdentifier();
            vendorHeaderGeneratedIdentifier = parent.getVendorHeaderGeneratedIdentifier();
        }

        Properties properties = new Properties();
        properties.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        properties.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier.toString());
        properties.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier.toString());
        properties.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorDetail.class.getName());
        String href = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, properties);
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, label);
        anchorHtmlData.setTarget(KFSConstants.NEW_WINDOW_URL_TARGET);
        return anchorHtmlData;
    }

    protected HtmlData getConfirmUrl(BusinessObject bo){
        Properties properties = new Properties();
        properties.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "confirmDebarredVendor");
        properties.put(DebarredVendorMatch.CONFIRM_STATUS, confirmStatusFieldValue);
        properties.put(DebarredVendorMatch.DEBARRED_VENDOR_ID, String.valueOf(((DebarredVendorMatch)bo).getDebarredVendorId()));
        properties.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DebarredVendorMatch.class.getName());
        properties.put("returnLocation", kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/kr/lookup.do");
        properties.put(DebarredVendorMatch.EXCLUSION_STATUS, ((DebarredVendorMatch)bo).getVendorExclusionStatus());

        String href = UrlFactory.parameterizeUrl("vendorExclusion.do", properties);
        href = kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+"/"+ href;
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KRADConstants.START_METHOD, "confirm");
        return anchorHtmlData;
    }

    protected HtmlData getDenyUrl(BusinessObject bo){
        Properties properties = new Properties();
        properties.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "denyDebarredVendor");
        properties.put(DebarredVendorMatch.CONFIRM_STATUS, confirmStatusFieldValue);
        properties.put(DebarredVendorMatch.DEBARRED_VENDOR_ID, String.valueOf(((DebarredVendorMatch)bo).getDebarredVendorId()));
        properties.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DebarredVendorMatch.class.getName());
        properties.put("returnLocation", kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/kr/lookup.do");
        properties.put("formKey", "88888888");

        String href = UrlFactory.parameterizeUrl("vendorExclusion.do", properties);
        href = kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+"/"+ href;
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KRADConstants.START_METHOD, "deny");
        return anchorHtmlData;
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getDebarredVendorSearchResults(fieldValues);
    }

    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getDebarredVendorSearchResults(fieldValues);
    }

    protected List<? extends BusinessObject> getDebarredVendorSearchResults(Map<String, String> fieldValues) {
        List<DebarredVendorMatch> vendorResultList = new ArrayList<DebarredVendorMatch>();
        String exclusionStatus = fieldValues.get(DebarredVendorMatch.EXCLUSION_STATUS);
        confirmStatusFieldValue = fieldValues.get(DebarredVendorMatch.CONFIRM_STATUS);
        fieldValues.remove(DebarredVendorMatch.EXCLUSION_STATUS);
        List<DebarredVendorMatch> searchResults = (List<DebarredVendorMatch>) super.getSearchResults(fieldValues);
        boolean status = exclusionStatus.equals(VendorConstants.EXCLUDED_MATCHED_VENDOR_STATUS) ? true : false;
        boolean debarred;
        for (DebarredVendorMatch match : searchResults) {
            debarred = match.getVendorHeader().getVendorDebarredIndicator() == null ? false : match.getVendorHeader().getVendorDebarredIndicator();
            if ((status && debarred) || (!status && !debarred)) {
                match.setConcatenatedId("<a href=\"" + getVendorInquiryUrl(match) + "\">" + match.getVendorDetail().getVendorNumber() + "</a> / " +
                        "<a href=\"http://www.epls.gov/epls/search.do?ssn=true&ssn_name=" + match.getName() + "&ssn_tin=" +
                        match.getVendorHeader().getVendorTaxNumber() + "\">" + match.getDebarredVendorId() + "</a>");
                match.setName(match.getVendorDetail().getVendorName() + " / " + match.getName());
                match.setCity(match.getVendorAddress().getVendorCityName() + " / " + match.getCity());
                match.setState(match.getVendorAddress().getVendorStateCode() == null ? match.getVendorAddress().getVendorCountryCode() : match.getVendorAddress().getVendorStateCode() + " / " + match.getState());
                match.setVendorExclusionStatus(exclusionStatus);
                vendorResultList.add(match);
            }
        }
        return vendorResultList;
    }

    /**
     * Gets the vendorExcludeService attribute.
     * @return Returns the vendorExcludeService.
     */
    public VendorExcludeService getVendorExcludeService() {
        return vendorExcludeService;
    }

    /**
     * Sets the vendorExcludeService attribute value.
     * @param vendorExcludeService The vendorExcludeService to set.
     */
    public void setVendorExcludeService(VendorExcludeService vendorExcludeService) {
        this.vendorExcludeService = vendorExcludeService;
    }

    /**
     * Gets the vendorService attribute.
     * @return Returns the vendorService.
     */
    public VendorService getVendorService() {
        return vendorService;
    }

    /**
     * Sets the vendorService attribute value.
     * @param vendorService The vendorService to set.
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * @return Returns the kualiConfigurationService.
     */
    @Override
    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
