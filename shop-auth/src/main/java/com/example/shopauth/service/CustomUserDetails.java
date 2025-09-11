package com.example.shopauth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails{

    private Long id;
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;


//    Trả danh sách quyền (role) ở dạng SimpleGrantedAuthority, ví dụ: ROLE_USER, ROLE_ADMIN.
    //Tác dụng: Dùng ở bước uỷ quyền (authorize).
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
//=============================================================================================
//    4 cờ trạng thái tài khoản.

//    Tác dụng: Nếu bất kỳ cờ nào false, quá trình authenticate/authorize có thể bị từ chối.

//    Trong code của bạn, các cờ trả true (tức user hoạt động bình thường).
//    Nếu sau này cần khoá tài khoản thì set false.
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}