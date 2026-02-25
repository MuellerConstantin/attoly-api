package de.mueller_constantin.attoly.api.web.v1.dto.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLFieldMapper;
import de.mueller_constantin.attoly.api.repository.rsql.JpaRSQLSpecification;
import de.mueller_constantin.attoly.api.repository.rsql.UnsupportedFilterQueryFieldException;
import de.mueller_constantin.attoly.api.repository.rsql.UnsupportedFilterQueryOperatorException;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserJpaRSQLFieldMapper implements JpaRSQLFieldMapper<User> {
    @Override
    public Specification<User> map(String fieldName, ComparisonOperator operator, List<String> args) {
        return switch (fieldName) {
            case "id" -> {
                if(!List.of("==", "!=").contains(operator.getSymbol())) {
                    throw new UnsupportedFilterQueryOperatorException(operator.getSymbol(), fieldName);
                }

                yield new JpaRSQLSpecification<>(fieldName, operator, args);
            }
            default ->
                    throw new UnsupportedFilterQueryFieldException(fieldName);
        };
    }
}
