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

/**
 * The main entry point for defining the DSL language. Create a DslParams instance with the supplied arguments and the supported params.
 * The supplied values can then be retrieved using {@link DslParams#value(String)} and related methods.
 *
 * <pre>{@code
 *     public void createUser(String... args) {
 *         DslParams params = new DslParams(args,
 *                                          new RequiredParam("user"),
 *                                          new OptionalParam("password"));
 *         getDriver().createUser(params.value("user"), params.valueAsOptional("password"));
 *     }
 * }</pre>
 */
public class DslParams extends DslValues
{
    private static final String USAGE_TOKEN = "-usage";

    private final DslParam[] params;
    private final Map<String,DslParam> paramsByName = new HashMap<>();

    /**
     * Create a new DslParams to define the supported dsl language.
     *
     * @param args the arguments supplied by the test.
     * @param params the supported parameters.
     */
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
        completedParsingArguments();
    }

    private boolean matches(final NameValuePair argument, final DslParam param)
    {
        return argument.getName() == null || param.getName().equalsIgnoreCase(argument.getName());
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


    /**
     * Retrieve the sets of values supplied for a {@link RepeatingParamGroup} or an empty array if no values were supplied. {@code RepeatingParamGroup} requires that it's first parameter
     * be a {@link RequiredParam} and the group as a whole is referenced using the name of that parameter. e.g.
     *
     * <pre>{@code
     *   DslParams params = new DslParams(args, new RepeatingParamGroup(new RequiredParam("user"),
     *                                                                  new OptionalParam("password")));
     *   RepeatingGroup[] usersToCreate = params.valuesAsGroup("user");
     * }</pre>
     *
     * @param groupName the name of the first required parameter.
     * @return an array of RepeatingGroup instances, one for each set of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a RepeatingParamGroup parameter.
     */
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

    /**
     * A shorthand way to create a {@code DslParams} instance that accepts a single required parameter and return the value that was supplied for that parameter.
     *
     * @param args the arguments supplied by the test.
     * @param requiredParamName the name of the required parameter.
     * @return the value supplied for the parameter.
     */
    public static String getSingleRequiredParamValue(final String[] args, final String requiredParamName)
    {
        return new DslParams(args, new RequiredParam(requiredParamName)).value(requiredParamName);
    }

    /**
     * Utility method for defining a DSL method that doesn't accept any arguments. This is an alternative to removing the {@code String... args} parameter entirely
     * when a consistent public API is desired.
     *
     * @param args the parameters provided.
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

    private void completedParsingArguments()
    {
        for (final DslParam param : params)
        {
            final SimpleDslParam simpleDslParam = param.getAsSimpleDslParam();
            if (simpleDslParam != null)
            {
                simpleDslParam.completedParsing();
            }
        }
    }

    private DslParam getDslParam(final String name)
    {
        return name != null ? paramsByName.get(name.toLowerCase()) : null;
    }
}
