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

import com.lmax.simpledsl.api.OptionalArg;

class OptionalParam extends SimpleDslParam<OptionalArg, OptionalParam>
{
    private String defaultValue;

    OptionalParam(final String name)
    {
        super(new OptionalArg(name));
    }

    OptionalParam(final OptionalArg arg)
    {
        super(arg);
    }

    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }

    OptionalParam setDefault(final String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String[] getValues()
    {
        final String[] values = super.getValues();
        return values.length > 0 ? values : defaultValue != null ? new String[]{defaultValue} : new String[0];
    }
}
