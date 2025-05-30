package gaarason.database.contract.builder;

import gaarason.database.contract.eloquent.Builder;

import java.util.Collection;

/**
 * 分组
 * @param <T>
 * @param <K>
 * @author xt
 */
public interface Group<B extends Builder<B, T, K>, T, K> {

    /**
     * 分组
     * @param sqlPart 原生语句
     * @return 查询构造器
     */
    B groupRaw(String sqlPart);

    /**
     * 分组
     * @param column 列名
     * @return 查询构造器
     */
    B group(String column);

    /**
     * 分组
     * @param column 列名数组
     * @return 查询构造器
     */
    B group(String... column);

    /**
     * 分组
     * @param columnList 列名列表
     * @return 查询构造器
     */
    B group(Collection<String> columnList);

}
