package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>
{

    List<Category> findByCategoryId(Integer categoryId);
}
