package com.sctrcd.qzr.facts;

/**
 * A person's maximum heart rate.
 * 
 * @author Stephen Masters
 */
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

    /** Indicates the calculation which was used to calculate the value. */
    public String getAlgorithm() {
        return algorithm;
    }
    
    public String toString() {
        return "HrMax:{ value=" + value + ", algorithm=\"" + algorithm + "\" }";
    }
    
}
