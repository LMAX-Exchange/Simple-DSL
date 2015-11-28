package com.lmax.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepeatingParamGroup extends DslParam
{
    private final SimpleDslParam identity;
    private final Map<String, SimpleDslParam> paramsByName = new HashMap<>();
    private final List<RepeatingGroup> values = new ArrayList<>();

    public RepeatingParamGroup(final RequiredParam firstParam, final SimpleDslParam... params)
    {
        this.identity = firstParam;
        paramsByName.put(identity.getName(), identity);
        for (final SimpleDslParam param : params)
        {
            paramsByName.put(param.getName(), param);
        }
    }

    @Override
    public SimpleDslParam getAsSimpleDslParam()
    {
        return null;
    }

    @Override
    public RepeatingParamGroup asRepeatingParamGroup()
    {
        return this;
    }

    public RepeatingGroup[] values()
    {
        return values.toArray(new RepeatingGroup[values.size()]);
    }

    @Override
    public int consume(final int startingPosition, final NameValuePair... arguments)    {
        final RepeatingGroup group = new RepeatingGroup();
        int currentPosition = startingPosition;
        while (currentPosition < arguments.length)
        {
            final NameValuePair argument = arguments[currentPosition];
            if (argument != null)
            {
                final SimpleDslParam param = paramsByName.get(argument.getName());
                if (param != null)
                {
                    if (group.hasValue(argument.getName()) && !param.allowMultipleValues)
                    {
                        break;
                    }
                    param.checkValidValue(argument.getValue());
                    group.addValue(argument.getName(), argument.getValue());
                    currentPosition++;
                }
                else
                {
                    break;
                }
            }
        }
        for (final SimpleDslParam param : paramsByName.values())
        {
            if (!group.hasValue(param.getName()))
            {
                if (param.isRequired())
                {
                    throw new IllegalArgumentException("Did not supply a value for " + param.getName() + " in group " + identity.getName());
                }
                else if (param.getDefaultValue() != null)
                {
                    group.addValue(param.getName(), param.getDefaultValue());
                }
            }
        }
        values.add(group);
        return currentPosition;
    }

    @Override
    public boolean hasValue()
    {
        return !values.isEmpty();
    }

    @Override
    public String getName()
    {
        return identity.getName();
    }
}
