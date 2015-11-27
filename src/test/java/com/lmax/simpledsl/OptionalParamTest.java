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

import java.util.LinkedList;
import java.util.function.Consumer;

public class OptionalParamTest
{
    @Test
    public void anOptionalParamShouldIdentifyItselfAsSuch()
    {
        final DslParam param = new OptionalParam("foo");
        Assert.assertFalse(param.isRequired());
    }

    @Test
    public void settingADefaultValueShouldNotAffectTheReturnedValueIfTheParameterIsSupplied()
    {
        final SimpleDslParam param = new OptionalParam("foo").setDefault("def");
        param.addValue("1");
        Assert.assertEquals("1", param.getValue());
        Assert.assertArrayEquals(new String[]{"1"}, param.getValues());
    }

    @Test
    public void settingADefaultValueShouldMakeGetValueReturnItIfTheParameterIsNotSupplied()
    {
        final SimpleDslParam param = new OptionalParam("foo").setDefault("def");
        Assert.assertEquals("def", param.getValue());
        Assert.assertArrayEquals(new String[]{"def"}, param.getValues());
    }

    @Test
    public void getValueShouldReturnNullIfThereIsNoParamAndNoDefault()
    {
        final SimpleDslParam param = new OptionalParam("foo");
        Assert.assertEquals(null, param.getValue());
        Assert.assertArrayEquals(new String[0], param.getValues());
    }

    @Test
    public void consumerIsCalledForSingleValue()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new OptionalParam("foo").setConsumer(consumer);
        param.addValue("abc");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("abc", list.get(0));
    }

    @Test
    public void consumerIsCalledForDefault()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new OptionalParam("foo").setDefault("abc").setConsumer(consumer);
        param.completedParsing();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("abc", list.get(0));
    }

    @Test
    public void consumerIsNotCalledForOptionalWithNoDefault()
    {
        final LinkedList<String> list = new LinkedList<>();
        final Consumer<String> consumer = list::add;
        final SimpleDslParam param = new OptionalParam("foo").setConsumer(consumer);
        param.completedParsing();

        Assert.assertEquals(0, list.size());
    }

}
