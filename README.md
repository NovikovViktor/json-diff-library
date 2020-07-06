# json-diff-library

## Библиотека для нахождения различий в двух JSON

## Changelog

Текущая версия: **1.0.0**

## Описание

Библиотека предназначена для сравнения двух JSON типа String на наличие изменений в значениях или структуре

## Использование

Импортируйте библиотеку в проект:
```xml
<dependency>
    <groupId>ru.novikov.library</groupId>
    <artifactId>json-diff-library</artifactId>
    <version>${library.version}</version>
</dependency>
```

Импортируйте класс JsonDiff и вызовите метод или его перегруженную версию в которой можно указать наименование идентификатора:

```java
import ru.novikov.library.jsonDiff.data.DiffDataDto;
import ru.novikov.library.jsonDiff.process.JsonDiff;

import java.util.List;

public class TestLibrary {

    public static void main(String[] args) {

        String leftJsonStr = args[0];
        String rightJsonStr = args[1];
        String customIdName = args[2];
        
        List<DiffDataDto> firstResult = JsonDiff.diffJsonString(leftJsonStr, rightJsonStr);
        List<DiffDataDto> secondResult = JsonDiff.diffJsonString(leftJsonStr, rightJsonStr, customIdName);
    }
}
```

Класс хранящий информацию о результатах сравнения имеет следующий вид:

```java
public final class DiffDataDto {
    private final String operation;
    private final String path;
    private final Object value;
    private final Object oldValue;
}
```

## Стек
1) lombok
2) gson
3) jUnit