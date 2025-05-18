package com.eazybytes.springsection1.config;





import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//burada converteri yazmamızın sebebi keycloack tarafından gönderilen jwt tokenindeki bilgilerin java spring securitynin anlayacağı
// grantedauthority nesnelerine dönüştürmektir
public class KeyCloackRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        ArrayList<String> roles = (ArrayList<String>) source.getClaims().get("scope");
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        Collection<GrantedAuthority> returnValue = roles
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return returnValue;
    }
}
