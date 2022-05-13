package gaarason.database.contract.builder;

import gaarason.database.contract.eloquent.Builder;
import gaarason.database.lang.Nullable;

import java.io.Serializable;

/**
 * 排序
 * @param <T>
 * @param <K>
 * @author xt
 */
public interface Order<T extends Serializable, K extends Serializable> {

    /**
     * 排序
     * @param column      列名, 为null则忽略
     * @param orderByType 正序|倒序
     * @return 查询构造器
     */
    Builder<T, K> orderBy(@Nullable String column, gaarason.database.appointment.OrderBy orderByType);

    /**
     * 排序
     * @param column 列名, 为null则忽略
     * @return 查询构造器
     */
    Builder<T, K> orderBy(@Nullable String column);

    /**
     * 排序
     * @param sqlPart sql片段, 为null则忽略
     * @return 查询构造器
     */
    Builder<T, K> orderByRaw(@Nullable String sqlPart);

}