package poly.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import poly.store.entity.Product;

public interface ProductDAO extends JpaRepository<Product, Integer>{

	/*Lấy ra các sp giống mã loại mà tham số truyền vào*/
	@Query("SELECT p FROM Product p WHERE p.category.id=?1")
	List<Product> findByCategoryId(String cid);
}
