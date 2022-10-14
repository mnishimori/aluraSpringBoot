package br.com.alura.forum.config.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	@Value("${forum.jwt.expiration}")
	public String expiration;

	@Value("${forum.jwt.secret}")
	public String secretKey;
	
	public String gerarToken(Authentication authenticate) {
		
		Usuario usuario = (Usuario) authenticate.getPrincipal();
		
		Date dataToken = Date.from(
				LocalDateTime.now()
				.atZone(ZoneId.systemDefault()).toInstant());
		
		Date dataTokenExpiration = Date.from(
				LocalDateTime.now().plusMinutes(Long.parseLong(expiration))
				.atZone(ZoneId.systemDefault()).toInstant());
		
		return Jwts.builder()
				.setIssuer("API do fórum da Alura")
				.setSubject(usuario.getId().toString())
				.setIssuedAt(dataToken)
				.setExpiration(dataTokenExpiration)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public Boolean isTokenValido(String token) {
		try {
			Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token);
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}

	public Long getIdUsuario(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
			return Long.parseLong(claims.getSubject());
			
		} catch (Exception e) {
			return null;
		}
	}

}
