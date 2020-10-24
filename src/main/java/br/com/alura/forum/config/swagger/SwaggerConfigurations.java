package br.com.alura.forum.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.model.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2) //
				.select() //
				.apis(RequestHandlerSelectors.basePackage("br.com.alura.forum")) // A partir qual pacote vai começar a ler as classes, nesse caso sem restrições
				.paths(PathSelectors.ant("/**")).build() // Endpoints que o SpringFox Swagger precisa analisar, nesse caso, nenhuma url
															// restrita
				.ignoredParameterTypes(Usuario.class) // Ignorar todas as URLs que trabalham com a classe Usuario, porque senão, na
														// tela do Swagger vai começar a aparecer os dados da senha do usuário
				.globalOperationParameters( // Adiciona parâmetros globais, ou seja, é um parâmetro que quero que o Swagger
											// apresente em todos os endpoints
						Arrays.asList( //
								new ParameterBuilder() // Cria campo para informar o token
										.name("Authorization") //
										.description("Header para Token JWT") //
										.modelRef(new ModelRef("string")) //
										.parameterType("header") //
										.required(false) //
										.build()));
	}

}
