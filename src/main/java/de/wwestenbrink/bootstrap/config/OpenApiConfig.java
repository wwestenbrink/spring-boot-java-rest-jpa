package de.wwestenbrink.bootstrap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents the open api config.
 */
@RequiredArgsConstructor
@Configuration
public class OpenApiConfig {

  private final BuildProperties buildProperties;

  /**
   * Configure the open api bean with build property values.
   *
   * @return the configured open api config
   */
  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info()
            .title(buildProperties.getArtifact())
            .version(buildProperties.getVersion())
            .license(new License()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT")));
  }
}
