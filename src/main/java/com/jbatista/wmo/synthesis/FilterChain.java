package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.filters.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FilterChain {

    private final List<FilterChainItem> chain = new ArrayList<>();
    private Iterator<FilterChainItem> chainIterator;
    private FilterChainItem chainItem;
    private double output;
    private boolean first;

    public double getResult(double input) {
        if (chain.isEmpty()) {
            return input;
        }

        chainIterator = chain.iterator();
        output = 0;
        first = true;

        while (chainIterator.hasNext()) {
            chainItem = chainIterator.next();

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

    public FilterChain sum(Filter filter) {
        if (!chain.contains(filter)) {
            chain.add(new FilterChainItem(FilterChainItem.FilterType.SUM, filter));
        }

        return this;
    }

    public FilterChain link(Filter filter) {
        if (!chain.contains(filter)) {
            chain.add(new FilterChainItem(FilterChainItem.FilterType.LINK, filter));
        }

        return this;
    }

    public boolean remove(Filter filter) {
        return chain.remove(filter);
    }

    public void swap(Filter filter1, Filter filter2) {
        Collections.swap(chain, chain.indexOf(filter1), chain.indexOf(filter2));
    }

}
