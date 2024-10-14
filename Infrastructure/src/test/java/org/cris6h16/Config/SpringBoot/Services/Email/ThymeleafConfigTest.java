package org.cris6h16.Config.SpringBoot.Services.Email;

import org.junit.jupiter.api.Test;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThymeleafConfigTest {

    ThymeleafConfig config;

    public ThymeleafConfigTest() {
        this.config = new ThymeleafConfig();
    }

    @Test
    void templateResolver_containsCorrectConfiguration(){
        ClassLoaderTemplateResolver resolver = (ClassLoaderTemplateResolver) config.templateResolver();
        assertEquals(resolver.getPrefix(), "/templates/");
        assertEquals(resolver.getSuffix(), ".html");
        assertEquals(resolver.getTemplateMode().name(), "HTML");
        assertEquals(resolver.getCharacterEncoding(), "UTF-8");
    }
}