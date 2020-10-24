package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

/**
 * Classe que possui todas as configurações de segurança.
 * 
 * @author maria luiza
 *
 */
@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

	@Autowired
	private AutenticacaoService autenticacaoService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	/**
	 * Necessário sobreescrever esse método para conseguirmos injetar o
	 * authenticationManager no controller.
	 */
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/**
	 * Configurações de autenticação.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// BCrypt é o algoritmo de hashing de senha.
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}

	/**
	 * Configurações de autorização.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests() //
				// Liberando acesso aos endpoints públicos
				.antMatchers(HttpMethod.GET, "/topicos").permitAll() //
				.antMatchers(HttpMethod.GET, "/topicos/*").permitAll() //
				.antMatchers(HttpMethod.POST, "/auth").permitAll() //
				.antMatchers(HttpMethod.GET, "/actuator/**").permitAll() //

				// Restringindo o acesso aos endpoints privados
				.anyRequest().authenticated() // Qualquer outra requisição tem que estar autenticada.

				// Configuração de autenticação stateless
				.and().csrf().disable() //
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				// Registra filtro de validação de token
				.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);
	}

	/**
	 * Configurações de recursos estáticos(js, css, imagens, etc).
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}

//	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder().encode("123456"));
//	}

}
