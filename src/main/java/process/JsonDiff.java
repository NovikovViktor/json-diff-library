package process;

import com.google.gson.*;
import data.DiffDataDto;
import data.OperationType;
import data.ParseWay;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    private static final String PATH_DELIMITER = "/";

    /**
     * Наименование поля идентификатора
     **/
    private static String idFieldName = "id";

    private static ParseWay parseWay;

    public static List<DiffDataDto> diffJsonString(String leftJson, String rightJson, String idName) {
        idFieldName = idName;
        return diffJsonString(leftJson, rightJson);
    }

    public static List<DiffDataDto> diffJsonString(String leftJson, String rightJson) {

        List<DiffDataDto> resultList = new ArrayList<>();

        JsonObject leftJsonObject = stringToJson(leftJson);
        JsonObject rightJsonObject = stringToJson(rightJson);

        //TODO(вынести часть методов в другой класс с protected доступом)
        parseWay = ParseWay.STRAIGHT;
        check(leftJsonObject, rightJsonObject, PATH_DELIMITER, resultList);
        parseWay = ParseWay.REVERSE;
        check(rightJsonObject, leftJsonObject, PATH_DELIMITER, resultList);
        return resultList;
    }

    private static JsonObject stringToJson(String str) {
        str = str == null ? EMPTY_JSON : str;
        return JsonParser.parseString(str).getAsJsonObject();
    }

    private static void check(JsonObject leftJsonObject, JsonObject rightJsonObject, String path, List<DiffDataDto> resultList) {

        for (String key : rightJsonObject.keySet()) {
            JsonElement rightJsonElement = getJsonElementByKey(rightJsonObject, key);
            JsonElement leftJsonElement = getJsonElementByKey(leftJsonObject, key);

            checkTypeJsonElement(leftJsonElement, rightJsonElement, path, key, resultList);
        }
    }

    private static void checkTypeJsonElement(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {
        if (rightJsonElement.isJsonArray()) {
            JsonHandler.jsonArray(leftJsonElement, rightJsonElement, path, key, resultList);

        } else if (rightJsonElement.isJsonPrimitive()) {
            JsonHandler.jsonPrimitive(leftJsonElement, rightJsonElement, path, key, resultList);

        } else if (rightJsonElement.isJsonObject()) {
            JsonHandler.jsonObject(leftJsonElement, rightJsonElement, path, key, resultList);

        }
    }

    private static JsonElement getJsonElementByKey(JsonObject jsonObject, String key) {
        if (jsonObject != null) {
            return jsonObject.get(key);
        }
        return null;
    }

    private static JsonArray getJsonArray(JsonElement jsonElement) {
        JsonArray jsonArray = null;
        if (jsonElement != null) {
            jsonArray = jsonElement.getAsJsonArray();
        }
        return jsonArray;
    }

    private static JsonObject getJsonObject(JsonElement jsonElement) {
        JsonObject jsonObject = null;
        if (jsonElement != null) {
            jsonObject = jsonElement.getAsJsonObject();
        }
        return jsonObject;
    }

    private static String checkOperationByWay() {
        String operationStr;

        if (parseWay == ParseWay.STRAIGHT) {
            operationStr = OperationType.ADD.op;
        } else operationStr = OperationType.REMOVE.op;

        return operationStr;
    }

    private static List<JsonObject> getJsonObjectList(JsonArray jsonArray) {
        //TODO(рассмотреть возможность параллельного стрима)
        return StreamSupport
                .stream(jsonArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toList());
    }

    private static Object getJsonPrimitive(JsonPrimitive primitive) {
        if (primitive == null) {
            return null;

        } else if (primitive.isBoolean()) {
            return primitive.getAsBoolean();

        } else if (primitive.isString()) {
            return primitive.getAsString();

        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();

        } else return null;
    }

    private static DiffDataDto formResult(String operation, String path, Object value, Object oldValue) {
        return DiffDataDto.builder()
                .value(value)
                .oldValue(oldValue)
                .path(path)
                .operation(operation)
                .build();
    }

    private static class JsonHandler {

        private static void jsonObject(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {

            JsonObject leftJsonObject = getJsonObject(leftJsonElement);
            JsonObject rightJsonObject = getJsonObject(rightJsonElement);

            check(leftJsonObject, rightJsonObject, path + key + PATH_DELIMITER, resultList);
        }

        private static void jsonPrimitive(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {
            Object leftValue = null;

            if (leftJsonElement != null && leftJsonElement.isJsonPrimitive()) {
                leftValue = getJsonPrimitive(leftJsonElement.getAsJsonPrimitive());
            }

            Object rightValue = getJsonPrimitive(rightJsonElement.getAsJsonPrimitive());

            if (parseWay.equals(ParseWay.STRAIGHT) && !rightValue.equals(leftValue) && leftValue != null) {
                resultList.add(
                        formResult(OperationType.REPLACE.op, path + key, rightValue, leftValue)
                );
            } else if (leftValue == null) {
                resultList.add(
                        formResult(checkOperationByWay(), path + key, rightValue, null)
                );
            }
        }

        private static void jsonArray(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {

            JsonArray leftJsonArray = getJsonArray(leftJsonElement);
            JsonArray rightJsonArray = getJsonArray(rightJsonElement);

            List<JsonObject> leftJsonObjectList = null;
            List<JsonObject> rightJsonObjectList = getJsonObjectList(rightJsonArray);

            if (leftJsonArray != null) {
                leftJsonObjectList = getJsonObjectList(leftJsonArray);
            }

            for (JsonObject rightJsonObject : rightJsonObjectList) {

                JsonElement rightId = rightJsonObject.get(idFieldName);
                JsonObject leftObject = null;
                if (leftJsonObjectList != null) {
                    //TODO("убрать в метод")

                    leftObject = leftJsonObjectList
                            .stream()
                            .filter(it -> rightId.equals(it.get(idFieldName)))
                            .findFirst()
                            .orElse(null);
                }

                if (leftObject != null) {
                    check(leftObject, rightJsonObject, path + key + PATH_DELIMITER, resultList);
                } else {
                    for (String rightKey : rightJsonObject.keySet()) {

                        JsonElement rightElement = getJsonElementByKey(rightJsonObject, rightKey);
                        //TODO("убрать в метод")
                        Object value = null;
                        if (rightElement.isJsonPrimitive()) {
                            value = getJsonPrimitive(rightElement.getAsJsonPrimitive());
                        } else if (rightElement.isJsonObject()) {
                            value = rightElement.getAsJsonObject();
                        } else continue;
                        //TODO("убрать в метод")
                        resultList.add(
                                formResult(checkOperationByWay(), path + key + PATH_DELIMITER + rightKey, value, null)
                        );
                    }
                }
            }
        }
    }
}
