package ru.novikov.library.jsonDiff.process;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.novikov.library.jsonDiff.data.DiffDataDto;
import ru.novikov.library.jsonDiff.data.OperationType;
import ru.novikov.library.jsonDiff.data.ParseWay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author novikov_vi on 03.07.2020
 */
public final class JsonDiff {

    /**
     * Начальное состояние строки определяющий путь JSON
     **/
    private static final String PATH_DELIMITER = "/";

    /**
     * Наименование поля идентификатора
     **/
    private static String idFieldName = "id";

    /**
     * Путь по которому происходит сверка изменений
     */
    private static ParseWay parseWay;

    /**
     * Сравнение строковых json на наличие различий с возможностью передачи параметра идентификатора вложенных объектов
     *
     * @param leftJson  - левая строка
     * @param rightJson - правая строка
     * @param idName    - наименование идентификатора вложенных объектов
     * @return - результирующий список  хранящий изменения
     */
    public static List<DiffDataDto> diffJsonString(String leftJson, String rightJson, String idName) {
        idFieldName = idName;
        return diffJsonString(leftJson, rightJson);
    }

    /**
     * Сравнение строковых json на наличие различий
     *
     * @param leftJson  - левая строка
     * @param rightJson - правая строка
     * @return - результирующий список  хранящий изменения
     */
    public static List<DiffDataDto> diffJsonString(String leftJson, String rightJson) {

        List<DiffDataDto> resultList = new ArrayList<>();

        JsonObject leftJsonObject = JsonUtil.stringToJson(leftJson);
        JsonObject rightJsonObject = JsonUtil.stringToJson(rightJson);

        parseWay = ParseWay.STRAIGHT;
        check(leftJsonObject, rightJsonObject, PATH_DELIMITER, resultList);
        parseWay = ParseWay.REVERSE;
        check(rightJsonObject, leftJsonObject, PATH_DELIMITER, resultList);
        return resultList;
    }

    /**
     * Функция извлекает элементы из объектов и отдаёт их на определение типа
     */
    private static void check(JsonObject leftJsonObject, JsonObject rightJsonObject, String path, List<DiffDataDto> resultList) {

        for (String key : rightJsonObject.keySet()) {
            JsonElement rightJsonElement = JsonUtil.getJsonElementByKey(rightJsonObject, key);
            JsonElement leftJsonElement = JsonUtil.getJsonElementByKey(leftJsonObject, key);

            checkTypeJsonElement(leftJsonElement, rightJsonElement, path, key, resultList);
        }
    }

    /**
     * Идентификация типа сравниваемого элемента и отправка на обработку в {@link JsonHandler}
     */
    private static void checkTypeJsonElement(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {
        if (rightJsonElement.isJsonArray()) {
            JsonHandler.jsonArray(leftJsonElement, rightJsonElement, path, key, resultList);

        } else if (rightJsonElement.isJsonPrimitive()) {
            JsonHandler.jsonPrimitive(leftJsonElement, rightJsonElement, path, key, resultList);

        } else if (rightJsonElement.isJsonObject()) {
            JsonHandler.jsonObject(leftJsonElement, rightJsonElement, path, key, resultList);

        }
    }

    /**
     * Получение наименования операции определенном по пути сравнения
     *
     * @return - строка хранящая тип операции
     */
    private static String checkOperationByWay() {
        String operationStr;

        if (parseWay == ParseWay.STRAIGHT) {
            operationStr = OperationType.ADD.op;
        } else operationStr = OperationType.REMOVE.op;

        return operationStr;
    }

    /**
     * Сопоставление идентификатора {@link JsonDiff#idFieldName} объекта вложенного массива json с с другим вложенным массивом из json сравнения
     *
     * @param jsonObjectList - список объектов в котором необходимо найти схожий идентификатор
     * @param idElement      - элемент json хранящий информацию об идентификаторе ячейки массива
     * @return - сопоставленный идентификатор (объект json) или null  случае ненахождения
     */
    private static JsonObject matchId(List<JsonObject> jsonObjectList, JsonElement idElement) {
        return jsonObjectList
                .stream()
                .filter(it -> idElement.equals(it.get(idFieldName)))
                .findFirst()
                .orElse(null);
    }

    /**
     * Формирование результат сравнения двух json
     *
     * @return - единица результата
     */
    private static DiffDataDto formResult(String operation, String path, Object value, Object oldValue) {
        return DiffDataDto.builder()
                .value(value)
                .oldValue(oldValue)
                .path(path)
                .operation(operation)
                .build();
    }

    /**
     * Класс-обработчик json в зависимости от типа
     */
    private static final class JsonHandler {

        /**
         * Обработать json типа - объект
         *
         * @param path - путь до значения
         * @param key  - текущее наименование объекта
         */
        private static void jsonObject(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {

            JsonObject leftJsonObject = JsonUtil.getJsonObject(leftJsonElement);
            JsonObject rightJsonObject = JsonUtil.getJsonObject(rightJsonElement);

            check(leftJsonObject, rightJsonObject, path + key + PATH_DELIMITER, resultList);
        }

        /**
         * Обработать json типа - примитив
         *
         * @param path - путь до значения
         * @param key  - текущее наименование примитива
         */
        private static void jsonPrimitive(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {
            Object leftValue = null;

            if (leftJsonElement != null && leftJsonElement.isJsonPrimitive()) {
                leftValue = JsonUtil.getJsonPrimitive(leftJsonElement.getAsJsonPrimitive());
            }

            Object rightValue = JsonUtil.getJsonPrimitive(rightJsonElement.getAsJsonPrimitive());

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

        /**
         * Обработать json типа - массив
         *
         * @param path - путь до значения
         * @param key  - текущее наименование массива
         */
        private static void jsonArray(JsonElement leftJsonElement, JsonElement rightJsonElement, String path, String key, List<DiffDataDto> resultList) {

            JsonArray leftJsonArray = JsonUtil.getJsonArray(leftJsonElement);
            JsonArray rightJsonArray = JsonUtil.getJsonArray(rightJsonElement);

            List<JsonObject> leftJsonObjectList = null;
            List<JsonObject> rightJsonObjectList = JsonUtil.getJsonObjectList(rightJsonArray);

            if (leftJsonArray != null) {
                leftJsonObjectList = JsonUtil.getJsonObjectList(leftJsonArray);
            }

            for (JsonObject rightJsonObject : rightJsonObjectList) {

                JsonElement rightId = rightJsonObject.get(idFieldName);
                JsonObject leftObject = null;

                if (leftJsonObjectList != null) {
                    leftObject = matchId(leftJsonObjectList, rightId);
                }

                if (leftObject != null) {
                    check(leftObject, rightJsonObject, path + key + PATH_DELIMITER, resultList);
                } else {
                    for (String rightKey : rightJsonObject.keySet()) {

                        JsonElement rightElement = JsonUtil.getJsonElementByKey(rightJsonObject, rightKey);
                        Object value = JsonUtil.getValueElement(rightElement);

                        if (value == null) continue;
                        resultList.add(
                                formResult(checkOperationByWay(), path + key + PATH_DELIMITER + rightKey, value, null)
                        );
                    }
                }
            }
        }
    }
}
