package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	@Value("${forum.jwt.expiration}")
	private String expiration;
	
	@Value("${forum.jwt.secret}")
	private String secret;


	public String gerarToken(Authentication authentication) {
		Usuario logado = (Usuario)authentication.getPrincipal();
		
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));
		
		return Jwts.builder()
				.setIssuer("API do Fórum da Alura") // Quem está gerando o token
				.setSubject(logado.getId().toString()) // Usuário dono desse token
				.setIssuedAt(hoje) // Data de geração do token
				.setExpiration(dataExpiracao)
				.signWith(SignatureAlgorithm.HS256, secret)  // Algoritmo de criptografia
				.compact();			
	}


	/**
	 * Descriptografa o token recebido e válida se está ok.
	 * @param token
	 * @return
	 */
	public boolean isTokenValido(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * Recupera o id do usuário que foi setado no setSubject quando foi criado o token.
	 * 
	 * @param token
	 * @return
	 */
	public Long getIdUsuario(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

}
