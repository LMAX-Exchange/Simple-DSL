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

import java.util.Map;
import java.util.Optional;

/**
 * The internal implementation of {@link DslParams}.
 */
final class DslParamsImpl implements DslParams
{
    private final DslArg[] args;
    private final Map<String, DslParam> paramsByName;

    DslParamsImpl(final DslArg[] args, final Map<String, DslParam> paramsByName)
    {
        this.args = args;
        this.paramsByName = paramsByName;
    }

    @Override
    public String value(final String name)
    {
        final DslParam param = getDslParam(name);
        return param.getAsSimpleDslParam().getValue();
    }

    @Override
    public String[] values(final String name)
    {
        final DslParam param = getDslParam(name);
        return param.getAsSimpleDslParam().getValues();
    }

    @Override
    public RepeatingGroup[] valuesAsGroup(final String groupName)
    {
        final DslParam param = getDslParam(groupName);
        final RepeatingParamGroup repeatingParamGroup = param.asRepeatingParamGroup();
        return repeatingParamGroup.values();
    }

    @Override
    public boolean hasValue(final String name)
    {
        return findDslParam(name)
                .map(DslParam::hasValue)
                .orElse(false);
    }

    @Override
    public DslArg[] getParams()
    {
        return args;
    }

    private DslParam getDslParam(final String name)
    {
        return findDslParam(name).orElseThrow(() -> new IllegalArgumentException(name + " is not a parameter"));
    }

    private Optional<DslParam> findDslParam(final String name)
    {
        return Optional.ofNullable(name)
                .map(String::toLowerCase)
                .map(paramsByName::get);
    }
}
