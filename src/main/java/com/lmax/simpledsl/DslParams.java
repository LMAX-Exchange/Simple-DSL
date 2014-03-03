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

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class DslParams
{
    private static final String USAGE_TOKEN = "-usage";
    private static ValueReplacer valueReplacer;

    private final DslParam[] params;

    public DslParams(String[] args, DslParam... params)
    {
        this.params = params;

        List<RequiredParam> requiredParams = getRequiredParams(params);
        List<OptionalParam> optionalParams = getOptionalParams(params);
        checkUsage(args, params);

        int currentPosition = 0;
        for (RequiredParam requiredParam : requiredParams)
        {
            currentPosition = requiredParam.consume(currentPosition, args);
        }

        for (int i = currentPosition; i < args.length; i++)
        {
            consumeArg(args[i], optionalParams);
        }
    }

    public static void setValueReplacer(ValueReplacer valueReplacer)
    {
        DslParams.valueReplacer = valueReplacer;
    }

    public String value(String name)
    {
        for (DslParam param : params)
        {
            if (param.getName().equals(name))
            {
                final String value = param.getValue();
                if (value != null && valueReplacer != null)
                {
                    return valueReplacer.replace(value);
                }
                return value;
            }
        }
        throw new IllegalArgumentException(name + " was not a parameter");
    }

    public int valueAsInt(String name)
    {
        return Integer.parseInt(value(name));
    }

    public long valueAsLong(String name)
    {
        return Long.valueOf(value(name));
    }

    public boolean valueAsBoolean(String name)
    {
        return Boolean.valueOf(value(name));
    }

    public BigDecimal valueAsBigDecimal(String name)
    {
        String value = value(name);
        return value != null ? new BigDecimal(value) : null;
    }

    public double valueAsDouble(String name)
    {
        return Double.valueOf(value(name));
    }

    public String valueAsParam(String name)
    {
        String value = value(name);
        return value != null ? name + ": " + value : null;
    }

    public String[] values(String name)
    {
        for (DslParam param : params)
        {
            if (param.getName().equals(name))
            {
                return param.getValues();
            }
        }
        throw new IllegalArgumentException(name + " was not a parameter");
    }

    public int[] valuesAsInts(String name)
    {
        String[] values = values(name);
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            intValues[i] = Integer.parseInt(values[i]);
        }
        return intValues;
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
    public static void checkEmpty(String[] args)
    {
        new DslParams(args);
    }

    public boolean hasValue(String name)
    {
        for (DslParam param : params)
        {
            if (param.getName().equals(name))
            {
                return param.allowMultipleValues ? param.getValues().length > 0 : param.getValue() != null;
            }
        }

        return false;
    }

    private static List<RequiredParam> getRequiredParams(final DslParam... params)
    {
        List<RequiredParam> requiredParams = new LinkedList<RequiredParam>();
        for (DslParam param : params)
        {
            if (!param.isRequired())
            {
                break;
            }
            requiredParams.add(param.getAsRequiredParam());
        }
        return requiredParams;
    }

    private static List<OptionalParam> getOptionalParams(final DslParam... params)
    {
        List<OptionalParam> optionalParams = new LinkedList<OptionalParam>();
        int i = 0;
        for (; i < params.length && params[i].isRequired(); i++)
        {
        }
        for (; i < params.length; i++)
        {
            if (params[i].isRequired())
            {
                throw new IllegalArgumentException("Required parameters must appear before optional parameters");
            }
            optionalParams.add(params[i].getAsOptionalParam());
        }
        return optionalParams;
    }

    private static void consumeArg(final String arg, List<OptionalParam> optionalParams)
    {
        if (arg == null)
        {
            return;
        }
        NameValuePair nameValue = new NameValuePair(arg);
        for (OptionalParam param : optionalParams)
        {
            if (param.matches(nameValue.getName()))
            {
                param.addValue(nameValue.getValue());
                return;
            }
        }
        throw new IllegalArgumentException("Unexpected argument " + arg);
    }

    private void checkUsage(String[] args, DslParam... params)
    {
        if (args != null && args.length == 1 && USAGE_TOKEN.equals(args[0]))
        {
            throw new DslParamsUsageException(params);
        }
    }
}
