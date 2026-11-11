package ru.seventech.service.requests.service.utils;

import java.util.List;

public interface MdmEntityToFetchFilterConverterService {

    /** Метод для создания структуры {@code List<Object>} из класса для взаимодействия с МДМ апи 2.0.
     *  Суть - имена всех полей класса собрать в структуру для работы с МДМ 2.0
     *
     * @param mdmEntity преобразуемый класс
     * @param deepRefs глубина раскрытия ссылочных полей
     * @param deepDetails глубина раскрытия полей детализаций<br><br>
     *
     * {@code deepRefs} / {@code deepDetails} = 0 -> в fetchFields войдут только поля {@code mdmEntity}<br>
     * {@code deepRefs} / {@code deepDetails} = 1 -> результат прошлого пункта + поля вложенных классов (детализации / ref поля)<br>
     * {@code deepRefs} / {@code deepDetails} = 2 -> результат прошлого пункта + поля вложенных классов, принадлежащие детализациям и ref ссылкам
     */
    List<Object> convert(Class mdmEntity, int deepRefs, int deepDetails);

}
