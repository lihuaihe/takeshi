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
 * @date 2023/4/11 17:36
 */
@ToString
public class Either<L, R> {

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> Left(L value) {
        return new Either<>(value, null);
    }

    public static <L, R> Either<L, R> Right(R value) {
        return new Either<>(null, value);
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

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
     * @param function
     * @param <T>
     * @param <R>
     * @return
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
     * @param function
     * @param <T>
     * @param <R>
     * @return
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
     * @param function
     * @param <T>
     * @param <R>
     * @return
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
