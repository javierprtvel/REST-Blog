package me.lnsprt.projects.restblog.security.filter;

import static me.lnsprt.projects.restblog.security.config.AuthConstants.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    public AuthorizationFilter(AuthenticationManager manager){
        super(manager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            String header = request.getHeader(AUTH_HEADER);

            if(header == null || !header.startsWith(TOKEN_PREFIX)){
                chain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch(ExpiredJwtException e){
            response.setStatus(419);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) throws ExpiredJwtException {
        Claims claims = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.decode(TOKEN_SECRET))
                .parseClaimsJws(token.replace(TOKEN_PREFIX, "").trim())
                .getBody();
        String user = claims.getSubject();

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get(ROLES_CLAIM).toString());

        return user == null ? null : new UsernamePasswordAuthenticationToken(user, token, authorities);
    }
}
