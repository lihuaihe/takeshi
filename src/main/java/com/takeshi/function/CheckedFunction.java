package com.takeshi.function;

/**
 * 异常处理函数式接口
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Exception Exception
     */
    R apply(T t) throws Exception;

}
