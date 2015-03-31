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
package com.lmax.simpledsl;

import java.util.HashMap;
import java.util.Map;

public class DslParams extends DslValues
{
    private static final String USAGE_TOKEN = "-usage";
    private static ValueReplacer valueReplacer;

    private final DslParam[] params;
    private final Map<String,DslParam> paramsByName = new HashMap<String, DslParam>();

    public DslParams(final String[] args, final DslParam... params)
    {
        this.params = params;
        checkUsage(args, params);

        final NameValuePair[] arguments = parseArguments(args);

        for (final DslParam param : params)
        {
            paramsByName.put(param.getName().toLowerCase(), param);
        }

        int currentPosition = 0;
        for (final DslParam param : params)
        {
            if (!param.isRequired() || currentPosition >= arguments.length || !matches(arguments[currentPosition], param))
            {
                break;
            }
            currentPosition = param.getAsRequiredParam().consume(currentPosition, arguments);
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
        return argument.getName() == null || param.getName().equalsIgnoreCase(argument.getName());
    }

    public static void setValueReplacer(final ValueReplacer valueReplacer)
    {
        DslParams.valueReplacer = valueReplacer;
    }

    @Override
    public String value(final String name)
    {
        final DslParam param = getDslParam(name);
        if (param != null)
        {
            final String value = param.getAsSimpleDslParam().getValue();
            if (value != null && valueReplacer != null)
            {
                return valueReplacer.replace(value);
            }
            return value;
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

    public DslParam[] getParams()
    {
        return params;
    }

    public static String getSingleRequiredParamValue(final String[] args, final String requiredParamName)
    {
        return new DslParams(args, new RequiredParam(requiredParamName)).value(requiredParamName);
    }

    /**If you have a DSL method that doesn't require any parameters, if you declare it as taking the usual String array and
     * call checkEmpty(), then the -usage trick still works.
     */
    public static void checkEmpty(final String[] args)
    {
        new DslParams(args);
    }

    @Override
    public boolean hasValue(final String name)
    {
        final DslParam param = getDslParam(name);
        return param != null && param.hasValue();
    }

    private void checkUsage(final String[] args, final DslParam... params)
    {
        if (args != null && args.length == 1 && USAGE_TOKEN.equals(args[0]))
        {
            throw new DslParamsUsageException(params);
        }
    }

    private void checkAllRequiredParamsSupplied(final DslParam[] params)
    {
        for (final DslParam param : params)
        {
            if (!param.isValid())
            {
                throw new IllegalArgumentException("Missing value for parameter: " + param.getName());
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
