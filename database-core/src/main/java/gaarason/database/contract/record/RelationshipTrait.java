package gaarason.database.contract.record;

import gaarason.database.contract.function.GenerateSqlPart;
import gaarason.database.contract.function.RelationshipRecordWith;
import gaarason.database.contract.record.bind.Relation;
import gaarason.database.eloquent.Record;

/**
 * 关联关系
 * @param <T> 实体类
 * @param <K> 主键类型
 */
public interface RelationshipTrait<T, K> {


    /**
     * 清空渴求式关联
     * @return 关联的Model的查询构造器
     */
    Record<T, K> withClear();

    /**
     * 渴求式关联
     * @param column 所关联的Model(当前模块的属性名)
     * @return 关联的Model的查询构造器
     */
    Record<T, K> with(String column);

    /**
     * 渴求式关联
     * @param column         所关联的Model(当前模块的属性名)
     * @param builderClosure 所关联的Model的查询构造器约束
     * @return 关联的Model的查询构造器
     */
    Record<T, K> with(String column, GenerateSqlPart builderClosure);

    /**
     * 渴求式关联
     * @param column         所关联的Model(当前模块的属性名)
     * @param builderClosure 所关联的Model的查询构造器约束
     * @param recordClosure  所关联的Model的再一级关联
     * @return 关联的Model的查询构造器
     */
    Record<T, K> with(String column, GenerateSqlPart builderClosure, RelationshipRecordWith recordClosure);

    /**
     *
     * @return
     */
    Relation bind(String column);
}