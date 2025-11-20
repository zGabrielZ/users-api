package br.com.gabrielferreira.users.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageTranslate {

    private PageTranslate() {}

    public static Pageable toPageable(Pageable pageable, HashMap<String, String> fieldsMapping) {
        List<Sort.Order> sorts = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            Sort.Direction direction = order.getDirection();

            if (fieldsMapping.containsKey(property)) {
                String mappedProperty = fieldsMapping.get(property);
                Sort.Order newSort = new Sort.Order(direction, mappedProperty);
                sorts.add(newSort);
            }
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sorts));
    }

    public static HashMap<String, String> getProjectPageableFieldsMapping() {
        HashMap<String, String> fieldsMapping = new HashMap<>();
        // DTO - Entity
        fieldsMapping.put("projectExternalId", "projectExternalId");
        fieldsMapping.put("name", "name");
        fieldsMapping.put("createdAt", "createdAt");
        return fieldsMapping;
    }
}
