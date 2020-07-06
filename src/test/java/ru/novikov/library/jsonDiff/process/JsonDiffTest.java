package ru.novikov.library.jsonDiff.process;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.novikov.library.jsonDiff.data.DiffDataDto;

import java.util.List;

/**
 * Тестирование работы функции выполняющей сравнение json
 * @author novikov_vi on 05.07.2020
 */
public class JsonDiffTest {

    @Test
    public void leftJsonEmptyRightWithData() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(EMPTY_JSON, JSON_1);
        printResult(result);

        assertEquals(result.size(), 8);
    }

    @Test
    public void checkTwoJsonReplaceValue() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(JSON_2, JSON_1);
        printResult(result);

        assertEquals(result.size(), 3);
    }

    @Test
    public void rightJsonEmpty() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(JSON_1, EMPTY_JSON);
        printResult(result);

        assertEquals(result.size(), 8);
    }

    @Test
    public void checkEmbeddedId() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(JSON_3,JSON_4);
        printResult(result);

        assertEquals(result.size(), 4);
    }

    @Test
    public void checkEmptyArrayAdd() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(JSON_5, JSON_6);
        printResult(result);

        assertEquals(result.size(), 3);
    }

    @Test
    public void checkEmptyArrayDelete() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(JSON_6, JSON_5);
        printResult(result);

        assertEquals(result.size(), 3);
    }

    @Test
    public void checkJsonInnerObjects() {
        List<DiffDataDto> result = JsonDiff.diffJsonString(EMPTY_JSON, JSON_7);
        printResult(result);

        assertEquals(result.size(), 4);
    }

    private void printResult(List<DiffDataDto> list) {
        list.forEach(it ->
                System.out.println("(op = " + it.getOperation() + ", path = " + it.getPath() + ", value = " + it.getValue() + ", oldValue = " + it.getOldValue())

        );
    }

    private static final String EMPTY_JSON = "{}";

    private static final String JSON_1 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"LeftsomeName\",\n" +
            "  \"phone\": [\n" +
            "    {\n" +
            "      \"id\": \"11\",\n" +
            "      \"number\": \"раз2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"22\",\n" +
            "      \"number\": \"два2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"33\",\n" +
            "      \"number\": \"три2\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_2 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"LeftsomeName\",\n" +
            "  \"phone\": [\n" +
            "    {\n" +
            "      \"id\": \"11\",\n" +
            "      \"number\": \"раз1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"22\",\n" +
            "      \"number\": \"два1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"33\",\n" +
            "      \"number\": \"три1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_3 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"LeftsomeName\",\n" +
            "  \"email\": [\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"11\",\n" +
            "        \"kek\": 12\n" +
            "      },\n" +
            "      \"someEmail\": \"раз1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"13\",\n" +
            "        \"kek\": 14\n" +
            "      },\n" +
            "      \"someEmail\": \"два1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"15\",\n" +
            "        \"kek\": 16\n" +
            "      },\n" +
            "      \"someEmail\": \"три1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_4 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"LeftsomeName\",\n" +
            "  \"email\": [\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"11\",\n" +
            "        \"kek\": 12\n" +
            "      },\n" +
            "      \"someEmail\": \"раз1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"13\",\n" +
            "        \"kek\": 14\n" +
            "      },\n" +
            "      \"someEmail\": \"два2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"15\",\n" +
            "        \"kek\": 16\n" +
            "      },\n" +
            "      \"someEmail\": \"три2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"16\",\n" +
            "        \"kek\": 17\n" +
            "      },\n" +
            "      \"someEmail\": \"три2\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_5 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"LeftSomeName\",\n" +
            "  \"email\": [\n" +
            "  ]\n" +
            "}";

    private static final String JSON_6 = "{\n" +
            "  \"id\": \"1\",\n" +
            "  \"name\": \"RightSomeName\",\n" +
            "  \"email\": [\n" +
            "    {\n" +
            "      \"id\": {\n" +
            "        \"lol\": \"11\",\n" +
            "        \"kek\": 12\n" +
            "      },\n" +
            "      \"someEmail\": \"раз1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String JSON_7 = "{\n" +
            "  \"dict\" : {\n" +
            "    \"name\" : \"Справочник\",\n" +
            "    \"nick\" : \"nick\",\n" +
            "    \"nsiElements\" : [\n" +
            "      {\n" +
            "        \"id\" : \"1\",\n" +
            "        \"value\" : \"start\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
}