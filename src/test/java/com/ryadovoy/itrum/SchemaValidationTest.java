package com.ryadovoy.itrum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
class SchemaValidationTest {
    @Test
    public void testSchemaValidity() {
    }
}
