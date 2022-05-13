package gaarason.database.query;

import gaarason.database.config.ConversionConfig;
import gaarason.database.contract.connection.GaarasonDataSource;
import gaarason.database.contract.eloquent.Builder;
import gaarason.database.contract.eloquent.Model;
import gaarason.database.contract.function.GenerateSqlPartFunctionalInterface;
import gaarason.database.contract.query.Grammar;
import gaarason.database.lang.Nullable;
import gaarason.database.provider.ContainerProvider;
import gaarason.database.provider.ModelShadowProvider;
import gaarason.database.util.FormatUtils;
import gaarason.database.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Having查询构造器
 * @param <T>
 * @param <K>
 * @author xt
 */
public abstract class HavingBuilder<T extends Serializable, K extends Serializable> extends GroupBuilder<T, K> {

    protected HavingBuilder(GaarasonDataSource gaarasonDataSource, Model<T, K> model, Grammar grammar) {
        super(gaarasonDataSource, model, grammar);
    }

    protected Builder<T, K> havingGrammar(String sqlPart, @Nullable Collection<Object> parameters, String separator) {
        grammar.addSmartSeparator(Grammar.SQLPartType.HAVING, sqlPart, parameters, separator);
        return this;
    }

    @Override
    public Builder<T, K> havingRaw(@Nullable String sqlPart, @Nullable Collection<?> parameters) {
        if (!ObjectUtils.isEmpty(sqlPart)) {
            havingGrammar(sqlPart, ObjectUtils.isEmpty(parameters) ? null : ObjectUtils.typeCast(parameters), " and ");
        }
        return this;
    }

    @Override
    public Builder<T, K> havingRaw(@Nullable String sqlPart) {
        if (!ObjectUtils.isEmpty(sqlPart)) {
            havingGrammar(sqlPart, null, " and ");
        }
        return this;
    }

    @Override
    public Builder<T, K> havingRaw(@Nullable Collection<String> sqlParts) {
        if (!ObjectUtils.isEmpty(sqlParts)) {
            for (String sqlPart : sqlParts) {
                havingRaw(sqlPart);
            }
        }
        return this;
    }

    @Override
    public Builder<T, K> having(String column, String symbol, Object value) {
        ArrayList<Object> parameters = new ArrayList<>();
        String sqlPart = backQuote(column) + symbol + grammar.replaceValueAndFillParameters(value, parameters);
        havingGrammar(sqlPart, parameters, " and ");
        return this;
    }

    @Override
    public Builder<T, K> havingIgnoreNull(String column, String symbol, @Nullable Object value) {
        return ObjectUtils.isNull(value) ? this : having(column, symbol, value);
    }

    @Override
    public Builder<T, K> having(String column, @Nullable Object value) {
        return ObjectUtils.isNull(value) ? havingNull(column) : having(column, "=", value);
    }

    @Override
    public Builder<T, K> havingIgnoreNull(String column, @Nullable Object value) {
        return ObjectUtils.isNull(value) ? this : having(column, value);
    }

    @Override
    public Builder<T, K> having(T entity) {
        final Map<String, Object> columnValueMap = ModelShadowProvider.columnValueMap(entity);
        return having(columnValueMap);
    }

