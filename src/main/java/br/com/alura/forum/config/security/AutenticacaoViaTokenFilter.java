package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.model.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

/**
 * Filtro que intercepta a requisição. Recuper o token do cabeçalho, valida e
 * autentica o usuário. A autenticação é feita para cada requisição. Toda
 * requisição que chegar para a API, tokem é obtido e autenticamos o usuário. Se
 * estiver ok, executamos a requisição, devolvemos a resposta.
 * 
 * @author maria luiza
 *
 */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	private TokenService tokenService;
	private UsuarioRepository usuarioRepository;

	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = recuperarToken(request);
		boolean valido = tokenService.isTokenValido(token);
		if (valido)
			autenticarCliente(token);

		// Se o token estive ok, então autentica o usuário.
		filterChain.doFilter(request, response);
	}

	private void autenticarCliente(String token) {
		// Recupera o is do usuário pelo token
		Long idUsuario = tokenService.getIdUsuario(token);
		Usuario usuario = usuarioRepository.findById(idUsuario).get();
		
		// Não precisa passar a senha, porque já validamos no controller
		Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		
		// Avisa para o Spring que o usuário está autenticado
		SecurityContextHolder.getContext().setAuthentication(authentication);		
	}

	/**
	 * Recupera o token do cabeçalho Authotization.
	 * 
	 * @param request
	 * @return
	 */
	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token == null || token.isEmpty() || !token.startsWith("Bearer "))
			return null;

		return token.substring(7, token.length());
	}

}
