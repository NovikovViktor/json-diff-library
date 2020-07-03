package process;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import data.DiffDataDto;
import data.OperationType;
import data.ParseWay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author novikov_vi on 03.07.2020
 */
public class JsonDiff {

    /**
     * Строка хранящая пустой JSON
     **/
    private static final String EMPTY_JSON = "{}";

    /**
     * Начальное состояние строки определяющий путь JSON
     **/
    private static final String START_PATH = "/";

    /**
     * Наименование поля идентификатора
     **/
    private static final String ID_FIELD_NAME = "id";

    public static String diffJson(String leftJson, String rightJson) {

        List<DiffDataDto> resultList = new ArrayList<>();


    }

    private static void check(JsonObject leftJsonObject, JsonObject rightJsonObject, String path, ParseWay parseWay, List<DiffDataDto> resultList) {

        String operation = checkOperationByWay(parseWay);

        rightJsonObject
                .keySet()
                .forEach(key -> {
                            JsonElement rightJsonElement = getJsonElementByKey(rightJsonObject, key);
                            JsonElement leftJsonElement = getJsonElementByKey(rightJsonObject, key);

                        }
                );
    }

    private static void checkTypeJsonElement(JsonElement rightJsonElement, JsonElement leftJsonElement) {

    }

    private static JsonElement getJsonElementByKey(JsonObject jsonObject, String key) {
        return jsonObject.get(key);
    }

    private static String checkOperationByWay(ParseWay parseWay) {
        String operationStr;

        if (parseWay == ParseWay.STRAIGHT) {
            operationStr = OperationType.ADD.op;
        } else operationStr = OperationType.REMOVE.op;

        return operationStr;
    }
}
