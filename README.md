### **КС Толчок**

##### Версия: 2.1
##### Описание
Стандарт конфигов от Киритрон'с Спэйс. Используется в продуктах КС. Можно использовать как метод хранения данных, а не только для создания и чтения конфигураций. В приложениях КС файлы конфигураций, которые созданы библиотекой КС Толчок, имеют формат .TOLF, в свою очередь являющемся идентификатором. Отсюда данные, генерируемые библиотекой КС Толчок, именуются данными формата TOLF.

Пример данных
```
</
    [Категория]
        - Параметр1: Значение;
        - Параметр2: Значение;
    [/Категория]
/>
```
##### Использование
Импортируйте методы из класса _TOLF_Handler_
```java
import static space.kiritron.tolchok.TOLF_Handler.*;
```

Используйте метод _GenData_, чтобы сгенерировать данные 
```java
GenData("Категория", "Имя первого поля", "Его значение");
```
Обратите внимание, что в данном случае будут сгенерированы данные только с одним параметром. Чтобы добавить параметр в существующие данные, используйте метод _AddFieldToData_
```java
AddFieldToData("Данные TOLF", "Категория, в которой нужной добавить строку", "Имя нового параметра", "Его значение");
```
однако это экспериментальный метод и мы не уверены, что он работает правильно.
Чтобы изменить значение поля/параметра, используйте метод _EditParamInData_
```java
EditParamInData("Данные TOLF", "Категория", "Имя уже имеющегося параметра", "Его новое значение");
```
Чтобы прочитать значение из параметра, используйте метод _ReadParamFromData_
```java
ReadParamFromData("Данные TOLF", "Категория", "Имя параметра, из которого нужно получить значение");
```
Ответом всех этих методов будет String с данными TOLF, а в случае с _ReadParamFromData_ будет полученное значение параметра. В случае ошибок будут коды ошибок.

#### Приятного пользования!
