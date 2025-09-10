package edu.sru.cpsc.webshopping.repository.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.sru.cpsc.webshopping.domain.products.Product;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long>{
	
}
