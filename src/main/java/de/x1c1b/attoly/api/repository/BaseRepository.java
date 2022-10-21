package de.x1c1b.attoly.api.repository;

import de.x1c1b.attoly.api.domain.model.BaseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends UUID> extends PagingAndSortingRepository<T, ID> {

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAll();

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false AND e.id = ?1")
    Optional<T> findById(ID id);

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false AND e.id IN ?1")
    List<T> findAllById(Iterable<ID> ids);

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT count(e) FROM #{#entityName} e WHERE e.deleted = false")
    long count();

    @Override
    @Transactional(readOnly = true)
    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAll(Sort sort);

    @Override
    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    Page<T> findAll(Pageable pageable);

    @Transactional
    @Modifying
    default void deleteSoftById(ID id) {
        T entity = findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("Entity with ID [%s] wasn't found in the database. Nothing to soft-delete.", id), 1));

        if (entity.isDeleted()) {
            throw new DataIntegrityViolationException(String.format("Entity with ID [%s] is already soft-deleted.", id));
        }

        entity.setDeleted(true);
        save(entity);
    }

    @Transactional
    @Modifying
    default void deleteSoft(T entity) {
        if (entity.isDeleted()) {
            throw new DataIntegrityViolationException(String.format("Entity with ID [%s] is already soft-deleted.", entity.getId()));
        }

        entity.setDeleted(true);
        save(entity);
    }

    @Transactional
    @Modifying
    default void deleteAllSoftById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteSoftById);
    }

    @Transactional
    @Modifying
    default void deleteAllSoft(Iterable<? extends T> entities) {
        entities.forEach(this::deleteSoft);
    }

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true")
    void deleteAllSoft();

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    List<T> findAllDeleted();

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true AND e.id = ?1")
    Optional<T> findDeletedById(ID id);

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true AND e.id IN ?1")
    List<T> findAllDeletedById(Iterable<ID> ids);

    @Transactional(readOnly = true)
    @Query("SELECT count(e) FROM #{#entityName} e WHERE e.deleted = true")
    long countDeleted();

    @Transactional(readOnly = true)
    default boolean existsDeletedById(ID id) {
        return findDeletedById(id).isPresent();
    }

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    List<T> findAllDeleted(Sort sort);

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    Page<T> findAllDeleted(Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e")
    List<T> findAllAny();

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1")
    Optional<T> findAnyById(ID id);

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e WHERE e.id IN ?1")
    List<T> findAllAnyById(Iterable<ID> ids);

    @Transactional(readOnly = true)
    @Query("SELECT count(e) FROM #{#entityName} e")
    long countAny();

    @Transactional(readOnly = true)
    default boolean existsAnyById(ID id) {
        return findDeletedById(id).isPresent();
    }

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e")
    List<T> findAllAny(Sort sort);

    @Transactional(readOnly = true)
    @Query("SELECT e FROM #{#entityName} e")
    Page<T> findAllAny(Pageable pageable);
}
