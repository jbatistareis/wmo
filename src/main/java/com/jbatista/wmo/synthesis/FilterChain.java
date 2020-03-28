package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.filter.Filter;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Contains a series of filters applied to a PCM sample.
 * <p>Filters are added in two ways, each one with a different effect:</p>
 * <ul>
 *     <li>Filters added with {@link #sum(Filter) sum} are going to be applied on the sample individually, and the result will be added to the output, preserving the input.</li>
 *     <li>Filter added with {@link #link(Filter) link} are going to be applied to the entire output, modifying the input.</li>
 * </ul>
 *
 * @see Filter
 */
public class FilterChain {

    private final LinkedList<FilterChainItem> chain = new LinkedList<>();
    private FilterChainItem chainItem;
    private double output;
    private int index = 0;
    private int size = 0;
    private boolean first;

    public double getResult(double input) {
        if (chain.isEmpty()) {
            return input;
        }

        output = 0;
        first = true;

        for (index = 0; index < size; index++) {
            chainItem = chain.get(index);

            switch (chainItem.getFilterType()) {
                case SUM:
                    output += chainItem.getFilter().apply(input);
                    break;

                case LINK:
                    if (first) {
                        output = input;
                        first = false;
                    }

                    output = chainItem.getFilter().apply(output);
                    break;

                default:
                    break;
            }
        }

        return output;
    }

    public void clear() {
        chain.clear();
    }

    /**
     * Filters added with this method are going to sum the result to the output, leaving the input intact for the next filter.
     *
     * @param filter The filter to be added to the chain.
     * @return The filter chain.
     */
    public FilterChain sum(Filter filter) {
        if (!chain.contains(filter)) {
            if (chain.add(new FilterChainItem(FilterChainItem.FilterType.SUM, filter))) {
                size++;
            }
        }

        return this;
    }

    /**
     * Filters added with this method are going to send the result to the next filter, losing the original input.
     *
     * @param filter The filter to be linked to the chain.
     * @return The filter chain.
     */
    public FilterChain link(Filter filter) {
        if (!chain.contains(filter)) {
            if (chain.add(new FilterChainItem(FilterChainItem.FilterType.LINK, filter))) {
                size++;
            }
        }

        return this;
    }

    public boolean remove(Filter filter) {
        if (chain.remove(filter)) {
            size--;
            return true;
        }

        return false;
    }

    public void swap(Filter filter1, Filter filter2) {
        Collections.swap(chain, chain.indexOf(filter1), chain.indexOf(filter2));
    }

}
