package com.sctrcd.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides some general utility methods to make it easier to match
 * whether particular fields and values exist on a bean. Because it uses the
 * apache commons {@link BeanUtils}, it performs matches on the string values
 * rather than the actual object values. i.e.:
 * <pre>
 *     o1.property.toString().equals(filterValue.toString())
 * </pre>
 * 
 * @author Stephen Masters
 */
public class BeanMatcher {
    
    private static Logger log = LoggerFactory.getLogger(BeanMatcher.class);
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     * 
     * The filters should be defined as an array of String, where each property
     * name/value pair is a String, like so:
     * <pre>
     *   String[] filters = { &quot;uncle=Bob&quot;, &quot;aunt=Fanny&quot; };
     * </pre>
     * This is a convenience method so that matchers can be defined with minimal LOC.
     */
    public boolean matches(Object bean, String[] filters) {
        if (filters.length == 0) return true;
        return matches(bean, parseFilters(filters));
    }
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     * 
     * @param bean The bean to examine.
     * @param filters Vararg list of filters.
     * @return True if all the filter properties match on the bean.
     */
    public boolean matches(Object bean, BeanPropertyFilter... filters) {
        if (filters.length == 0) return true;
        Map<String, Object> filtermap = new HashMap<String, Object>();
        for (BeanPropertyFilter filter : filters) {
            filtermap.put(filter.getPropertyName(), filter.getPropertyValue());
        }        
        return matches(bean, filtermap);
    }
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public boolean matches(Object bean, Map<String, Object> filters) {
        if (filters.size() == 0) return true;
        if (bean == null) return false;
        try {
            Map<String, String> described = BeanUtils.describe(bean);
            log.debug(described.toString());
            return matchesProperties(described, filters);
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public boolean matchesProperties(Map<String, String> beanProperties, Map<String, Object> filters) {
        if (filters.size() == 0) return true;
        if (log.isTraceEnabled()) {
            logComparison(beanProperties, filters);
        }
        for(String key : filters.keySet()) {
            if (!beanProperties.containsKey(key) 
                    ||  !isEquivalent(filters.get(key), beanProperties.get(key))) {
                return false;
            }
        }
        // None of the filters failed to match, so it must be okay.
        return true;
    }
    
    /**
     * This method will log the bean properties and the filter properties at debug level.
     *  
     * @param beanProperties The field names and their values for a bean.
     * @param filters The filters we are looking to match.
     */
    private void logComparison(Map<String, String> beanProperties, Map<String, Object> filters) {
        StringBuilder filterValues = new StringBuilder();
        StringBuilder beanValues = new StringBuilder();
        
        for(String key : filters.keySet()) {
            filterValues.append(key + "=" + filters.get(key) + ",");
            if (beanProperties.containsKey(key)) {
                beanValues.append(key + "=" + beanProperties.get(key) + "," );
            } else {
                beanValues.append("No property.");
            }
        }
        
        if (log.isTraceEnabled()) {
            log.trace("Matching filters: " + filterValues.toString() + "\nto bean properties:" + beanValues.toString());
        }
    }

    /**
     * When a fact is retrieved from the working memory as a bean and we parse
     * its get/set methods, all properties have their first letter in lower case
     * in the usual Java Bean convention. This is usually fine, but if the first
     * letter of a fact property should be upper-case, it's much more readable
     * (particularly in the tests) if we define the field we're looking for with
     * the same text as that associated with the fact. So we lower-case the
     * first letter of the property defined in the filters.
     * <p>
     * We're deliberately losing the ability to perform case-sensitive matches
     * where the first letter makes a difference.
     * </p>
     */
    protected String lowercaseFirstLetter(String text) {
        if (text == null || text.length() == 0) {
            return text;
        } else {
            return text.substring(0, 1).toLowerCase() + text.substring(1, text.length());
        }
    }
    
    /**
     * Takes an array of <code>String</code> defined like so:
     * <pre>
     *   String[] filters = { &quot;uncle=Bob&quot;, &quot;aunt=Fanny&quot; };
     * </pre>
     * Converts this into a Map of String keys to Object values by splitting
     * each element on the "=" symbol.
     */
    private Map<String, Object> parseFilters(String[] filters) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String filter : filters) {
            String[] keyval = filter.split("=");
            if (keyval.length != 2) {
                throw new IllegalArgumentException(
                  "Filter [" + filter + "] does not parse to a key/value pair.");
            }
            map.put(lowercaseFirstLetter(keyval[0]), keyval[1]);
        }
        return map;
    }

    /**
     * As the commons BeanUtils give us the String value of each property, we
     * match on whether that String value is the same, rather than being able to
     * test whether one object truly equals the other.
     */
    protected boolean isEquivalent(Object o1, Object o2) {
        log.trace("    matching: " + o1 + " and " + o2);
        if (o1 == null && o2 == null) return true;
        if (o1 == null && o2 != null || o1 != null && o2 == null) return false;
        if (o1 == o2) return true;
        if (o1.toString().equals(o2.toString())) return true;
        return false;
    }

}
