package com.sctrcd.qzr.web.resources;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sctrcd.qzr.facts.AnswerType;
import com.sctrcd.qzr.facts.Option;

@JsonInclude(Include.NON_EMPTY)
@XmlRootElement(name = "question")
public class QuestionResource extends ResourceSupport {

    private String key;
    private String text;
    private final Set<Option> options = new HashSet<>();
    private AnswerType answerType;
    private AnswerResource answer;
    
    public QuestionResource() {
    }

    public QuestionResource(String key, String text) {
        this.key = key;
        this.text = text;
    }
    
    public QuestionResource(String key, String text, AnswerType answerType) {
        this(key, text);
        this.answerType = answerType;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public Set<Option> getOptions() {
        return options;
    }
    
    public void addOptions(Collection<Option> options) {
        options.stream().forEach(option -> this.options.add(option));
    }
    
    public void addOption(Option option) {
        this.options.add(option);
    }
    
    public void addOption(String key, String value) {
        addOption(new Option(key, value));
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public AnswerResource getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerResource answer) {
        this.answer = answer;
    }

    public String toString() {
        return this.getClass().getSimpleName() 
                + ": { key=\"" + key + "\", text=\"" + text + "\" }";
    }

}
