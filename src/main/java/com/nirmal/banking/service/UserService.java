package com.nirmal.banking.service;

import com.nirmal.banking.common.SuccessMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.ImageStatus;
import com.nirmal.banking.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;

    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails details = userDetailsRepo.findByuserName(username);
        List<GrantedAuthority> authorities = List.of((GrantedAuthority) () -> details.getUserRole().getRole().name());
        return new User(details.getUserName(), details.getPassword(), authorities);
    }

    public UserDetailsDto addUser(UserDetailsDto userDetailsDto) {

        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userDetailsDto, customUserDetails);
        customUserDetails.setUserRole(roleRepo.findById(3).get());
        customUserDetails.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        userDetailsRepo.save(customUserDetails);
        BeanUtils.copyProperties(customUserDetails, userDetailsDto);
        return userDetailsDto;
    }

    public String uploadImage(MultipartFile file, HttpServletRequest request) throws IOException {

        String authHeader = request.getHeader("Authorization");
        String base = authHeader.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        final String username = values[0];
        CustomUserDetails customUserDetails = userDetailsRepo.findByuserName(username);
        customUserDetails.setImageData(ImageUtil.compressImage(file.getBytes()));
        customUserDetails.setImageStatus(ImageStatus.UPLOADED);
        userDetailsRepo.save(customUserDetails);
        return SuccessMessages.IMAGE_UPLOADED;
    }
}