package gaarason.database.contract.eloquent;

import gaarason.database.contract.record.OperationTrait;

/**
 * 快捷方式
 * @param <T> 实体类
 * @param <K> 主键类型
 */
public interface Shortcut<T, K> extends OperationTrait<T, K> {

    /**
     * 实体对象
     * @return 可用于新增/更新数据的实体对象
     */
    T getEntity();
}