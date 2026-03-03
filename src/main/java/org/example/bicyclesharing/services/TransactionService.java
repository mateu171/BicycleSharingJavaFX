package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.TransactionRepository;

public class TransactionService extends BaseService<Transaction, UUID>{

  private final TransactionRepository transactionRepository;

  public TransactionService(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  @Override
  protected Repository<Transaction, UUID> getRepository() {
    return transactionRepository;
  }

  public List<Transaction> getByUserId(UUID userId)
  {
    return transactionRepository.findByUserId(userId);
  }
}
