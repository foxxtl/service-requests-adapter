package ru.seventech.service.requests.service.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import ru.seventech.service.requests.dto.annotation.FetchFieldName;
import ru.seventech.service.requests.dto.annotation.NotFetch;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MdmEntityToFetchFilterConverterServiceImpl implements MdmEntityToFetchFilterConverterService {

    private static final List<String> RESERVED_FIELDS_NAME = List.of("class", "mdmCatalog");


    @Override
    public List<Object> convert(Class mdmEntity, int deepRefs, int deepDetails) {
        return internalConvert(mdmEntity, deepRefs, deepDetails);
    }

    private List<Object> internalConvert(Class mdmEntity, int deepRefs, int deepDetails) {
        List<Object> result = new ArrayList<>();

        PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(mdmEntity);
        for (PropertyDescriptor p : pd) {

            if (isNotFetchField(mdmEntity, p)) {
                continue;
            }

            Class<?> type = p.getPropertyType();
            String fieldName = p.getName();

            if (!RESERVED_FIELDS_NAME.contains(fieldName)) {
                String annotationFieldName = getNameByAnnotation(mdmEntity, fieldName);
                String resultFieldName = Objects.nonNull(annotationFieldName) ? annotationFieldName : fieldName;

                if (isDetailField(type)) {
                    if (deepDetails > 0) {
                        Class detailClass = getGenericInfoForField(mdmEntity, fieldName);
                        if (Objects.nonNull(detailClass)) {
                            if (detailClass.isEnum()) {
                                // если поле объявлено как List<enum>, то в БД это тип JSONB и запрашивать его надо как обычное поле
                                result.add(resultFieldName);
                            } else {
                                List<Object> detailFields =
                                        new ArrayList<>(internalConvert(detailClass, deepRefs, deepDetails - 1));
                                result.add(Map.of(resultFieldName, detailFields));
                            }
                        }
                    }
                } else if (isComplexClass(type) && deepRefs > 0) {
                    List<Object> fields = new ArrayList<>(internalConvert(type, deepRefs - 1, deepDetails));
                    result.add(Map.of(resultFieldName, fields));
                } else {
                    result.add(resultFieldName);
                }
            }
        }
        return result;
    }

    private String getNameByAnnotation(Class clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (Objects.nonNull(field)) {
            if (field.isAnnotationPresent(FetchFieldName.class)) {
                final FetchFieldName annotation = field.getAnnotation(FetchFieldName.class);
                return annotation.name();
            } else if (field.isAnnotationPresent(JsonProperty.class)) {
                final JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                return annotation.value();
            }
        }
        return null;
    }

    private boolean isDetailField(Class<?> fieldType) {
        return Collection.class.isAssignableFrom(fieldType);
    }

    /**
     * Определяет, является ли класс сложным классом (у которого внутри тоже есть поля) <br>
     * В нашем случае это всегда isMdmEntity, но могут вылезти еще кейсы
     */
    private boolean isComplexClass(Class<?> fieldType) {
        return isMdmEntity(fieldType);
    }

    /**
     * Все mdm сущности реализуют интерфейс Uuidable
     */
    private boolean isMdmEntity(Class<?> fieldType) {
        return Uuidable.class.isAssignableFrom(fieldType);
    }

    /**
     * Поле, отмеченное аннотацией @NotFetch, не передается в mdm в качестве fetchFields
     */
    private boolean isNotFetchField(Class<?> clazz, PropertyDescriptor pd) {
        try {
            Field field = clazz.getDeclaredField(pd.getName());
            if (field.isAnnotationPresent(NotFetch.class)) {
                return true;
            }
        } catch (NoSuchFieldException ignored) {
            // Поле может отсутствовать, если свойство создано через кастомный getter
        }

        Method readMethod = pd.getReadMethod();
        return Objects.nonNull(readMethod) && readMethod.isAnnotationPresent(NotFetch.class);
    }

    private Field getField(Class clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.warn("Can`t find field {} in class {}", fieldName, clazz.getName());
        }
        return null;
    }

    private Class getGenericInfoForField(Class clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (Objects.nonNull(field)) {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                return (Class) pt.getActualTypeArguments()[0];
            } else {
                return field.getClass();
            }
        }
        return null;
    }
}
