package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.ResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {
}
