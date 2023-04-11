package com.takeshi.function;

/**
 * 异常处理函数式接口
 */
@FunctionalInterface
public interface CheckedFunction<T,R> {
 
    R apply(T t) throws Exception;
 
}
