package edu.sru.cpsc.webshopping.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.cpsc.webshopping.domain.billing.Purchase;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
	List<Purchase> findByBuyerId(long buyerId);

}
