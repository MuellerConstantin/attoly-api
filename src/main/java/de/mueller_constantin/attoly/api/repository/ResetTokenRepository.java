package de.mueller_constantin.attoly.api.repository;

import de.mueller_constantin.attoly.api.repository.model.ResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {
}
