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

public class OptionalParam extends DslParam
{
    private String defaultValue;

    public OptionalParam(final String name)
    {
        super(name);
    }

    @Override
    public RequiredParam getAsRequiredParam()
    {
        return null;
    }

    @Override
    public OptionalParam getAsOptionalParam()
    {
        return this;
    }

    @Override
    public DslParam setDefault(final String defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean matches(String argName)
    {
        return name.equalsIgnoreCase(argName);
    }

    @Override
    public String[] getValues()
    {
        String[] values = super.getValues();
        return values.length > 0 ? values : defaultValue != null ? new String[]{defaultValue} : new String[0];
    }
}
