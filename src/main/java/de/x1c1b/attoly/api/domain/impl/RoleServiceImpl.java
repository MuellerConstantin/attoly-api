package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.RoleService;
import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.exception.RoleNameAlreadyInUseException;
import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.payload.RoleCreationPayload;
import de.x1c1b.attoly.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    public Role findById(UUID id) throws EntityNotFoundException {
        return roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Role findByName(String name) throws EntityNotFoundException {
        return roleRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    public Role create(RoleCreationPayload payload) throws RoleNameAlreadyInUseException {
        if (roleRepository.existsAnyByName(payload.getName())) {
            throw new RoleNameAlreadyInUseException(payload.getName());
        }

        Role role = Role.builder()
                .name(payload.getName())
                .build();

        return roleRepository.save(role);
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    @Override
    public void deleteByName(String name) throws EntityNotFoundException {
        delete(findByName(name));
    }

    protected void delete(Role role) {
        roleRepository.deleteSoft(role);
    }
}
