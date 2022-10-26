package wiki.lever.modal.param.information;

/**
 * 2022/10/15 22:37:30
 *
 * @author yue
 */
public record ParameterType(
        String name, Class<?> clazz, MappingInformation.RequestType type
) {

}