package de.mueller_constantin.attoly.api.repository.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

public class JpaRSQLSpecificationBuilder<T> {
    private final JpaRSQLFieldMapper<T> fieldMapper;

    public JpaRSQLSpecificationBuilder(JpaRSQLFieldMapper<T> fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    public Specification<T> createSpecification(Node node) {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node);
        }

        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node);
        }

        return null;
    }

    public Specification<T> createSpecification(final LogicalNode logicalNode) {

        List<Specification<T>> specifications = logicalNode.getChildren()
                .stream()
                .map(this::createSpecification)
                .filter(Objects::nonNull).toList();

        Specification<T> result = specifications.get(0);

        if (logicalNode.getOperator() == LogicalOperator.OR) {
            for (Specification<T> specification : specifications) {
                result = result.or(specification);
            }
        }

        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (Specification<T> specification : specifications) {
                result = result.and(specification);
            }
        }

        return result;
    }

    public Specification<T> createSpecification(ComparisonNode node) {
        return this.fieldMapper.map(node.getSelector(), node.getOperator(), node.getArguments());
    }
}
