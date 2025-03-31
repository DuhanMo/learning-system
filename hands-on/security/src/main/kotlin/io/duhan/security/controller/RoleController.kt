package io.duhan.security.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RoleController {
    @GetMapping("/api/v1/role-admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun some(): String {
        return "just need role admin"
    }

    @GetMapping("/api/v1/role-admin/create-need")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_MEMBER')")
    fun some2(): String {
        return "need role admin and CREATE_MEMBER permission need"
    }

    @GetMapping("/api/v1/role-admin/delete-need")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_MEMBER')")
    fun some3(): String {
        return "need role admin and DELETE_MEMBER permission need"
    }

    @GetMapping("/api/v1/vip-member")
    @PreAuthorize("hasAuthority('VIP')")
    fun some4(): String {
        return "need vip permission"
    }

    @GetMapping
    fun permitAll(): String {
        return "permit all"
    }
}
