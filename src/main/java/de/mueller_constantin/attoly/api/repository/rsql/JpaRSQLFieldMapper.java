package de.mueller_constantin.attoly.api.repository.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface JpaRSQLFieldMapper<T> {
    Specification<T> map(String fieldName, ComparisonOperator operator, List<String> arguments);
}
