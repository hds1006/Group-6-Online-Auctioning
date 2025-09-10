package edu.sru.cpsc.webshopping.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.widgets.Widget;
import edu.sru.cpsc.webshopping.domain.widgets.WidgetAttribute;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import jakarta.persistence.criteria.*;

/**
 * This service is responsible for the pagination 
 * of the market listings on the browse page.
 * @author Wolfgang, Assistant
 */
@Service
public class MarketListingService {

    Collection<Boolean> isDeleted = Arrays.asList(Boolean.TRUE);
    Collection<Long> quantityZero = Arrays.asList(Long.valueOf(0));
    double startPrice = 0;
    double endPrice = Double.MAX_VALUE;
    long startQuantity = 1;
    long endQuantity = Long.MAX_VALUE;
    
    @Autowired
    MarketListingRepository marketListingRepository;

    public Page<MarketListing> findPage(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, 4);
        return marketListingRepository.findAllByIsDeletedNotInAndQtyAvailableNotIn(isDeleted, quantityZero, pageable);
    }
    
    public Page<MarketListing> findPageWithFilter(int pageNum, Map<String, String> allParams, ArrayList<Object> values) {
        Pageable pageable = PageRequest.of(pageNum - 1, 4);
        double startPrice;
        double endPrice;
        long startQuantity;
        long endQuantity;
        
        if(allParams.get("startPrice") == "") {
            startPrice = this.startPrice;
            values.add("");
        }
        else {
            startPrice = Double.valueOf(allParams.get("startPrice"));
            values.add(startPrice);
        }       
        
        if(allParams.get("endPrice") == "") {
            endPrice = this.endPrice;
            values.add("");
        }
        else {
            endPrice = Double.valueOf(allParams.get("endPrice"));
            values.add(endPrice);
        }   
        
        if(allParams.get("startQty") == "") {
            startQuantity = this.startQuantity;
            values.add("");
        }
        else {
            startQuantity = Long.valueOf(allParams.get("startQty"));
            values.add(startQuantity);
        }   
        
        if(allParams.get("endQty") == "") {
            endQuantity = this.endQuantity;
            values.add("");
        }
        else {
            endQuantity = Long.valueOf(allParams.get("endQty"));
            values.add(endQuantity);
        }   
        
        return marketListingRepository
                .findAllByIsDeletedNotInAndQtyAvailableNotInAndPricePerItemBetweenAndQtyAvailableBetween(isDeleted,
                        quantityZero, startPrice, endPrice, startQuantity, endQuantity, pageable);
    }

    public Page<MarketListing> findPageWithAttributeFilter(int pageNum, String year, String condition, String make, String model_car, String keywords, String engine) {
        Pageable pageable = PageRequest.of(pageNum - 1, 4);

        Specification<MarketListing> spec = (Root<MarketListing> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.not(root.get("isDeleted").in(isDeleted)));
            predicates.add(cb.not(root.get("qtyAvailable").in(quantityZero)));

            if (year != null && !year.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Year", year));
            }
            if (condition != null && !condition.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Condition", condition));
            }
            if (make != null && !make.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Make", make));
            }
            if (model_car != null && !model_car.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Model_car", model_car));
            }
            if (keywords != null && !keywords.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Keywords", keywords));
            }
            if (engine != null && !engine.isEmpty()) {
                predicates.add(createAttributePredicate(cb, query, root, "Engine", engine));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return marketListingRepository.findAll(spec, pageable);
    }

    private Predicate createAttributePredicate(CriteriaBuilder cb, CriteriaQuery<?> query, Root<MarketListing> root, String attributeKey, String attributeValue) {
        Subquery<MarketListing> subquery = query.subquery(MarketListing.class);
        Root<MarketListing> subRoot = subquery.from(MarketListing.class);
        Join<Object, Object> subWidgetJoin = subRoot.join("widgetSold");
        Join<Object, Object> subAttributeJoin = subWidgetJoin.join("widgetAttributes");

        subquery.select(subRoot)
                .where(cb.and(
                    cb.equal(subRoot, root),
                    cb.equal(subAttributeJoin.get("attributeKey"), attributeKey),
                    cb.like(subAttributeJoin.get("value"), "%" + attributeValue + "%")
                ));

        return cb.exists(subquery);
    }

    public List<String> getUniqueAttributeValues(String attributeKey) {
        List<String> values = marketListingRepository.findDistinctAttributeValues(attributeKey);
        return sortAttributeValues(attributeKey, values);
    }

    public Map<String, List<String>> getAllUniqueAttributeValues() {
        List<String> attributeKeys = Arrays.asList("Year", "Condition", "Make", "Model_car", "Keywords", "Engine");
        Map<String, List<String>> result = new HashMap<>();
        for (String key : attributeKeys) {
            result.put(key, getUniqueAttributeValues(key));
        }
        return result;
    }

    public Map<String, List<String>> getDependentAttributeValues(Map<String, String> selectedFilters) {
        Map<String, List<String>> dependentValues = new HashMap<>();
        List<String> attributeKeys = Arrays.asList("Year", "Condition", "Make", "Model_car", "Keywords", "Engine");

        Specification<MarketListing> baseSpec = (Root<MarketListing> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.not(root.get("isDeleted").in(isDeleted)));
            predicates.add(cb.not(root.get("qtyAvailable").in(quantityZero)));

            Join<MarketListing, Widget> widgetJoin = root.join("widgetSold");

            for (Map.Entry<String, String> entry : selectedFilters.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (attributeKeys.contains(entry.getKey())) {
                        Join<Widget, WidgetAttribute> attributeJoin = widgetJoin.join("widgetAttributes");
                        predicates.add(cb.and(
                            cb.equal(attributeJoin.get("attributeKey"), entry.getKey()),
                            cb.equal(attributeJoin.get("value"), entry.getValue())
                        ));
                    } else {
                        predicates.add(cb.equal(widgetJoin.get(entry.getKey()), entry.getValue()));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<MarketListing> filteredListings = marketListingRepository.findAll(baseSpec);

        for (String key : attributeKeys) {
            List<String> values = filteredListings.stream()
                .map(MarketListing::getWidgetSold)
                .flatMap(widget -> widget.getWidgetAttributes().stream())
                .filter(wa -> wa.getAttributeKey().equals(key))
                .map(WidgetAttribute::getValue)
                .filter(value -> value != null && !value.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

            dependentValues.put(key, sortAttributeValues(key, values));
        }

        return dependentValues;
    }
    
    public Map<String, List<String>> getUpdatedAttributeValues(Map<String, String> selectedFilters, String changedAttribute) {
        Map<String, List<String>> dependentValues = new HashMap<>();
        List<String> attributeKeys = Arrays.asList("Year", "Condition", "Make", "Model_car", "Keywords", "Engine");

        Specification<MarketListing> baseSpec = (Root<MarketListing> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.not(root.get("isDeleted").in(isDeleted)));
            predicates.add(cb.not(root.get("qtyAvailable").in(quantityZero)));

            Join<MarketListing, Widget> widgetJoin = root.join("widgetSold");

            for (Map.Entry<String, String> entry : selectedFilters.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty() && !entry.getKey().equals(changedAttribute)) {
                    if (attributeKeys.contains(entry.getKey())) {
                        Join<Widget, WidgetAttribute> attributeJoin = widgetJoin.join("widgetAttributes");
                        predicates.add(cb.and(
                            cb.equal(attributeJoin.get("attributeKey"), entry.getKey()),
                            cb.equal(attributeJoin.get("value"), entry.getValue())
                        ));
                    } else {
                        predicates.add(cb.equal(widgetJoin.get(entry.getKey()), entry.getValue()));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<MarketListing> filteredListings = marketListingRepository.findAll(baseSpec);

        for (String key : attributeKeys) {
            if (!key.equals(changedAttribute)) {
                List<String> values = filteredListings.stream()
                    .map(MarketListing::getWidgetSold)
                    .flatMap(widget -> widget.getWidgetAttributes().stream())
                    .filter(wa -> wa.getAttributeKey().equals(key))
                    .map(WidgetAttribute::getValue)
                    .filter(value -> value != null && !value.trim().isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

                dependentValues.put(key, sortAttributeValues(key, values));
            }
        }

        return dependentValues;
    }

    private List<String> sortAttributeValues(String attributeKey, List<String> values) {
        if ("Year".equals(attributeKey)) {
            Collections.sort(values, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.valueOf(o2).compareTo(Integer.valueOf(o1));
                }
            });
        } else if ("Engine".equals(attributeKey)) {
            Collections.sort(values, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Double.compare(parseEngineSize(o1), parseEngineSize(o2));
                }
            });
        } else {
            Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        }
        return values;
    }

    private double parseEngineSize(String engineSize) {
        String numericPart = engineSize.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            return 0.0; // Default value if parsing fails
        }
    }

	public Page<MarketListing> searchMarketListings(String searchTerm, int pageNumber) {
		PageRequest pageable = PageRequest.of(pageNumber - 1, 12);

		return marketListingRepository.findByWidgetSoldName(searchTerm, searchTerm, searchTerm, pageable);
	}
}


