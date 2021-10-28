package com.lmax.simpledsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Define a group of parameters that can be repeated 0 or more times. The first parameter in the group must be a {@link RequiredParam} and is used to identify the start
 * of the group in arguments and when retrieving the groups from {@link DslParams#valuesAsGroup(String)}. Other parameters may follow the first parameter in any order.
 *
 * <p>Multiple {@code RepeatingParamGroup}s can be used within the same {@code DslParams} but they cannot be nested.</p>
 *
 * <pre>{@code
 * public void createUsers(String... args) {
 *     DslParams params = new DslParams(
 *         args,
 *         new RepeatingParamGroup(
 *             new RequiredParam("user"),
 *             new OptionalParam("password").setDefault("aPassword")
 *         ));
 *     for (RepeatingGroup user : params.valueAsGroup("user")) {
 *         driver.createUser(user.value("user"), user.value("password"));
 *     }
 * }
 * }</pre>
 *
 * @see RepeatingGroup
 * @see DslParams#valuesAsGroup(String)
 */
public class RepeatingParamGroup extends DslParam
{
    private final SimpleDslParam identity;
    private final Map<String, SimpleDslParam> paramsByName = new HashMap<>();
    private final List<RepeatingGroup> values = new ArrayList<>();

    /**
     * Create a new {@code RepeatingParamGroup}.
     *
     * @param firstParam the parameter that marks the start of the group.
     * @param params     the other parameters in the group.
     */
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

    /**
     * Return the value groups supplied for this repeating group.
     *
     * @return an array of {@link RepeatingGroup} instances, one for each set of values provided for this repeating group.
     */
    public RepeatingGroup[] values()
    {
        return values.toArray(new RepeatingGroup[values.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     */
    @Override
    public int consume(final int startingPosition, final NameValuePair... arguments)
    {
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
            else
            {
                currentPosition++;
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
