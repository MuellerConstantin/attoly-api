package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.RoleService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.result.RoleResult;
import de.mueller_constantin.attoly.api.domain.result.mapper.RoleResultMapper;
import de.mueller_constantin.attoly.api.repository.model.Role;
import de.mueller_constantin.attoly.api.repository.model.RoleName;
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
    private final RoleResultMapper roleResultMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, RoleResultMapper roleResultMapper) {
        this.roleRepository = roleRepository;
        this.roleResultMapper = roleResultMapper;
    }

    @Override
    public List<RoleResult> findAll() {
        var roles = roleRepository.findAll();
        return roleResultMapper.mapToResult(roles);
    }

    @Override
    public Page<RoleResult> findAll(Pageable pageable) {
        var roles = roleRepository.findAll(pageable);
        return roleResultMapper.mapToResult(roles);
    }

    @Override
    public RoleResult findById(UUID id) throws EntityNotFoundException {
        var role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return roleResultMapper.mapToResult(role);
    }

    @Override
    public RoleResult findByName(RoleName name) throws EntityNotFoundException {
        var role = roleRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return roleResultMapper.mapToResult(role);
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
