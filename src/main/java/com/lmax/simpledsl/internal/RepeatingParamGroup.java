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

import com.lmax.simpledsl.api.RepeatingGroup;

import java.util.List;

class RepeatingParamGroup extends DslParam
{
    private final String name;
    private final List<RepeatingParamValues> values;

    RepeatingParamGroup(final String name, final List<RepeatingParamValues> values)
    {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public SimpleDslParam getAsSimpleDslParam()
    {
        throw new IllegalArgumentException(name + " is a repeating group");
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

    @Override
    public boolean hasValue()
    {
        return !values.isEmpty();
    }
}
