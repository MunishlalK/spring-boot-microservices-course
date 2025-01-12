package com.bookstorelabs.catalog.domain;

import com.bookstorelabs.catalog.ApplicationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ApplicationProperties properties;

    public ProductService(ProductRepository productRepository, ApplicationProperties properties) {
        this.productRepository = productRepository;
        this.properties = properties;
    }

    public PageResult<Product> getProducts(int pageNo) {
        Sort sort = Sort.by("name").ascending();
        pageNo = (pageNo <= 1) ? 0 : pageNo -1;
        Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);
        Page<Product> productsPage =
                productRepository.findAll(pageable)
                        .map(ProductMapper::toProduct);

       return new PageResult<>(
                        productsPage.getContent(),
                        productsPage.getTotalElements(),
                        productsPage.getNumber() + 1,
                        productsPage.getTotalPages(),
                        productsPage.isFirst(),
                        productsPage.isLast(),
                        productsPage.hasNext(),
                        productsPage.hasPrevious()
                );

    }

    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code).map(ProductMapper::toProduct);
    }

}
