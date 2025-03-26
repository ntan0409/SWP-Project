package com.example.Child.Growth.Tracking.Repository;

import com.example.Child.Growth.Tracking.Model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);
    
    List<PaymentTransaction> findAllByOrderByPaymentDateDesc();
    
    List<PaymentTransaction> findByStatus(String status);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentDate BETWEEN :startDate AND :endDate")
    List<PaymentTransaction> findByPaymentDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    List<PaymentTransaction> findByBankCode(String bankCode);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.amount BETWEEN :minAmount AND :maxAmount")
    List<PaymentTransaction> findByAmountRange(@Param("minAmount") Long minAmount, @Param("maxAmount") Long maxAmount);
    
    Long countByStatus(String status);
    
    @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.status = 'SUCCESS'")
    Long sumSuccessfulTransactions();
    
    List<PaymentTransaction> findByOrderInfoContaining(String keyword);
    
    boolean existsByTransactionRef(String transactionRef);
    
    @Query("SELECT pt FROM PaymentTransaction pt ORDER BY pt.paymentDate DESC")
    List<PaymentTransaction> findTopNOrderByPaymentDateDesc(@Param("limit") int limit);

    List<PaymentTransaction> findByUserId(Long userId);
} 