    @Override
    public Builder<T, K> having(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            having(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Builder<T, K> havingIgnoreNull(@Nullable Map<String, Object> map) {
        if (!ObjectUtils.isNull(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                havingIgnoreNull(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public Builder<T, K> havingKeywords(@Nullable Object value, Collection<String> columns) {
        andHaving(builder -> {
            for (String column : columns) {
                builder.orHaving(builderInner -> builderInner.havingMayLike(column, value));
            }
            return builder;
        });
        return this;
    }

    @Override
    public Builder<T, K> havingKeywords(@Nullable Object value, String... columns) {
        return havingKeywords(value, Arrays.asList(columns));
    }

    @Override
    public Builder<T, K> havingKeywordsIgnoreNull(@Nullable Object value, Collection<String> columns) {
        andHavingIgnoreEmpty(builder -> {
            for (String column : columns) {
                builder.orHavingIgnoreEmpty(builderInner -> builderInner.havingMayLikeIgnoreNull(column, value));
            }
            return builder;
        });
        return this;
    }

    @Override
    public Builder<T, K> havingKeywordsIgnoreNull(@Nullable Object value, String... columns) {
        return havingKeywordsIgnoreNull(value, Arrays.asList(columns));
    }

    @Override
    public Builder<T, K> havingLike(String column, @Nullable Object value) {
        return havingIgnoreNull(column, "like", value);
    }

    @Override
    public Builder<T, K> havingLike(@Nullable T entity) {
        final Map<String, Object> columnValueMap = ModelShadowProvider.columnValueMap(entity);
        return havingLike(columnValueMap);
    }

    @Override
    public Builder<T, K> havingLike(@Nullable Map<String, Object> map) {
        if (ObjectUtils.isEmpty(map)) {
            return this;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            havingLike(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Builder<T, K> havingMayLike(String column, @Nullable Object value) {
        String s = ContainerProvider.getBean(ConversionConfig.class).castNullable(value, String.class);
        if (!ObjectUtils.isNull(s) && (s.endsWith("%") || s.startsWith("%"))) {
            return havingLike(column, value);
        } else {
            return having(column, value);
        }
    }

    @Override
    public Builder<T, K> havingMayLikeIgnoreNull(String column, @Nullable Object value) {
        if (ObjectUtils.isNull(value)) {
            return this;
        }
        return havingMayLike(column, value);
    }

    @Override
    public Builder<T, K> havingMayLike(@Nullable T entity) {
        final Map<String, Object> columnValueMap = ModelShadowProvider.columnValueMap(entity);
        return havingMayLike(columnValueMap);
    }

    @Override
    public Builder<T, K> havingMayLike(@Nullable Map<String, Object> map) {
        if (ObjectUtils.isEmpty(map)) {
            return this;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            havingMayLike(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Builder<T, K> havingMayLikeIgnoreNull(@Nullable Map<String, Object> map) {
        if (ObjectUtils.isEmpty(map)) {
            return this;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            havingMayLikeIgnoreNull(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Builder<T, K> havingSubQuery(String column, String symbol, String completeSql) {
        String sqlPart = backQuote(column) + symbol + FormatUtils.bracket(completeSql);
        return havingRaw(sqlPart);
    }


    @Override
    public Builder<T, K> havingSubQuery(String column, String symbol,
                                        GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure);
        String completeSql = FormatUtils.bracket(sqlPartInfo.getSqlString());
        String sqlPart = backQuote(column) + symbol + completeSql;
        return havingGrammar(sqlPart, sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> havingIn(String column, Collection<?> valueList) {
        Collection<Object> parameters = new ArrayList<>();
        String valueStr = grammar.replaceValuesAndFillParameters(ObjectUtils.typeCast(valueList), parameters, ",");
        String sqlPart = backQuote(column) + "in" + FormatUtils.bracket(valueStr);
        return havingGrammar(sqlPart, parameters, " and ");
    }

    @Override
    public Builder<T, K> havingNotIn(String column, Collection<?> valueList) {
        Collection<Object> parameters = new ArrayList<>();
        String valueStr = grammar.replaceValuesAndFillParameters(ObjectUtils.typeCast(valueList), parameters, ",");
        String sqlPart = backQuote(column) + "not in" + FormatUtils.bracket(valueStr);
        return havingGrammar(sqlPart, parameters, " and ");
    }

    @Override
    public Builder<T, K> havingInRaw(String column, String sql) {
        String sqlPart = backQuote(column) + "in" + FormatUtils.bracket(sql);
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingNotInRaw(String column, String sql) {
        String sqlPart = backQuote(column) + "not in" + FormatUtils.bracket(sql);
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingInIgnoreEmpty(String column, @Nullable Collection<?> valueList) {
        return ObjectUtils.isEmpty(valueList) ? this : havingIn(column, valueList);
    }

    @Override
    public Builder<T, K> havingNotInIgnoreEmpty(String column, @Nullable Collection<?> valueList) {
        return ObjectUtils.isEmpty(valueList) ? this : havingNotIn(column, valueList);
    }

    @Override
    public Builder<T, K> havingIn(String column, Object... valueArray) {
        return havingIn(column, Arrays.asList(valueArray));
    }

    @Override
    public Builder<T, K> havingNotIn(String column, Object... valueArray) {
        return havingNotIn(column, Arrays.asList(valueArray));
    }

    @Override
    public Builder<T, K> havingInIgnoreEmpty(String column, @Nullable Object... valueArray) {
        return ObjectUtils.isEmpty(valueArray) ? this : havingIn(column, valueArray);
    }

    @Override
    public Builder<T, K> havingNotInIgnoreEmpty(String column, @Nullable Object... valueArray) {
        return ObjectUtils.isEmpty(valueArray) ? this : havingNotIn(column, valueArray);
    }

    @Override
    public Builder<T, K> havingIn(String column, GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure);
        String sqlPart = backQuote(column) + "in" + FormatUtils.bracket(sqlPartInfo.getSqlString());
        return havingGrammar(sqlPart, sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> havingNotIn(String column, GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure);
        String sqlPart = backQuote(column) + "not in" + FormatUtils.bracket(sqlPartInfo.getSqlString());
        return havingGrammar(sqlPart, sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> havingBetween(String column, Object min, Object max) {
        Collection<Object> parameters = new ArrayList<>();
        String sqlPart = backQuote(column) + "between" +
            grammar.replaceValueAndFillParameters(min, parameters) + "and" +
            grammar.replaceValueAndFillParameters(max, parameters);
        return havingGrammar(sqlPart, parameters, " and ");
    }

    @Override
    public Builder<T, K> havingNotBetween(String column, Object min, Object max) {
        Collection<Object> parameters = new ArrayList<>();
        String sqlPart = backQuote(column) + "not between" +
            grammar.replaceValueAndFillParameters(min, parameters) + "and" +
            grammar.replaceValueAndFillParameters(max, parameters);
        return havingGrammar(sqlPart, parameters, " and ");
    }

    @Override
    public Builder<T, K> havingNull(String column) {
        String sqlPart = backQuote(column) + "is null";
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingNotNull(String column) {
        String sqlPart = backQuote(column) + "is not null";
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingExistsRaw(String sql) {
        String sqlPart = "exists " + FormatUtils.bracket(sql);
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingNotExistsRaw(String sql) {
        String sqlPart = "not exists " + FormatUtils.bracket(sql);
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingExists(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure);
        String sql = "exists " + FormatUtils.bracket(sqlPartInfo.getSqlString());
        return havingGrammar(sql, sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> havingNotExists(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure);
        String sql = "not exists " + FormatUtils.bracket(sqlPartInfo.getSqlString());
        return havingGrammar(sql, sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> havingColumn(String column1, String symbol, String column2) {
        String sqlPart = backQuote(column1) + symbol + backQuote(column2);
        return havingRaw(sqlPart);
    }

    @Override
    public Builder<T, K> havingColumn(String column1, String column2) {
        return havingColumn(column1, "=", column2);
    }

    @Override
    public Builder<T, K> andHaving(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure, Grammar.SQLPartType.HAVING);
        return havingGrammar(FormatUtils.bracket(sqlPartInfo.getSqlString()), sqlPartInfo.getParameters(), " and ");
    }

    @Override
    public Builder<T, K> orHaving(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure, Grammar.SQLPartType.HAVING);
        return havingGrammar(FormatUtils.bracket(sqlPartInfo.getSqlString()), sqlPartInfo.getParameters(), " or ");
    }

    @Override
    public Builder<T, K> andHavingIgnoreEmpty(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure, Grammar.SQLPartType.HAVING);
        if (!ObjectUtils.isEmpty(sqlPartInfo.getSqlString())) {
            havingGrammar(FormatUtils.bracket(sqlPartInfo.getSqlString()), sqlPartInfo.getParameters(), " and ");
        }
        return this;
    }

    @Override
    public Builder<T, K> orHavingIgnoreEmpty(GenerateSqlPartFunctionalInterface<T, K> closure) {
        Grammar.SQLPartInfo sqlPartInfo = generateSql(closure, Grammar.SQLPartType.HAVING);
        if (!ObjectUtils.isEmpty(sqlPartInfo.getSqlString())) {
            havingGrammar(FormatUtils.bracket(sqlPartInfo.getSqlString()), sqlPartInfo.getParameters(), " or ");
        }
        return this;
    }
}