package wiki.lever.integration.controller.test.param;

import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationModule;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationOption;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 2022/10/29 22:13:14
 *
 * @author yue
 */
class JsonTest {
    @Test
    void name() {
        OptionPreset optionPreset = new OptionPreset(
                Option.SCHEMA_VERSION_INDICATOR,
                Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT,
                Option.DEFINITIONS_FOR_MEMBER_SUPERTYPES,
                Option.NULLABLE_FIELDS_BY_DEFAULT,
                Option.DEFINITION_FOR_MAIN_SCHEMA,
                Option.NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS,
                Option.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS,
                Option.SIMPLIFIED_ENUMS,
                Option.SIMPLIFIED_OPTIONALS,
                Option.DEFINITIONS_FOR_ALL_OBJECTS,
                Option.ALLOF_CLEANUP_AT_THE_END
        );
        SchemaGeneratorConfig config = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, optionPreset)
                .with(new JacksonModule(JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE))
                .with(new JakartaValidationModule(JakartaValidationOption.NOT_NULLABLE_FIELD_IS_REQUIRED))
                .build();
        SchemaGenerator schemaGenerator = new SchemaGenerator(config);
        SchemaBuilder schemaBuilder = schemaGenerator.buildMultipleSchemaDefinitions();
        schemaBuilder.createSchemaReference(Hellp.class);
        schemaBuilder.createSchemaReference(HelloRecorde.class);
        schemaBuilder.createSchemaReference(TestN.class);
        System.out.println(schemaBuilder.collectDefinitions("$defs").toPrettyString());
        System.out.println(schemaGenerator.generateSchema(Hellp.class).toPrettyString());
    }
}

@Data
class Hellp {

    @NotNull
    private String hello;

    private List<HelloRecorde> recordes;

}

record HelloRecorde(
        String hello
) {

}

record TestN(
        Integer box
) {
}