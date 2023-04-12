package com.takeshi.exception;

import com.takeshi.function.CheckedFunction;
import lombok.ToString;
import org.springframework.data.util.Pair;

import java.util.Optional;
import java.util.function.Function;

/**
 * Either
 *
 * @author 七濑武【Nanase Takeshi】
 */
@ToString
public class Either<L, R> {

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * left
     *
     * @param value value
     * @param <L>   L
     * @param <R>   R
     * @return Either
     */
    public static <L, R> Either<L, R> Left(L value) {
        return new Either<>(value, null);
    }

    /**
     * right
     *
     * @param value value
     * @param <L>   L
     * @param <R>   R
     * @return Either
     */
    public static <L, R> Either<L, R> Right(R value) {
        return new Either<>(null, value);
    }

    /**
     * 获取左侧
     *
     * @return Optional
     */
    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    /**
     * 获取右侧
     *
     * @return Optional
     */
    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    /**
     * 左侧是否存在
     *
     * @return boolean
     */
    public boolean isLeft() {
        return left != null;
    }

    /**
     * 右侧是否存在
     *
     * @return boolean
     */
    public boolean isRight() {
        return right != null;
    }

    /**
     * 获取左侧
     *
     * @param mapper 函数
     * @param <T>    T
     * @return Optional
     */
    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    /**
     * 获取右侧
     *
     * @param mapper 函数
     * @param <T>    T
     * @return Optional
     */
    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }


    /**
     * lambda 抛出异常<br/>
     * 发生异常时,流的处理会立即停止
     *
     * @param function 函数
     * @param <T>      T
     * @param <R>      R
     * @return 函数
     */
    public static <T, R> Function<T, R> warp(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * lambda 抛出异常<br/>
     * 发生异常时,流的处理会继续<br/>
     * 不保存原始值
     *
     * @param function 函数
     * @param <T>      T
     * @param <R>      R
     * @return 函数
     */
    public static <T, R> Function<T, Either<?, R>> lift(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.Right(function.apply(t));
            } catch (Exception e) {
                return Either.Left(e);
            }
        };
    }

    /**
     * lambda 抛出异常<br/>
     * 发生异常时,流的处理会继续<br/>
     * 异常和原始值都保存在左侧
     *
     * @param function 函数
     * @param <T>      T
     * @param <R>      R
     * @return 函数
     */
    public static <T, R> Function<T, Either<?, R>> liftWithValue(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.Right(function.apply(t));
            } catch (Exception ex) {
                return Either.Left(Pair.of(ex, t));
            }
        };
    }

}
