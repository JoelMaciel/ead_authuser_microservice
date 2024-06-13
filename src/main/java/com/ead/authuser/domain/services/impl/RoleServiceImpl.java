package com.ead.authuser.domain.services.impl;

import com.ead.authuser.domain.enums.RoleType;
import com.ead.authuser.domain.exceptions.RoleNotFoundException;
import com.ead.authuser.domain.models.RoleModel;
import com.ead.authuser.domain.repositories.RoleRepository;
import com.ead.authuser.domain.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    public static final String NO_ROLE_REGISTERED = "There is no role registered in the database";
    private final RoleRepository roleRepository;

    @Override
    public RoleModel findByRoleName(RoleType roleType) {
        return roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new RoleNotFoundException(NO_ROLE_REGISTERED));
    }
}
