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
        return values != null ? values.toArray(new String[0]) : new String[0];
    }

    @Override
    public DslArg[] getParams()
    {
        return dslArgs;
    }

    private List<String> getValues(final String name)
    {
        return name != null ? valuesByName.get(name.toLowerCase()) : null;
    }

    public String[] rawArgs()
    {
        return stream(dslArgs)
                .flatMap(arg -> getValues(arg.getName()).stream()
                        .map(val -> arg.getName() + ": " + val))
                .toArray(String[]::new);
    }
}
