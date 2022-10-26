package wiki.lever.integration.controller.test.param;

/**
 * 2022/10/27 00:43:31
 *
 * @author yue
 */
public record InformationRecordParam(
        Byte byteValue, Short shortValue, Integer intValue, Long longValue, String stringValue, Float floatValue,
        Double doubleValue, Boolean booleanValue,
        byte primitiveByteValue, short primitiveShortValue, int primitiveIntValue, long primitiveLongValue,
        float primitiveFloatValue, double primitiveDoubleValue, boolean primitiveBooleanValue
) {
}
