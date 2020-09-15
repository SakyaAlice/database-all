package gaarason.database.contract.builder;

import gaarason.database.contract.function.GenerateSqlPart;
import gaarason.database.query.Builder;

/**
 * 结果集连接
 * @param <T>
 * @param <K>
 */
public interface Union<T, K>{

    /**
     * 结果集连接(去重)
     * @param closure 返回代码片段
     * @return 查询构造器
     */
    Builder<T, K> union(GenerateSqlPart closure);

    /**
     * 结果集连接(不去重)
     * @param closure 返回代码片段
     * @return 查询构造器
     */
    Builder<T, K> unionAll(GenerateSqlPart closure);
}