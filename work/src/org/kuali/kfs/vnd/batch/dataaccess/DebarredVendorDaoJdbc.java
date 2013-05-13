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
package org.kuali.kfs.vnd.batch.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DebarredVendorDaoJdbc extends  PlatformAwareDaoBaseJdbc implements DebarredVendorDao {
    private VendorService vendorService;
    private DebarredVendorMatchDao debarredVendorMatchDao;

    @Override
    public List<DebarredVendorMatch> match() {
        String active = "dtl.DOBJ_MAINT_CD_ACTV_IND = 'Y'";
        String joinDtl = " INNER JOIN pur_vndr_dtl_t dtl";
        String joinExcl = " INNER JOIN PUR_VNDR_EXCL_MT excl";
        String where = " WHERE " + active;
        String eplsFields = "excl.VNDR_EXCL_ID, excl.VNDR_EXCL_LOAD_DT, excl.VNDR_EXCL_NM, excl.VNDR_EXCL_LN1_ADDR, excl.VNDR_EXCL_LN2_ADDR, excl.VNDR_EXCL_CTY_NM" +
        		", excl.VNDR_EXCL_ST_CD, excl.VNDR_EXCL_PRVN_NM, excl.VNDR_EXCL_ZIP_CD, excl.VNDR_EXCL_OTHR_NM, excl.VNDR_EXCL_DESC_TXT";

        String selectName = "SELECT dtl.VNDR_HDR_GNRTD_ID, dtl.VNDR_DTL_ASND_ID, " + eplsFields + " , 0 VNDR_ADDR_GNRTD_ID";
        String fromName = " FROM pur_vndr_dtl_t dtl";
        String name = filter("dtl.VNDR_NM", "., ");
        String eplsName = filter("excl.VNDR_EXCL_NM", "., ");
        String onName = " ON " + compare(name, eplsName, false); // use = to compare
        String sqlName = selectName + fromName + joinExcl + onName + where;

        String selectAlias = "SELECT als.VNDR_HDR_GNRTD_ID, als.VNDR_DTL_ASND_ID, " + eplsFields + " , 0 VNDR_ADDR_GNRTD_ID";
        String fromAlias = " FROM pur_vndr_alias_t als";
        String onAlsDtl = " ON als.VNDR_HDR_GNRTD_ID = dtl.VNDR_HDR_GNRTD_ID AND als.VNDR_DTL_ASND_ID = dtl.VNDR_DTL_ASND_ID";
        String alias = filter("als.VNDR_ALIAS_NM", "., ");
        String eplsAlias = filter("excl.VNDR_EXCL_NM", "., ");
        String onAlias = " ON " + compare(alias, eplsAlias, false); // use = to compare
        String sqlAlias = selectAlias + fromAlias + joinDtl + onAlsDtl + joinExcl + onAlias + where;

        String selectAddr = "SELECT addr.VNDR_HDR_GNRTD_ID, addr.VNDR_DTL_ASND_ID, " + eplsFields + " , addr.VNDR_ADDR_GNRTD_ID";
        String fromAddr = " FROM pur_vndr_addr_t addr";
        String onAddrDtl = " ON addr.VNDR_HDR_GNRTD_ID = dtl.VNDR_HDR_GNRTD_ID AND addr.VNDR_DTL_ASND_ID = dtl.VNDR_DTL_ASND_ID";
        //
        String addr1 = filter("addr.VNDR_LN1_ADDR", ".,# ");
        String eplsAddr1 = filter("excl.VNDR_EXCL_LN1_ADDR", ".,# ");
        String cmpAddr1 = compare(addr1, eplsAddr1, true); // use LIKE to compare
        //
        String city = filter("addr.VNDR_CTY_NM", "., ");
        String eplsCity = filter("excl.VNDR_EXCL_CTY_NM", "., ");
        String cmpCity = compare(city, eplsCity, false); // use = to compare
        //
        String state = "upper(addr.VNDR_ST_CD)";
        String eplsState = "upper(excl.VNDR_EXCL_ST_CD)";
        String cmpState = compare(state, eplsState, false); // use = to compare
        //
        String zip = filter("addr.VNDR_ZIP_CD", "-");
        String eplsZip = filter("excl.VNDR_EXCL_ZIP_CD", "-");
        String cmpZip = compare(zip, eplsZip, false); // use = to compare
        String fullZip = "length(addr.VNDR_ZIP_CD) > 5";
        //
        String onAddr = " ON (" + cmpAddr1 + " OR " + cmpZip + " AND " + fullZip + ") AND " + cmpCity + " AND " + cmpState;
        String sqlAddr = selectAddr + fromAddr + joinDtl + onAddrDtl + joinExcl + onAddr + where;

        String max = ", MAX(VNDR_ADDR_GNRTD_ID)";
        String selectFields = "VNDR_HDR_GNRTD_ID, VNDR_DTL_ASND_ID, VNDR_EXCL_ID, VNDR_EXCL_LOAD_DT, VNDR_EXCL_NM, VNDR_EXCL_LN1_ADDR, VNDR_EXCL_LN2_ADDR, VNDR_EXCL_CTY_NM" +
                ", VNDR_EXCL_ST_CD, VNDR_EXCL_PRVN_NM, VNDR_EXCL_ZIP_CD, VNDR_EXCL_OTHR_NM, VNDR_EXCL_DESC_TXT";
        String select = "SELECT " + selectFields + max;
        String subqr = sqlName + " UNION " + sqlAlias + " UNION " + sqlAddr;
        String from = " FROM (" + subqr + ")";
        String group = " GROUP BY " + selectFields;
        String sql = select + from + group;


        List<DebarredVendorMatch> matches = new ArrayList<DebarredVendorMatch>();
        try {
            SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql);
            DebarredVendorMatch match;

            while(rs.next()) {
                match = new DebarredVendorMatch();
                match.setVendorHeaderGeneratedIdentifier(new Integer(rs.getInt(1)));
                match.setVendorDetailAssignedIdentifier(new Integer(rs.getInt(2)));
                match.setLoadDate(rs.getDate(4));
                match.setName(rs.getString(5));
                match.setAddress1(rs.getString(6));
                match.setAddress2(rs.getString(7));
                match.setCity(rs.getString(8));
                match.setState(rs.getString(9));
                match.setProvince(rs.getString(10));
                match.setZip(rs.getString(11));
                match.setAliases(rs.getString(12));
                match.setDescription(rs.getString(13));
                match.setAddressGeneratedId(rs.getLong(14));
                // didn't find a matched address, search for best one
                if (match.getAddressGeneratedId() == 0) {
                    match.setAddressGeneratedId(getMatchAddressId(match));
                }

                DebarredVendorMatch oldMatch = debarredVendorMatchDao.getPreviousVendorExcludeConfirmation(match);
                if (oldMatch == null) {
                    // store the match only if an exact old match doesn't exist
                    match.setConfirmStatusCode("U"); // status - Unprocessed
                    matches.add(match);
                }
            }
        } catch (Exception e) {
            // if exception occurs, return empty results
            throw new RuntimeException(e);
        }

        return matches;
    }

    /**
     * Gets the addressGeneratedId of the vendor address that matches best with the address of the
     * EPLS debarred vendor in the specified vendor exclude match.
     * If no address matches, returns the default address for IU campus.
     */
    protected long getMatchAddressId(DebarredVendorMatch match) {
        long bestid = 0;
        long defaultId = 0;
        int maxPriority = 0;
        List<VendorAddress> addresses = vendorService.getVendorDetail(match.getVendorHeaderGeneratedIdentifier(),
                match.getVendorDetailAssignedIdentifier()).getVendorAddresses();
        if (addresses == null ) {
            return bestid;
        }

        for (VendorAddress address : addresses) {
            if (address.isVendorDefaultAddressIndicator()) {
                defaultId = address.getVendorAddressGeneratedIdentifier();
            }
            //each condition satisfied will increase the priority score for this address
            int priority = 0;
            String vendorAddr1 = StringUtils.replaceChars(address.getVendorLine1Address(), ".,# ", "");
            String eplsAddr1 = StringUtils.replaceChars(match.getAddress1(), ".,# ", "");
            if (StringUtils.equalsIgnoreCase(vendorAddr1, eplsAddr1)) {
                priority++;
            }
            String vendorCity = StringUtils.replaceChars(address.getVendorCityName(), "., ", "");
            String eplsCity = StringUtils.replaceChars(match.getCity(), "., ", "");
            if (StringUtils.equalsIgnoreCase(vendorCity, eplsCity)) {
                priority++;
            }
            if (StringUtils.equalsIgnoreCase(address.getVendorStateCode(), match.getState())) {
                priority++;
            }
            String vendorZip = StringUtils.substring(address.getVendorZipCode(), 0, 5);
            String eplsZip = StringUtils.substring(match.getZip(), 0, 5);
            if (StringUtils.equals(vendorZip, eplsZip)) {
                priority++;
            }
            if (priority >= maxPriority) {
                bestid = address.getVendorAddressGeneratedIdentifier();
                maxPriority = priority;
            }
        }
        if (bestid == 0) {
            bestid = defaultId;
        }
        return bestid;
    }

    protected String filter(String field, String charset) {
        // add upper function
        String upper = "upper(" + field + ")";
        if (charset == null)
            return upper;

        // add replace functions layer by layer to filter out the chars in the charset one by one
        String replace = upper;
        char[] chararr = charset.toCharArray();
        for (char ch : chararr) {
            // replace with empty string
            replace = "replace(" + replace + ", '" + ch + "', '')";
        }
        return replace;
    }

    protected String compare(String fieldl, String fieldr, boolean useLike) {
        String cmpstr = "";

        // whether neither field is null
        String notnulll = fieldl + " IS NOT NULL";
        String notnullr = fieldr + " IS NOT NULL";

        if (useLike) {
            // whether one of the two fields is substring of the other
            String like1 = notnullr + " AND " + fieldl + " LIKE '%'||" + fieldr + "||'%'";
            String like2 = notnulll + " AND " + fieldr + " LIKE '%'||" + fieldl + "||'%'";
            cmpstr += "(" + like1 + " OR " + like2 + ")"; // put () around the 'OR' to ensure integrity
        }
        else {
            // whether the two fields equal
            cmpstr = notnulll + " AND " + fieldl + " = " + fieldr;
        }

        return cmpstr;
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
     * Gets the debarredVendorMatchDao attribute.
     * @return Returns the debarredVendorMatchDao.
     */
    public DebarredVendorMatchDao getDebarredVendorMatchDao() {
        return debarredVendorMatchDao;
    }

    /**
     * Sets the debarredVendorMatchDao attribute value.
     * @param debarredVendorMatchDao The debarredVendorMatchDao to set.
     */
    public void setDebarredVendorMatchDao(DebarredVendorMatchDao debarredVendorMatchDao) {
        this.debarredVendorMatchDao = debarredVendorMatchDao;
    }

}
