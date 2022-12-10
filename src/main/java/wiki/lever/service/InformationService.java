package wiki.lever.service;

import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import wiki.lever.modal.param.information.MappingInformation;

import java.util.List;

/**
 * 2022/10/29 19:30:38
 *
 * @author yue
 */
public interface InformationService {

    MappingInformation mappingInformation(List<DispatcherServletMappingDescription> descriptionList);
}
