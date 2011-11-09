/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.sesn;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.sesn.timeouthandlers.TimeoutHandler;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class is used to interface with the distributed session database.
 * 
 * TODO: add clear principals to clearSesn
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DistributedSession {
    public static final String DEFAULT_PREFIX="DST";
    private static String prefix = DEFAULT_PREFIX;
    private JdbcTemplate jdbcTemplate;
    private TimeoutHandler timeoutHandler;
    private boolean allowInsertOnTouch = false;
    
    private static final Log logger = LogFactory.getLog(DistributedSession.class);

    /**
     * @param timeoutHandler the timeoutHandler to set
     */
    public void setTimeoutHandler(TimeoutHandler timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
    }

    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * This method determines if the Session Ticket is valid.
     * 
     * @param DST - the Distributed Session Ticket
     * @return true if the session is valid
     */
    public boolean isSesnValid(String DST) {
        logger.debug("isSesnValid(DST)");
        return isSesnValid (DST, new HashMap<String,Object>());
    }
    
    /**
     * This method determines if the Session Ticket is valid.
     * 
     * @param DST - the Distributed Session Ticket
     * @param timoutArgs - Additional information on which to base timeouts
     * @return true if the session is valid
     */
    public boolean isSesnValid(String DST, Map<String,Object> timeoutArgs) {
        logger.debug("isSesnValid(DST, timeoutArgs)");
        boolean bRet = false;
        String sql = "select sesnID, lastAccessDt, maxIdleTime from authnsesn where sesnID=?";
        
        if (DST != null) {
            Object[] args = { DST };
            
            try {
                Map<String,Object> fields = jdbcTemplate.queryForMap(sql, args);
                fields.put("maxIdleTime", this.getMaxIdleTime((Long)fields.get("maxIdleTime"), (Date)fields.get("lastAccessDt")));
                fields.putAll(timeoutArgs);
                
                if (logger.isDebugEnabled()) {
                    logger.debug("ARGUMENTS number:" + fields.size());
                    for (Iterator<Map.Entry<String,Object>> i = fields.entrySet().iterator(); i.hasNext(); ) {
                        Map.Entry<String,Object> entry = (Map.Entry<String,Object>)i.next();
                        logger.debug("ARGUMENT " + entry.getKey() + ":" + entry.getValue());
                    }
                }
                
                if(!timeoutHandler.hasTimedOut(fields)) {
                    logger.debug("Session not timed out");
                    bRet = true;
                } else {
                    logger.debug("Session timed out");
                }
            } catch (Exception e) {
                logger.debug(e);
            }
        } 
        else {
            logger.debug("Session ID is null");           
        }
                
        return bRet;
    }
    
    
    /**
     * This method returns the list of principals currently authenticated
     * that are associated with the provided Distributed Session Ticket
     * 
     * @param DST - the Distributed Session Ticket
     * @return the List of authenticated principals
     */
    public List<String> getAuthenticatedPricipals(String DST) {
        String sql = "select principalID from authnsesn where sesnID=?";
        Object args[] = { DST };
        
        return jdbcTemplate.queryForList(sql, args, String.class);
    }
    
    /**
     * This method removes the session
     * 
     * @param DST - the Distributed Session Ticket
     */
    public void clearSesn(String DST) {
        String sql = "delete from authnsesn where sesnID='" + DST + "'";
        
        jdbcTemplate.execute(sql);
    }
    
    
    /**
     * This method takes an authenticated principal and generates a
     * Distributed Session Ticket and updates the session database
     * 
     * @param principalID - the id of the authenticated entity
     * @return DST - the Distributed Session Ticket
     */
    public String createSesn(String principalID) {
        String DST = this.generateDST();
        
        this.touchSesn(DST);
        this.addPrincipalToSesn(DST, principalID);

        return DST;
    }
    
    /**
     * This method generates a unique Distributed Session Ticket
     * 
     * @return DST - the Distributed Session Ticket
     */
    public String generateDST() {
        return prefix + "-" + SessionIdGenerator.getNewString();
    }
    
    /**
     * This method updates the session
     * 
     * @param DST - the Distributed Session Ticket
     */
    public void touchSesn(String DST) {
        String sql = "select lastAccessDt, maxIdleTime from authnsesn where sesnID=?";
        String updateSql = "";
        Object[] args = { DST },
               updateArgs;
        Long maxIdleTime;
        
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("ARGUMENTS number:" + args.length);
                logger.debug("ARGUMENTS 0:" + args[0]);
            }
            Map<String,Object> fields = jdbcTemplate.queryForMap(sql, args);
            Date lastAccessDt = (Date)fields.get("lastAccessDt");
            if (logger.isDebugEnabled()) {
                logger.debug("Last Access:" + lastAccessDt);
            }
            maxIdleTime = getMaxIdleTime((Long)fields.get("maxIdleTime"), lastAccessDt);
            
            
            updateSql = "update authnsesn set lastAccessDt=NOW(), maxIdleTime = ? where sesnID=?";
            updateArgs = new Object[] { maxIdleTime, DST };
            jdbcTemplate.update(updateSql, updateArgs);
        } 
        // catch if no or more than 1 results are returned
        catch (IncorrectResultSizeDataAccessException ex) {
            if (this.allowInsertOnTouch) {
                maxIdleTime = new Long(0);
                
                updateSql = "insert into authnsesn (sesnID, insertDt, lastAccessDt, maxIdleTime) values (?, NOW(), NOW(), ?)";
                updateArgs = new Object[] { DST, maxIdleTime };
                jdbcTemplate.update(updateSql, updateArgs);
            }
        }
    }
    
    
    /**
     * This method returns the greater of the stored max idle time and 
     * time since last access
     * 
     * @param oldMaxIdleTime - the previous max idle time
     * @param lastAccessDt - the timestamp of last access
     * @return the max idle time
     */
    public Long getMaxIdleTime(Long oldMaxIdleTime, Date lastAccessDt) {
        Long maxIdleTime = oldMaxIdleTime;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Max Idle:" + maxIdleTime);
        }
        long curIdleTime = System.currentTimeMillis()-lastAccessDt.getTime();
        if (logger.isDebugEnabled()) {
            logger.debug("Curr Idle:" + curIdleTime);
        }
        if (curIdleTime > maxIdleTime) {
            maxIdleTime = new Long(curIdleTime);
        }
        
        return maxIdleTime;
    }
    
    /**
     * This method appends a principal to an active session
     * 
     * @param DST - the Distributed Session Ticket
     * @param principalID - the id of the authenticated entity
     */
    public void addPrincipalToSesn(String DST, String principalID) {
        // this will fail if the record already exists 
        try {
            String updateSql = "insert into authnsesnprincipal (sesnID, principalID) values (?, ?)";
            
            jdbcTemplate.update(updateSql, new Object[] { DST, principalID });
            if (logger.isDebugEnabled()) {
                logger.debug("Added Principal to Sesn:" + principalID + " " + DST);
            }
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Principal Probably already exists:" + principalID + " " + DST);
            }
        }
    }

    /**
     * @return the prefix
     */
    public static String getPrefix() {
        return DistributedSession.prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public static void setPrefix(String prefix) {
        DistributedSession.prefix = prefix;
    }

    /**
     * @param allowInsertOnTouch the allowInsertOnTouch to set
     */
    public void setAllowInsertOnTouch(boolean allowInsertOnTouch) {
        this.allowInsertOnTouch = allowInsertOnTouch;
    }
}
