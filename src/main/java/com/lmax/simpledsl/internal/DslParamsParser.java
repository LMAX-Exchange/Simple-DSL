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
import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.SimpleDslArg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parser for transforming a specification of {@link DslArg DslArgs} and a set of provided {@link String} values into
 * usable {@link DslParams}.
 */
public class DslParamsParser
{
    /**
     * Construct new {@link DslParams} from the given {@link DslArg DslArgs} and values.
     *
     * @param args    the values
     * @param dslArgs the {@link DslArg} specifications
     * @return the parsed {@link DslParams}
     * @throws IllegalArgumentException if any of the provided {@link String} arguments are invalid
     */
    public DslParams parse(final String[] args, final DslArg... dslArgs)
    {
        final Deque<NameValuePair> arguments = parseArgumentValues(args);

        final ArgumentProcessor argumentProcessor = new ArgumentProcessor();
        argumentProcessor.drain(dslArgs, arguments);

        final Map<String, DslParam> paramsByName = argumentProcessor.collect(dslArgs);
        return new DslParamsImpl(dslArgs, paramsByName);
    }

    private static Deque<NameValuePair> parseArgumentValues(final String[] args)
    {
        final ArrayDeque<NameValuePair> nameValuePairs = new ArrayDeque<>();
        for (String arg : args)
        {
            final NameValuePair nameValuePair = NameValuePair.fromArgumentString(arg);
            nameValuePairs.add(nameValuePair);
        }
        return nameValuePairs;
    }

    private static final class ArgumentProcessor
    {
        private final SimpleArgumentProcessor simpleProcessor = new SimpleArgumentProcessor(new HashMap<>(), "Missing value for parameter: %s");
        private final RepeatingGroupArgumentProcessor groupProcessor = new RepeatingGroupArgumentProcessor(new HashMap<>());

        void drain(final DslArg[] args, final Deque<NameValuePair> arguments)
        {
            for (final DslArg arg : args)
            {
                final NameValuePair argument = arguments.peekFirst();
                if (argument != null)
                {
                    if (argument == NameValuePair.NULL)
                    {
                        arguments.pollFirst();
                        continue;
                    }

                    if (invalidNamedParameter(arg, argument))
                    {
                        break;
                    }

                    consume(arg, arguments);
                }
            }

            final Map<String, DslArg> argsByName = new HashMap<>();
            for (DslArg dslArg : args)
            {
                if (argsByName.put(dslArg.getName().toLowerCase(), dslArg) != null)
                {
                    throw new IllegalArgumentException("Duplicate parameter '" + dslArg.getName() + "'");
                }
            }

            while (!arguments.isEmpty())
            {
                final NameValuePair argument = arguments.peekFirst();
                if (argument == NameValuePair.NULL)
                {
                    arguments.pollFirst();
                    continue;
                }

                if (argument.name == null)
                {
                    throw new IllegalArgumentException("Unexpected ambiguous argument " + argument.originalValue);
                }

                final DslArg arg = argsByName.get(argument.name.toLowerCase());
                if (arg == null)
                {
                    throw new IllegalArgumentException("Unexpected argument " + argument.originalValue);
                }

                consume(arg, arguments);
            }
        }

        Map<String, DslParam> collect(final DslArg[] args)
        {
            final Map<String, DslParam> map = new HashMap<>();
            for (final DslArg dslArg : args)
            {
                // TODO: work out how we can avoid this cast :/
                final DslParam val = dslArg instanceof RepeatingArgGroup
                        ? groupProcessor.collect((RepeatingArgGroup) dslArg)
                        : simpleProcessor.collect((SimpleDslArg) dslArg);
                map.put(dslArg.getName().toLowerCase(), val);
            }
            return map;
        }

        private void consume(final DslArg arg, final Deque<NameValuePair> arguments)
        {
            if (arg instanceof RepeatingArgGroup)
            {
                // TODO: work out how we can avoid this cast :/
                groupProcessor.consume((RepeatingArgGroup) arg, arguments);
            }
            else
            {
                simpleProcessor.consume(arg, arguments);
            }
        }

        private boolean invalidNamedParameter(final DslArg arg, final NameValuePair argument)
        {
            return argument.name != null &&
                    ((arg.isRequired() && !arg.getName().equals(argument.name) || !arg.isRequired()));
        }
    }

    private static final class SimpleArgumentProcessor
    {
        private final Map<DslArg, List<String>> valuesByArg;
        private final String requiredParamMissingError;

        SimpleArgumentProcessor(final Map<DslArg, List<String>> valuesByArg, final String requiredParamMissingError)
        {
            this.valuesByArg = valuesByArg;
            this.requiredParamMissingError = requiredParamMissingError;
        }

        void consume(final DslArg arg, final Deque<NameValuePair> args)
        {
            final List<String> values = valuesByArg.computeIfAbsent(arg, k -> new ArrayList<>());
            while (consumeSingleParam(arg, args, values))
            {
                if (!arg.isAllowMultipleValues())
                {
                    break;
                }
            }
        }

        SimpleDslParam collect(final SimpleDslArg arg)
        {
            final List<String> values = valuesByArg.getOrDefault(arg, Collections.emptyList());
            final List<String> validatedValues = validateSimpleArg(arg, values);
            return new SimpleDslParam(arg.getName(), validatedValues);
        }

