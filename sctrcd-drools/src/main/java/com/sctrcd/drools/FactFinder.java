package com.sctrcd.drools;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

import com.sctrcd.beans.BeanMatcher;
import com.sctrcd.beans.BeanPropertyFilter;

public class FactFinder<T> {

    private BeanMatcher beanMatcher = new BeanMatcher();

    private Class<?> classToFind;
    
    public FactFinder(Class<?> classToFind) {
        this.classToFind = classToFind;
    }

    /**
     * An assertion that a fact of the expected class with specified properties
     * is in working memory.
     * 
     * @param session
     *            A {@link KnowledgeSession} in which we are looking for the
     *            fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    public List<T> findFacts(final KieSession session, final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().equals(classToFind) 
                        && beanMatcher.matches(object, expectedProperties);
            }
        };

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        List<T> facts = new ArrayList<T>();
        for (FactHandle handle : factHandles) {
            facts.add((T) session.getObject(handle));
        }
        return facts;
    }
    
    @SuppressWarnings("unchecked")
    public List<T> findFacts(final KieSession session) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().equals(classToFind);
            }
        };

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        List<T> facts = new ArrayList<T>();
        for (FactHandle handle : factHandles) {
            facts.add((T) session.getObject(handle));
        }
        return facts;
    }
    
    public void deleteFacts(final KieSession session, final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().equals(classToFind) 
                        && beanMatcher.matches(object, expectedProperties);
            }
        };

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        for (FactHandle handle : factHandles) {
            session.delete(handle);
        }
    }

}
