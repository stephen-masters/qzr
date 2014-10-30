package com.sctrcd.qzr.facts;

public class HrMax {

    private final Integer value;
    private final String algorithm;
    
    public HrMax(Integer value, String algorithm) {
        this.value = value;
        this.algorithm = algorithm;
    }

    public Integer getValue() {
        return value;
    }

    public String getAlgorithm() {
        return algorithm;
    }
    
    public String toString() {
        return "HrMax:{ value=" + value + ", algorithm=\"" + algorithm + "\" }";
    }
    
}
