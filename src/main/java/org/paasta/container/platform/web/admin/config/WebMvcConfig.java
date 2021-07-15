package org.paasta.container.platform.web.admin.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.paasta.container.platform.web.admin.common.ConstantsUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Web Mvc Config 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.08.24
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/", "classpath:/static/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/font/**").addResourceLocations("classpath:/static/font/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }


    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver  templateResolver = new SpringResourceTemplateResolver ();
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(MessageSource messageSource) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);
        templateEngine.addDialect(layoutDialect());

        return templateEngine;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Bean
    @Autowired
    public ViewResolver viewResolver(MessageSource messageSource) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine(messageSource));
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(0);
        return viewResolver;
    }

    @Bean
    CustomIntercepter customIntercepter() {
        return new CustomIntercepter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customIntercepter())
                .excludePathPatterns("/common/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/font/**")
                .excludePathPatterns("/img/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/webjars")
                .excludePathPatterns(ConstantsUrl.URI_CP_SESSION_OUT)
                .excludePathPatterns(ConstantsUrl.URI_CP_SESSION_OUT+ "/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/error/**");

    }
}