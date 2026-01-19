package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.RoleService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.model.Role;
import de.mueller_constantin.attoly.api.domain.model.RoleName;
import de.mueller_constantin.attoly.api.repository.RoleRepository;
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
    public Role findByName(RoleName name) throws EntityNotFoundException {
        return roleRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    @Override
    public boolean existsByName(RoleName name) {
        return roleRepository.existsByName(name);
    }
}
