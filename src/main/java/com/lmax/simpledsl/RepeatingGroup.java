package com.lmax.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepeatingGroup extends DslValues
{
    private final Map<String, List<String>> valuesByName = new HashMap<>();

    public void addValue(final String name, final String value)
    {
        List<String> values = getValues(name);
        if (values == null)
        {
            values = new ArrayList<>();
            valuesByName.put(name.toLowerCase(), values);
        }
        values.add(value);
    }

    @Override
    public boolean hasValue(final String name)
    {
        return valuesByName.containsKey(name.toLowerCase());
    }

    @Override
    public String value(final String name)
    {
        final String[] strings = values(name);
        return strings.length > 0 ? strings[0] : null;
    }

    @Override
    public String[] values(final String name)
    {
        final List<String> values = getValues(name);
        return values != null ? values.toArray(new String[values.size()]) : new String[0];
    }

    private List<String> getValues(final String name)
    {
        return name != null ? valuesByName.get(name.toLowerCase()) : null;
    }
}
