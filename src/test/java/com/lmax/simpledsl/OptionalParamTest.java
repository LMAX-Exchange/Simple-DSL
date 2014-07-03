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

import org.junit.Assert;
import org.junit.Test;

public class OptionalParamTest
{
    @Test
    public void anOptionalParamShouldIdentifyItselfAsSuch()
    {
        DslParam param = new OptionalParam("foo");
        Assert.assertFalse(param.isRequired());
        Assert.assertNull(param.getAsRequiredParam());
    }

    @Test
    public void settingADefaultValueShouldNotAffectTheReturnedValueIfTheParameterIsSupplied()
    {
        SimpleDslParam param = new OptionalParam("foo").setDefault("def");
        param.addValue("1");
        Assert.assertEquals("1", param.getValue());
        Assert.assertArrayEquals(new String[]{"1"}, param.getValues());
    }

    @Test
    public void settingADefaultValueShouldMakeGetValueReturnItIfTheParameterIsNotSupplied()
    {
        SimpleDslParam param = new OptionalParam("foo").setDefault("def");
        Assert.assertEquals("def", param.getValue());
        Assert.assertArrayEquals(new String[]{"def"}, param.getValues());
    }

    @Test
    public void getValueShouldReturnNullIfThereIsNoParamAndNoDefault()
    {
        SimpleDslParam param = new OptionalParam("foo");
        Assert.assertEquals(null, param.getValue());
        Assert.assertArrayEquals(new String[0], param.getValues());
    }
}
