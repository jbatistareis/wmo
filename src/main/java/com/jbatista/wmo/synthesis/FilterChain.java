package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.filters.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterChain {

    private final List<FilterChainItem> chain = new ArrayList<>();
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

    public FilterChain sum(Filter filter) {
        if (!chain.contains(filter)) {
            if (chain.add(new FilterChainItem(FilterChainItem.FilterType.SUM, filter))) {
                size++;
            }
        }

        return this;
    }

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
