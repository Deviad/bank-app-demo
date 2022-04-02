package account.infrastructure

import account.TestSuiteSpecification
import spock.lang.Specification

import static account.utils.TestUtils.compressString

class PayloadConverterTest extends TestSuiteSpecification {

    PayloadConverter payloadConverter

    void setup() {

        payloadConverter = new PayloadConverter()
    }

    void "ConvertToDatabaseColumn"() {

        given:
        def expected = compressString("""
            {
                "id": "test",
                "password": "password"
            }
        """);

        def map = MappingUtils.deserialize(expected)
         when:
        def result = compressString(payloadConverter.convertToDatabaseColumn(map))
        then:
        expected == result;

    }

    void "ConvertToEntityAttribute"() {
        given:
        def map = [id: "1", password: "password"]
        def serMap = MappingUtils.serialize(map)
        when:
        var result = payloadConverter.convertToEntityAttribute(serMap)
        then:
        map == result
    }
}