        private static boolean consumeSingleParam(final DslArg arg, final Deque<NameValuePair> args, final List<String> values)
        {
            if (!args.isEmpty())
            {
                final NameValuePair nameValue = args.peekFirst();
                if (matches(arg, nameValue))
                {
                    addValue(arg, nameValue.value, values);
                    args.pollFirst();
                    return true;
                }
            }
            return false;
        }

        private static boolean matches(final DslArg arg, final NameValuePair value)
        {
            return (value != NameValuePair.NULL) &&
                    (value.name == null || arg.getName().equalsIgnoreCase(value.name));
        }

        private static void addValue(final DslArg arg, final String value, final List<String> values)
        {
            if (arg.isAllowMultipleValues())
            {
                final String[] vals = value.split(Pattern.quote(arg.getMultipleValueSeparator()));
                for (final String singleValue : vals)
                {
                    addSingleValue(arg, singleValue.trim(), values);
                }
            }
            else
            {
                addSingleValue(arg, value, values);
            }
        }

        private static void addSingleValue(final DslArg arg, final String value, final List<String> values)
        {
            checkCanAddValue(arg, values);
            values.add(checkValidValue(arg, value));
        }

        private static void checkCanAddValue(final DslArg arg, final List<String> values)
        {
            if (!arg.isAllowMultipleValues() && values.size() == 1)
            {
                throw new IllegalArgumentException("Multiple " + arg.getName() + " parameters are not allowed");
            }
        }

        private List<String> validateSimpleArg(final SimpleDslArg arg, final List<String> values)
        {
            if (values.isEmpty())
            {
                if (arg.isRequired())
                {
                    throw new IllegalArgumentException(String.format(requiredParamMissingError, arg.getName()));
                }
                else
                {
                    return arg.getDefaultValue() == null
                            ? Collections.emptyList()
                            : Collections.singletonList(arg.getDefaultValue());
                }
            }
            return values;
        }
    }

    private static final class RepeatingGroupArgumentProcessor
    {
        final Map<DslArg, List<RepeatingParamValues>> groupsByArg;

        RepeatingGroupArgumentProcessor(final Map<DslArg, List<RepeatingParamValues>> groupsByArg)
        {
            this.groupsByArg = groupsByArg;
        }

        void consume(final RepeatingArgGroup groupArg, final Deque<NameValuePair> arguments)
        {
            final Map<DslArg, List<String>> valuesByArg = new HashMap<>();
            final SimpleArgumentProcessor processor = new SimpleArgumentProcessor(valuesByArg, "Did not supply a value for %s in group " + groupArg.getName());

            processor.consume(groupArg.getIdentity(), arguments);

            final Map<String, SimpleDslArg> argsByName = new HashMap<>();
            argsByName.put(groupArg.getIdentity().getName(), groupArg.getIdentity());
            for (SimpleDslArg dslArg : groupArg.getOtherArgs())
            {
                if (argsByName.put(dslArg.getName().toLowerCase(), dslArg) != null)
                {
                    throw new IllegalArgumentException("Duplicate parameter '" + dslArg.getName() + "' in group " + groupArg.getName());
                }
            }

            while (!arguments.isEmpty())
            {
                final NameValuePair argument = arguments.peekFirst();
                if (argument == NameValuePair.NULL)
                {
                    arguments.pollFirst();
                    continue;
                }

                if (argument.name == null)
                {
                    throw new IllegalArgumentException("Unexpected ambiguous argument " + argument.originalValue);
                }

                final DslArg arg = argsByName.get(argument.name.toLowerCase());
                if (arg == null)
                {
                    break;
                }

                final List<String> argValues = valuesByArg.computeIfAbsent(arg, k -> new ArrayList<>());
                if (!argValues.isEmpty() && !arg.isAllowMultipleValues())
                {
                    break;
                }

                SimpleArgumentProcessor.addValue(arg, argument.value, argValues);
                arguments.pollFirst();
            }

            // TODO: this whole thing here is a bit hacky!
            final Map<String, List<String>> valuesByName = new HashMap<>();
            for (final SimpleDslArg simpleDslArg : argsByName.values())
            {
                final SimpleDslParam param = processor.collect(simpleDslArg);
                if (param.hasValue())
                {
                    valuesByName.put(param.getName().toLowerCase(), param.getValuesAsList());
                }
            }

            final DslArg[] dslArgs = new DslArg[groupArg.getOtherArgs().length + 1];
            dslArgs[0] = groupArg.getIdentity();
            System.arraycopy(groupArg.getOtherArgs(), 0, dslArgs, 1, groupArg.getOtherArgs().length);
            groupsByArg.computeIfAbsent(groupArg, k -> new ArrayList<>()).add(new RepeatingParamValues(dslArgs, valuesByName));
        }

        RepeatingParamGroup collect(final RepeatingArgGroup arg)
        {
            return new RepeatingParamGroup(arg.getName(), groupsByArg.getOrDefault(arg, Collections.emptyList()));
        }
    }

    private static String checkValidValue(final DslArg arg, final String value)
    {
        if (arg.getAllowedValues() != null)
        {
            for (final String allowedValue : arg.getAllowedValues())
            {
                if (allowedValue.equalsIgnoreCase(value))
                {
                    return allowedValue;
                }
            }
            throw new IllegalArgumentException(arg.getName() + " parameter value '" + value + "' must be one of: " + Arrays.toString(arg.getAllowedValues()));
        }
        return value;
    }
}
