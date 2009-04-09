/*
 * Created on Feb 16, 2006
 *
 */
package org.kuali.kfs.module.purap.util.cxml;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CxmlExtrinsic {

    private String name;
    private String value;

    public CxmlExtrinsic() {
    }

    public CxmlExtrinsic(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("Name", getName());
        toString.append("Value", getValue());
        return toString.toString();
    }
}
