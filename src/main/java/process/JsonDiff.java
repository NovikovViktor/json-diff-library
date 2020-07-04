package process;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import data.DiffDataDto;
import data.OperationType;
import data.ParseWay;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final String ID_FIELD_NAME = "id";

    private static ParseWay parseWay = ParseWay.STRAIGHT;

    public static String diffJson(String leftJson, String rightJson) {

        List<DiffDataDto> resultList = new ArrayList<>();


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

            Optional<JsonArray> leftJsonArray = getJsonArray(leftJsonElement);
            Optional<JsonArray> rightJsonArray = getJsonArray(rightJsonElement);

            Process.jsonArrayProcess(leftJsonArray.orElse(null), rightJsonArray.get(), path, key, resultList);

        } else if (rightJsonElement.isJsonPrimitive()) {

            Process.jsonPrimitiveProcess(leftJsonElement, rightJsonElement, path, key, resultList);

        } else if (rightJsonElement.isJsonObject()) {


        }
    }


    private static JsonElement getJsonElementByKey(JsonObject jsonObject, String key) {
        return jsonObject.get(key);
    }

    private static Optional<JsonArray> getJsonArray(JsonElement jsonElement) {
        JsonArray jsonArray = null;
        if (jsonElement != null) {
            jsonArray = jsonElement.getAsJsonArray();
        }
        return Optional.ofNullable(jsonArray);
    }

    private static String checkOperationByWay() {
        String operationStr;

        if (parseWay == ParseWay.STRAIGHT) {
            operationStr = OperationType.ADD.op;
        } else operationStr = OperationType.REMOVE.op;

        return operationStr;
    }

    private static List<JsonObject> getJsonObjectList(JsonArray jsonArray) {
        return Stream
                .of(jsonArray)
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

    private static class Process {

        private static void jsonPrimitiveProcess(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {
            Object leftValue = null;

            if (leftJsonElement != null && leftJsonElement.isJsonPrimitive()) {
                leftValue = getJsonPrimitive(leftJsonElement.getAsJsonPrimitive());
            }

            Object rightValue = getJsonPrimitive(rightJsonElement.getAsJsonPrimitive());

            if (parseWay == ParseWay.STRAIGHT && rightValue != leftValue && leftValue != null) {
                resultList.add(
                        formResult(OperationType.REPLACE.op, path + key, rightValue, leftValue)
                );
            } else if (leftValue == null) {
                resultList.add(
                        formResult(checkOperationByWay(), path + key, rightValue, null)
                );
            }
        }

        private static void jsonArrayProcess(JsonArray leftJsonArray, JsonArray rightJsonArray, String path, String key, List<DiffDataDto> resultList) {
            List<JsonObject> leftJsonObjectList = null;
            List<JsonObject> rightJsonObjectList = getJsonObjectList(rightJsonArray);

            if (leftJsonArray != null) {
                leftJsonObjectList = getJsonObjectList(leftJsonArray);
            }

            for (JsonObject rightJsonObject : rightJsonObjectList) {

                JsonElement rightId = rightJsonObject.get(ID_FIELD_NAME);
                JsonObject leftObject = null;
                if (leftJsonObjectList != null) {
                    //TODO("убрать в метод")
                    leftObject = leftJsonObjectList
                            .stream()
                            .filter(it -> rightId == it.get(ID_FIELD_NAME))
                            .findFirst()
                            .orElse(null);
                }

                if (leftObject != null) {
                    check(leftObject, rightJsonObject, path + key + PATH_DELIMITER, resultList);
                } else {
                    for (String rightKey : rightJsonObject.keySet()) {
                        JsonElement rightElement = getJsonElementByKey(rightJsonObject, rightKey);

                        //TODO("убрать в метод")
                        Object value;
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
