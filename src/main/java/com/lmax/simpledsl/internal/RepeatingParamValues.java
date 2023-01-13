package com.lmax.simpledsl.internal;

import com.lmax.simpledsl.api.DslArg;
import com.lmax.simpledsl.api.RepeatingGroup;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;

class RepeatingParamValues implements RepeatingGroup
{
    private final DslArg[] dslArgs;
    private final Map<String, List<String>> valuesByName;

    RepeatingParamValues(final DslArg[] dslArgs, final Map<String, List<String>> valuesByName)
    {
        this.dslArgs = dslArgs;
        this.valuesByName = valuesByName;
    }

    @Override
    public boolean hasValue(final String name)
    {
        final List<String> values = getValues(name);
        return !(values == null || values.isEmpty());

    }

    @Override
    public boolean hasParam(final String name)
    {
        return stream(dslArgs).anyMatch(arg -> arg.getName().equalsIgnoreCase(name));
    }

    @Override
    public String value(final String name)
    {
        final String[] strings = values(name);
        if (strings.length > 1)
        {
            throw new IllegalArgumentException("values() should be used when multiple values are allowed");
        }
        return strings.length > 0 ? strings[0] : null;
    }

    @Override
    public String[] values(final String name)
    {
        final List<String> values = getValues(name);
        return values != null ? values.toArray(new String[0]) : new String[0];
    }

    @Override
    public DslArg[] getParams()
    {
        return dslArgs;
    }

    private List<String> getValues(final String name)
    {

        if (name == null || stream(dslArgs).noneMatch(arg -> arg.getName().equalsIgnoreCase(name)))
        {
            throw new IllegalArgumentException(String.format("Parameter %s does not exist in this repeating group", name));
        }

        return valuesByName.get(name.toLowerCase());
    }
}
