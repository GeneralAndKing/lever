package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.web.mappings.MappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletsMappingDescriptionProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import wiki.lever.modal.exception.SystemException;
import wiki.lever.service.InformationService;

import java.util.Collection;
import java.util.List;

/**
 * 2022/10/7 16:19:37
 *
 * @author yue
 */
@Slf4j
@RestController
@RequestMapping("/information")
@RequiredArgsConstructor
public class InformationController {

    private final WebApplicationContext webApplicationContext;
    private final InformationService informationService;
    private final Collection<MappingDescriptionProvider> descriptionProviders;

    @GetMapping("/mapping")
    public HttpEntity<?> mapping() {
        List<DispatcherServletMappingDescription> descriptionList = descriptionProviders.stream()
                .filter(DispatcherServletsMappingDescriptionProvider.class::isInstance)
                .map(DispatcherServletsMappingDescriptionProvider.class::cast)
                .findFirst().orElseThrow(() -> new SystemException("Can not find dispatcher servlet mapping provider."))
                .describeMappings(webApplicationContext)
                .values().stream().flatMap(Collection::stream).toList();
        return ResponseEntity.ok(informationService.mappingInformation(descriptionList));
    }
}
