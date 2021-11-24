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

package com.lmax.simpledsl.api;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Base class for value containers such as {@link DslParams} and {@link RepeatingGroup}.
 * <p>
 * Provides a range of methods to access the supplied values in a convenient form such as parsed into an int.
 */
public interface DslValues
{
    /**
     * Determine if a value was supplied for a parameter.
     * <p>
     * Returns true when the parameter is supplied with an empty value.
     *
     * @param name the name of the parameter.
     * @return true if the parameter was supplied, otherwise false.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    boolean hasValue(String name);

    /**
     * Retrieve the value supplied for a parameter.
     * <p>
     * May return {@code null} if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    String value(String name);

    /**
     * Retrieve the values supplied for a parameter as an array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    String[] values(String name);

    /**
     * Retrieve the value supplied for a parameter as an {@link Optional}.
     * <p>
     * The optional will be empty if the parameter was not supplied.
     *
     * @param name the name of the parameter
     * @return an Optional containing the value supplied for the parameter, if any.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default Optional<String> valueAsOptional(final String name)
    {
        return Optional.ofNullable(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter as an int.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as an {@code int} (including because it wasn't supplied).
     */
    default int valueAsInt(final String name)
    {
        return Integer.parseInt(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter as a long.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code long} (including because it wasn't supplied).
     */
    default long valueAsLong(final String name)
    {
        return Long.parseLong(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter as a {@literal boolean}.
     * <p>
     * The value is parsed using {@link Boolean#parseBoolean(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default boolean valueAsBoolean(final String name)
    {
        return Boolean.parseBoolean(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter as a {@link BigDecimal}.
     * <p>
     * The value is parsed using {@link BigDecimal#BigDecimal(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code BigDecimal}
     */
    default BigDecimal valueAsBigDecimal(final String name)
    {
        final String value = value(name);
        return value != null ? new BigDecimal(value) : null;
    }


    /**
     * Retrieve the value supplied for a parameter as a {@code double}.
     * <p>
     * The value is parsed using {@link Double#parseDouble(String)}.
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     * @throws NumberFormatException    if the supplied value can't be parsed as a {@code double}
     */
    default double valueAsDouble(final String name)
    {
        return Double.parseDouble(value(name));
    }

    /**
     * Retrieve the value supplied for a parameter formatted as a parameter. For example, if the parameter {@literal user} was given the value {@literal jenny},
     * then {@code valueAsParam("user")} would return {@code user: jenny}.
     * <p>
     * This is useful when reusing DSL methods to build higher level functions. e.g.
     *
     * <pre>{@code
     *   public void createUserAndLogin(String... args) {
     *     DslParams params = new DslParams(args,
     *                                      new RequiredParam("user"),
     *                                      new RequiredParam("accountType"));
     *     createUser(params.valueAsParam("user"), "password: password");
     *     login(params.valueAsParam("user"), "password: password");
     *   }
     * }</pre>
     *
     * @param name the name of the parameter.
     * @return the value supplied for that parameter, formatted as a parameter ready to pass on to another method that uses Simple-DSL.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter or if the parameter supports multiple values.
     */
    default String valueAsParam(final String name)
    {
        final String value = value(name);
        return value != null ? name + ": " + value : null;
    }

    /**
     * Retrieve the values supplied for a parameter as a {@link List}. Returns an empty list if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return a List of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default List<String> valuesAsList(final String name)
    {
        return Arrays.asList(values(name));
    }

    /**
     * Retrieve the values supplied for a parameter as an {@link Optional} {@link List}.
     * <p>
     * Returns an empty Optional if the parameter is optional and a value has not been supplied.
     * <p>
     * In most cases {@link #valuesAsList} is the more suitable method.
     * This variant is useful if there is an important difference between a parameter being set to an empty list vs not being supplied.
     * <p>
     * For example, a test that checks the properties of a user may have an optional check for the roles assigned to that user:
     * <pre>{@code
     *     DslParams params = new DslParams(args,
     *                                      new RequiredParam("user"),
     *                                      new OptionalParam("suspended"),
     *                                      new OptionalParam("roles").setAllowMultipleValues());
     *     params.valuesAsOptional("roles").ifPresent(roles -> getDriver().verifyRoles(params.value("user"), roles));
     * }</pre>
     * <p>
     * It's also possible to achieve this distinction by checking the parameter was supplied using {@link #hasValue}.
     *
     * @param name the name of the parameter.
     * @return a List of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     */
    default Optional<List<String>> valuesAsOptional(final String name)
    {
        final List<String> values = valuesAsList(name);
        return values.isEmpty() ? Optional.empty() : Optional.of(values);
    }

    /**
     * Retrieve the values supplied for a parameter as an {@code int} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code int}.
     */
    default int[] valuesAsInts(final String name)
    {
        final String[] values = values(name);
        final int[] parsedValues = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Integer.parseInt(values[i]);
        }
        return parsedValues;
    }

    /**
     * Retrieve the values supplied for a parameter as a {@code long} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code long}.
     */
    default long[] valuesAsLongs(final String name)
    {
        final String[] values = values(name);
        final long[] parsedValues = new long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Long.parseLong(values[i]);
        }
        return parsedValues;
    }

    /**
     * Retrieve the values supplied for a parameter as a {@link BigDecimal} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code BigDecimal}.
     */
    default BigDecimal[] valuesAsBigDecimals(final String name)
    {
        final String[] values = values(name);
        final BigDecimal[] parsedValues = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = new BigDecimal(values[i]);
        }
        return parsedValues;
    }

    /**
     * Retrieve the values supplied for a parameter as a {@code double} array.
     * <p>
     * Returns an empty array if the parameter is optional and a value has not been supplied.
     *
     * @param name the name of the parameter.
     * @return an array of values supplied for the parameter.
     * @throws IllegalArgumentException if {@code name} does not match the name of a supported parameter.
     * @throws NumberFormatException    if any of the supplied values can not be parsed as an {@code double}.
     */
    default double[] valuesAsDoubles(final String name)
    {
        final String[] values = values(name);
        final double[] parsedValues = new double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Double.parseDouble(values[i]);
        }
        return parsedValues;
    }

    /**
     * Get the supported parameters. Supplied values will have been parsed into the parameters.
     *
     * @return the array of {@link DslArg DslArgs} supplied to the constructor.
     */
    DslArg[] getParams();
}
