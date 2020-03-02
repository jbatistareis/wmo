package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.filters.Filter;

public class FilterChainItem {

    public enum FilterType {SUM, LINK}

    private final FilterType filterType;
    private final Filter filter;

    FilterChainItem(FilterType filterType, Filter filter) {
        this.filterType = filterType;
        this.filter = filter;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    public int hashCode() {
        return filter.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FilterChainItem) && filter.equals(((FilterChainItem) obj).getFilter());
    }

}
