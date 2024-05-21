package com.backend.dorandoran.security.jwt.service;

import com.backend.dorandoran.common.domain.ErrorCode;
import com.backend.dorandoran.common.exception.CommonException;
import com.backend.dorandoran.security.service.CustomUserDetailService;
import com.backend.dorandoran.user.domain.entity.UserToken;
import com.backend.dorandoran.user.service.UserTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.backend.dorandoran.common.domain.ErrorCode.EXPIRED_TOKEN;

@Slf4j
@Component
public class JwtUtil {

    private static final Long ACCESS_TOKEN_EXPIRED_TIME = Duration.ofDays(14).toMillis();
    private static final Long REFRESH_TOKEN_EXPIRED_TIME = Duration.ofDays(30).toMillis();

    private static final String KEY_ROLE = "role";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final SecretKey secretKey;
    private final UserTokenService userTokenService;
    private final CustomUserDetailService customUserDetailService;

    public JwtUtil(@Value("${jwt.secret-key}") String key, UserTokenService userTokenService, CustomUserDetailService customUserDetailService) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.userTokenService = userTokenService;
        this.customUserDetailService = customUserDetailService;
    }

    public String createAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRED_TIME);
    }

    public String generateToken(Authentication authentication, Long expiredTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiredTime);

        Claims claims = Jwts.claims().setSubject(authentication.getName());// name == userId
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(KEY_ROLE, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new CommonException(EXPIRED_TOKEN);
        }
        return parseClaims(token)
                .getExpiration()
                .after(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
        } catch (ExpiredJwtException eje) {
            return eje.getClaims();
        } catch (JwtException je) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException iae) {
            throw new CommonException(ErrorCode.INTERNAL_ERROR);
        }
    }

    public String reissuedAccessToken(Long userId) {
        UserToken userToken = userTokenService.findByUserId(userId);
        String refreshToken = userToken.getRefreshToken();

        if (validateToken(refreshToken)) {
            return TOKEN_PREFIX + createAccessToken(getAuthenticationByAccessToken(refreshToken));
        } else {
            Authentication authentication = getAuthenticationByUserId(userToken.getUserId());

            String accessToken = createAccessToken(authentication); // Front
            refreshToken = createRefreshToken(authentication); // DB

            userToken.updateRefreshToken(refreshToken);
            userTokenService.save(userToken);
            return accessToken;
        }
    }

    public String saveToken(Long userId) {
        Authentication authentication = getAuthenticationByUserId(userId);

        String accessToken = createAccessToken(authentication); // Front
        String refreshToken = createRefreshToken(authentication); // DB

        UserToken userToken = UserToken.toUserTokenEntity(userId, refreshToken);
        userTokenService.save(userToken);
        return accessToken;
    }

    public Authentication getAuthenticationByAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        User userDetails = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(
                new SimpleGrantedAuthority(claims.get(KEY_ROLE).toString()));
    }

    private Authentication getAuthenticationByUserId(Long userId) {
        UserDetails userDetails = getUserDetails(userId);
        return createAuthentication(userDetails);
    }

    private UserDetails getUserDetails(Long userId) {
        return customUserDetailService.loadUserByUsername(String.valueOf(userId));
    }

    private Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }
}