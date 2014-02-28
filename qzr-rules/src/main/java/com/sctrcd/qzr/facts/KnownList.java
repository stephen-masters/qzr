package com.sctrcd.qzr.facts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "knowns")
public class KnownList {

    private List<Known<?>> knowns;

    public KnownList() {

    }

    public KnownList(Collection<Known<?>> knowns) {
        this.knowns = new ArrayList<>(knowns);
    }

    @XmlElement(name = "known")
    public List<Known<?>> getKnowns() {
        return knowns;
    }

    public void setKnowns(List<Known<?>> knowns) {
        this.knowns = knowns;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("KnownList: { knowns=[ ");
        for (Known<?> q : knowns) {
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
