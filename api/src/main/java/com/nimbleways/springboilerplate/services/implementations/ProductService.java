package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    private  final ProductRepository productRepository;
    private final NotificationService notificationService;


    public ProductService(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }


    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    private void handleSeasonalProduct(Product product) {
        if ((LocalDate.now().isAfter(product.getSeasonStartDate()) && LocalDate.now().isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        }
        else if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    private void handleExpiredProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);

        } else if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
        }
        productRepository.save(product);
    }

    private void handleNormalProduct(Product product){
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

    public void processProduct(Set<Product> products) {
        for (Product p : products) {
            switch (p.getType()) {
                case NORMAL   -> handleNormalProduct(p);
                case SEASONAL  -> handleSeasonalProduct(p);
                case EXPIRABLE -> handleExpiredProduct(p);
            }
        }
    }
}