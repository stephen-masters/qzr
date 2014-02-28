package com.sctrcd.beans;

/**
 * A name/value pair where the name is the name of a bean property and value is
 * the object that should be returned. Such that, as an example:
 * 
 * <pre>
 * anObject.get&lt;PropertyName&gt;().equals(thisFilter.getPropertyValue)
 * </pre>
 * 
 * It should be noted that in the case of the {@link BeanMatcher},
 * the comparison will be on whether the String representations of the two
 * objects are equal.
 * 
 * @author Stephen Masters
 */
public class BeanPropertyFilter {

    private String propertyName;
    private Object propertyValue;

    /**
     * Void constructor.
     */
    public BeanPropertyFilter() {
        propertyName = null;
        propertyValue = null;
    }

    /**
     * 
     * @param propertyName The name of the property we are filtering on. i.e. There should be a "get" method.
     * @param propertyValue The expected value of the property we are filtering on.
     */
    public BeanPropertyFilter(String propertyName, Object propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    /**
     * The name of the property we are filtering on. i.e. There should be a "get" method.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Set the name of the property we are filtering on. i.e. There should be a "get" method.
     */
    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    /**
     * The expected value of the property we are filtering on.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * Set the expected value of the property we are filtering on.
     */
    public void setPropertyValue(Object value) {
        this.propertyValue = value;
    }

    public String toString() {
        return "BeanProperty:{name=" + propertyName + ", value=" + propertyValue + "}";
    }

}
