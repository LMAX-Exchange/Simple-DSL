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

import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.RepeatingGroup;
import com.lmax.simpledsl.api.SimpleDslArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;

class RepeatingParamGroup extends DslParam
{
    protected final RepeatingArgGroup identity;
    protected final Map<String, SimpleDslParam<?, ?>> paramsByName = new HashMap<>();
    private final List<RepeatingParamValues> values = new ArrayList<>();

    RepeatingParamGroup(final RequiredParam firstParam, final SimpleDslParam<?, ?>... params)
    {
        this.identity = new RepeatingArgGroup(firstParam.getArg(), stream(params)
                .map(SimpleDslParam::getArg)
                .toArray(SimpleDslArg[]::new));

        paramsByName.put(firstParam.getArg().getName(), firstParam);
        Arrays.stream(params)
                .forEach(param -> paramsByName.put(param.getArg().getName(), param));
    }

    RepeatingParamGroup(final RepeatingArgGroup group)
    {
        this(
                new RequiredParam(group.getIdentity()),
                stream(group.getOtherArgs())
                        .map(arg -> (SimpleDslParam<?, ?>) arg.fold(
                                RequiredParam::new,
                                OptionalParam::new,
                                invalid ->
                                {
                                    throw new IllegalArgumentException("Cannot nest RepeatingArgGroups");
                                })
                        )
                        .toArray(SimpleDslParam[]::new)
        );
    }

    @Override
    public RepeatingArgGroup getArg()
    {
        return identity;
    }

    @Override
    public SimpleDslParam<?, ?> getAsSimpleDslParam()
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
     * @return an array of {@link RepeatingParamValues} instances, one for each set of values provided for this repeating group.
     */
    public RepeatingGroup[] values()
    {
        return values.toArray(new RepeatingParamValues[0]);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     */
    @Override
    public int consume(final int startingPosition, final NameValuePair... arguments)
    {
        final RepeatingParamValues group = new RepeatingParamValues();
        int currentPosition = startingPosition;
        while (currentPosition < arguments.length)
        {
            final NameValuePair argument = arguments[currentPosition];
            if (argument != null)
            {
                final SimpleDslParam<?, ?> param = paramsByName.get(argument.getName());
                if (param != null)
                {
                    if (group.hasValue(argument.getName()) && !param.getArg().isAllowMultipleValues())
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
        for (final SimpleDslParam<?, ?> param : paramsByName.values())
        {
            if (!group.hasValue(param.getArg().getName()))
            {
                if (param.getArg().isRequired())
                {
                    throw new IllegalArgumentException("Did not supply a value for " + param.getArg().getName() + " in group " + identity.getName());
                }
                else if (param.getDefaultValue() != null)
                {
                    group.addValue(param.getArg().getName(), param.getDefaultValue());
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

}
