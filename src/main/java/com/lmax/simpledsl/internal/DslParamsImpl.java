/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.simpledsl.internal;

import com.lmax.simpledsl.api.DslArg;
import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.RepeatingGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.stream;

/**
 * The internal implementation of {@link DslParams}.
 */
public final class DslParamsImpl implements DslParams
{
    private final DslParam[] params;
    private final Map<String, DslParam> paramsByName = new HashMap<>();

    /**
     * Create a new DslParams to define the supported dsl language.
     *
     * @param args   the arguments supplied by the test.
     * @param params the supported parameters.
     * @throws IllegalArgumentException if an invalid parameter is specified
     */
    public DslParamsImpl(final String[] args, final DslArg... params)
    {
        this(args, stream(params)
                .map(arg -> arg.fold(RequiredParam::new, OptionalParam::new, RepeatingParamGroup::new))
                .toArray(DslParam[]::new));
    }

    DslParamsImpl(final String[] args, final DslParam... params)
    {
        this.params = params;

        final NameValuePair[] arguments = parseArguments(args);

        for (final DslParam param : params)
        {
            paramsByName.put(param.getArg().getName().toLowerCase(), param);
        }

        int currentPosition = 0;

        final boolean allParamsOptional = stream(params).map(DslParam::getArg).allMatch(DslArg::isOptional);
        final boolean allArgsPositional = stream(arguments).filter(Objects::nonNull)
                .map(NameValuePair::getName)
                .allMatch(Objects::isNull);
        if (allParamsOptional && allArgsPositional && params.length == args.length)
        {
            for (final DslParam param : params)
            {
                currentPosition = param.consume(currentPosition, arguments);
            }
        }

        for (final DslParam param : params)
        {
            if (!param.getArg().isRequired() || currentPosition >= arguments.length || !matches(arguments[currentPosition], param))
            {
                break;
            }
            currentPosition = param.consume(currentPosition, arguments);
        }

        while (currentPosition < args.length)
        {
            if (arguments[currentPosition] == null)
            {
                currentPosition++;
                continue;
            }
            final DslParam param = getDslParam(arguments[currentPosition].getName());
            if (param != null)
            {
                currentPosition = param.consume(currentPosition, arguments);
            }
            else
            {
                throw new IllegalArgumentException("Unexpected argument " + arguments[currentPosition]);
            }
        }

        checkAllRequiredParamsSupplied(params);
    }

    private boolean matches(final NameValuePair argument, final DslParam param)
    {
        return argument.getName() == null || param.getArg().getName().equalsIgnoreCase(argument.getName());
    }

    @Override
    public String value(final String name)
    {
        final DslParam param = getDslParam(name);
        if (param != null)
        {
            return param.getAsSimpleDslParam().getValue();
        }
        throw new IllegalArgumentException(name + " was not a parameter");
    }

    @Override
    public String[] values(final String name)
    {
        final DslParam param = getDslParam(name);
        if (param != null)
        {
            return param.getAsSimpleDslParam().getValues();
        }
        throw new IllegalArgumentException(name + " was not a parameter");
    }

    @Override
    public RepeatingGroup[] valuesAsGroup(final String groupName)
    {
        final DslParam param = getDslParam(groupName);
        if (param == null)
        {
            throw new IllegalArgumentException(groupName + " was not a parameter");
        }
        final RepeatingParamGroup repeatingParamGroup = param.asRepeatingParamGroup();
        if (repeatingParamGroup == null)
        {
            throw new IllegalArgumentException(groupName + " was not a repeating group");
        }
        return repeatingParamGroup.values();
    }

    /**
     * Get the supported parameters. Supplied values will have been parsed into the parameters.
     *
     * @return the array of {@link DslParam} instances supplied to the constructor.
     */
    public DslParam[] getParams()
    {
        return params;
    }

    @Override
    public boolean hasValue(final String name)
    {
        final DslParam param = getDslParam(name);
        return param != null && param.hasValue();
    }

    private void checkAllRequiredParamsSupplied(final DslParam[] params)
    {
        for (final DslParam param : params)
        {
            if (!param.isValid())
            {
                throw new IllegalArgumentException("Missing value for parameter: " + param.getArg().getName());
            }
        }
    }

    private NameValuePair[] parseArguments(final String[] args)
    {
        final NameValuePair[] arguments = new NameValuePair[args.length];
        for (int i = 0; i < args.length; i++)
        {
            arguments[i] = args[i] != null ? new NameValuePair(args[i]) : null;
        }
        return arguments;
    }

    private DslParam getDslParam(final String name)
    {
        return name != null ? paramsByName.get(name.toLowerCase()) : null;
    }
}
