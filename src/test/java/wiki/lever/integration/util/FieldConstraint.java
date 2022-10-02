package wiki.lever.integration.util;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * 2022/10/2 20:53:40
 *
 * @author yue
 */
public class FieldConstraint {

    /**
     * The field must be required;
     */
    public static final Attributes.Attribute REQUIRE = key("require").value("true");

    /**
     * The field has other constraints.
     */
    public static final Attributes.AttributeBuilder CONSTRAINTS = key("constraints");

    /**
     * Error description.
     */
    public static final String ERROR_DESCRIPTION = "description";

    /**
     * Error stack.
     */
    public static final String ERROR = "error";

}
