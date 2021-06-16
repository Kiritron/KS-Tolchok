# КС Толчок

### Версия: _4.1 Бета_
##### Описание
Стандарт конфигов от Киритрон'с Спэйс. Используется в продуктах КС. Можно использовать как метод хранения данных, а не только для создания и чтения конфигураций. В приложениях КС файлы конфигураций, которые созданы библиотекой КС Толчок, имеют формат .TOLF, в свою очередь являющемся идентификатором. Отсюда данные, генерируемые библиотекой КС Толчок, именуются данными формата TOLF.

Пример данных
```
<#!--
    [Категория]
        - Параметр1: Значение;
        - Параметр2: Значение;
    [/Категория]
--!#>
```

КС Толчок — это гибкий стандарт конфигурационных файлов(и, повторюсь, данных), который позволяет писать в названиях параметров и в значении самих параметров всё что угодно(кроме служебных знаков, используюемых для парсинга содержимого TOLF данных), не переживая, что что-то сломается. Жесткие требования к форматированию, дают гарантию, что TOLF данные будут выглядеть +/- одинаково и будут понятными для пользователя, что даёт ему возможность отредактировать эти данные вручную.

##### Использование
У данной библиотеки есть JavaDoc по [этой ссылке](https://cdn.kiritron.space/javadoc/tolchok/), в которой я пусть и лениво, но написал, как использовать функции библиотеки. JavaDoc обновляется с каждым обновлением библиотеки.

##### Добавление JavaDoc на примере IntelliJ IDEA
1. Откройте окно Project Structure...(CTRL+ALT+SHIFT+S)
2. Перейдите в раздел Libraries
3. Там, где вы подключили библиотеку КС Толчок найдите кнопку похожую на плюсик с глобусом. Нажмите.
4. Откроется форма всего с одним полем для ввода. Введите "https://cdn.kiritron.space/javadoc/tolchok/" без кавычек. Подтвердите действие.
5. Готово. Методы, которые вы будете использовать из Толчок(да, это странно звучит), теперь документированы.

#### Приятного пользования!
