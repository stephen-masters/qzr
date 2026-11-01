package com.sctrcd.beans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sctrcd.drools.testfacts.Product;
import org.junit.jupiter.api.Test;

class BeanMatcherTest {

    private final BeanMatcher matcher = new BeanMatcher();
    private final Product widget = new Product("Widget", 3);

    @Test
    void shouldMatchOnStringValuesOfProperties() {
        assertThat(matcher.matches(widget, new BeanPropertyFilter("name", "Widget")))
                .isTrue();
        assertThat(matcher.matches(widget, new BeanPropertyFilter("count", 3))).isTrue();
        assertThat(matcher.matches(
                        widget, new BeanPropertyFilter("name", "Widget"), new BeanPropertyFilter("count", "3")))
                .isTrue();
    }

    @Test
    void shouldNotMatchWhenAnyFilterDiffers() {
        assertThat(matcher.matches(
                        widget, new BeanPropertyFilter("name", "Widget"), new BeanPropertyFilter("count", 4)))
                .isFalse();
        assertThat(matcher.matches(widget, new BeanPropertyFilter("colour", "red")))
                .isFalse();
        assertThat(matcher.matches(null, new BeanPropertyFilter("name", "Widget")))
                .isFalse();
    }

    @Test
    void shouldMatchEverythingWithNoFilters() {
        assertThat(matcher.matches(widget)).isTrue();
    }

    @Test
    void shouldParseStringFilters() {
        assertThat(matcher.matches(widget, new String[] {"Name=Widget", "count=3"}))
                .isTrue();
        assertThatThrownBy(() -> matcher.matches(widget, new String[] {"name"}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
