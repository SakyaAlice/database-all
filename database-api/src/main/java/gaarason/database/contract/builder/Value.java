package gaarason.database.contract.builder;

import gaarason.database.contract.eloquent.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * 值
 * @param <T>
 * @param <K>
 * @author xt
 */
public interface Value<T extends Serializable, K extends Serializable> {

    /**
     * 插入数据使用
     * @param valueList 值列表
     * @return 查询构造器
     */
    Builder<T, K> value(List<Object> valueList);

    /**
     * 批量插入数据使用
     * @param valueList 值列表的列表
     * @return 查询构造器
     */
    Builder<T, K> valueList(List<List<Object>> valueList);

}