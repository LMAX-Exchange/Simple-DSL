package com.lmax.simpledsl;

/**
 * The base class for all param types.
 */
public abstract class DslParam
{
    /** @deprecated With the addition of covariant return types, this should no longer be necessary. */
    @Deprecated
    public RequiredParam getAsRequiredParam() {
        return null;
    }

    /**
     * Return this param as a {@link SimpleDslParam} or {@code null} if the param cannot be viewed as a {@code SimpleDslParam} (e.g. because it's a {@link RepeatingParamGroup}).
     *
     * @return this param as a {@code SimpleDslParam}.
     */
    public abstract SimpleDslParam getAsSimpleDslParam();

    /**
     * Return this param as a {@link RepeatingParamGroup} or {@code null} if the param cannot be viewed as a {@code SimpleDslParam} (e.g. because it's a {@code SimpleDslParam}).
     *
     * @return this param as a {@code RepeatingParamGroup}.
     */
    public abstract RepeatingParamGroup asRepeatingParamGroup();

    /**
     * Used as part of parsing the arguments.
     *
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     * @param currentPosition the current argument position in the parse.
     * @param args the arguments to be consumed.
     * @return the new argument position.
     */
    public abstract int consume(int currentPosition, NameValuePair... args);

    /**
     * Determine if a value is required for this parameter.
     *
     * @return {@code true} if and only if this param is required.
     */
    public boolean isRequired()
    {
        return false;
    }

    /**
     * Determine a value was supplied for this parameter.
     *
     * @return {@code true} if and only if a value (including a blank value) was supplied for this parameter.
     */
    public abstract boolean hasValue();

    /**
     * Get the name of this parameter.
     *
     * @return the parameter name.
     */
    public abstract String getName();

    /**
     * Used as part of parsing the arguments.
     *
     * @deprecated This is not intended to be part of the public API and will be removed in a future release.
     * @return {@code true} if and only if the value is valid.
     */
    public boolean isValid()
    {
        return true;
    }
}
