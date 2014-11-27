package com.sctrcd.qzr.facts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A collection of {@link Known}, which maps the key to the object.
 * 
 * @author Stephen masters
 */
@XmlRootElement(name = "knowns")
public class KnownList {

    private Map<String, Known<?>> knowns;

    public KnownList() {

    }

    public KnownList(Collection<Known<?>> knowns) {
        this.knowns = new HashMap<>();
        setKnowns(knowns);
    }

    @XmlElement(name = "known")
    public Map<String, Known<?>> getKnowns() {
        return knowns;
    }

    /**
     * Get a reference to a {@link Known} with the specified key. Null if there
     * is none.
     */
    public Known<?> getKnown(String key) {
        return knowns.get(key);
    }

    public void setKnowns(Collection<Known<?>> knowns) {
        if (this.knowns == null)
            this.knowns = new HashMap<>();
        for (Known<?> known : knowns) {
            this.knowns.put(known.getKey(), known);
        }
    }

    public void add(Known<?> known) {
        if (this.knowns == null)
            this.knowns = new HashMap<>();
        this.knowns.put(known.getKey(), known);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(KnownList.class.getSimpleName()
                + ": { knowns=[ ");
        for (Known<?> q : knowns.values()) {
            sb.append(q.toString());
            sb.append(",  ");
        }
        if (knowns == null || knowns.size() == 0) {
            return sb.toString() + " ]}";
        } else {
            return sb.substring(0, sb.length() - 3) + " ]}";
        }
    }

}
