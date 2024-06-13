package com.ead.authuser.domain.services;

import com.ead.authuser.domain.enums.RoleType;
import com.ead.authuser.domain.models.RoleModel;

public interface RoleService {

    RoleModel findByRoleName(RoleType roleType);
}
